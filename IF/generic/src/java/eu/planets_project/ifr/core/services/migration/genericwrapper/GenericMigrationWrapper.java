package eu.planets_project.ifr.core.services.migration.genericwrapper;

import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.MigrationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.MigrationInitialisationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GenericMigrationWrapper {

    private PlanetsLogger log = PlanetsLogger
            .getLogger(GenericMigrationWrapper.class);


    private MigrationPaths migrationPaths;

    private ServiceDescription serviceDescription;



    private boolean returnByReference;

    public GenericMigrationWrapper(Document configuration, String canonicalName)
            throws MigrationInitialisationException {

        try {
            MigrationPathsFactory pathsFactory = new MigrationPathsFactory();
            migrationPaths = pathsFactory.getMigrationPaths(configuration);
            ServiceDescriptionFactory serviceFactory = new ServiceDescriptionFactory();
            serviceDescription = serviceFactory.getServiceDescription(configuration, migrationPaths.getAsPlanetsPaths(),canonicalName);
            String result = configuration.getDocumentElement().getAttribute(
                    "returnByReference");
            if (result != null){
                returnByReference = Boolean.valueOf(result);
            }


        } catch (Exception e) {
            throw new MigrationInitialisationException(
                    "Failed initialising migration path data from the configuration document: "
                    + configuration.getNodeName(), e);
        }

        //TODO parse and initialise the ServiceDescription
    }

    // FIXME! This method should be able to decide whether it should create
    // temporary files or not.
    /**
     * Migrate the digital object from the sourceFormat to the destination
     * format, with the given parameters
     * @param sourceObject the digital object to migrate
     * @param sourceFormat the format of the digital object
     * @param destinationFormat The
     * @param toolParameters the parameters. If null, is initialised as an
     * empty list.
     * @return the migrateResult for the migration
     * @throws MigrationException if the generic wrapper failed in invoking the
     * tool and migrating the digital object. Tool failures are
     * embedded in the migrateResult.
     */
    public MigrateResult migrate(DigitalObject sourceObject, URI sourceFormat,
                                 URI destinationFormat, List<Parameter> toolParameters)
            throws MigrationException, IOException {

        /*
         * - Validate that the proper parameters are set for the migration path
         * identified by sourceFormat and destinationFormat
         */
        final MigrationPath migrationPath = migrationPaths
                .getMigrationPath(sourceFormat, destinationFormat);

        log.info("Found migrationpath from "+sourceFormat+" to "+destinationFormat);

        //If called with null parameters, use an empty list instead
        if (toolParameters == null){
            log.info("Called with null parameters");
            toolParameters = new ArrayList<Parameter>();
        }


        //make workfolder, and reserve filenames in this folder.
        log.info("Making workfolder for migration");
        File workfolder = handleTempfiles(migrationPath);

        //handle temp input file

        if (migrationPath.useTempSourceFile()){
            log.info("Migrationpath uses temp source file, reading digital object into file");
            handleTempSourceFile(migrationPath,sourceObject, workfolder);
        }


        //handle temp output file
        if (migrationPath.useTempDestinationFile()){
            log.info("Migrationpath uses temp destination path");
            handleTempDestinationFile(migrationPath,workfolder);
        }


        List<String> command = migrationPath.getCommandLine(toolParameters);
        log.info("Command line found: ");
        log.info(command);


        InputStream processStandardInput = null;
        if (!migrationPath.useTempSourceFile()) {
            //serve the file on standard input
            processStandardInput = sourceObject.getContent().read();
        } else {
            //fine, is alreade written
        }

        //Execute the tool
        final ProcessRunner toolProcessRunner = new ProcessRunner();
        ServiceReport serviceReport = executeToolProcess(toolProcessRunner,
                                                         command, processStandardInput);



        if (serviceReport.getType() == Type.ERROR) {
            String message = "Failed migrating object with title '"
                             + sourceObject.getTitle() + "' from format URI: "
                             + sourceFormat + " to " + destinationFormat
                             + " Standard output: "
                             + toolProcessRunner.getProcessOutputAsString()
                             + "\nStandard error output: "
                             + toolProcessRunner.getProcessErrorAsString();
            serviceReport = new ServiceReport(Type.ERROR,
                                              Status.TOOL_ERROR, message);
            return new MigrateResult(null, serviceReport);
        }


        //cleanup
        if (migrationPath.useTempSourceFile()){
            migrationPath.getTempSourceFile().getFile().delete();
        }
        for (TempFile tempFile : migrationPath.getTempFileDeclarations()) {
            tempFile.getFile().delete();
        }


        //READING THE OUTPUT
        //TODO return a reference to the outputfile
        DigitalObject.Builder builder;


        if (migrationPath.useTempDestinationFile()){
            //we should read a temp file afterwards
            File outputfile = migrationPath.getTempOutputFile().getFile();
            if (returnByReference){
                builder = new DigitalObject.Builder(Content.byReference(outputfile));
            } else {
                builder = new DigitalObject.Builder(Content.byValue(outputfile));
                outputfile.delete();
            }


            String message = "Successfully migrated object with title '"
                             + sourceObject.getTitle() + "' from format URI: "
                             + sourceFormat + " to " + destinationFormat
                             + " Standard output: "
                             + toolProcessRunner.getProcessOutputAsString()
                             + "\nStandard error output: "
                             + toolProcessRunner.getProcessErrorAsString();
            serviceReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                                              message);

        } else {

            if (returnByReference){
                //we should read the output
                builder = new DigitalObject.Builder(Content.byReference(toolProcessRunner.getProcessOutput()));
            } else{
                builder = new DigitalObject.Builder(Content.byValue(toolProcessRunner.getProcessOutput()));
            }
            String message = "Successfully migrated object with title '"
                             + sourceObject.getTitle() + "' from format URI: "
                             + sourceFormat + " to " + destinationFormat
                             + " Standard error output: "
                             + toolProcessRunner.getProcessErrorAsString();
            serviceReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                                              message);


        }


        //TODO cleanup the dir
        DigitalObject destinationObject = builder
                .format(destinationFormat)
                .build();

        return new MigrateResult(destinationObject, serviceReport);

    }

    private void handleTempDestinationFile(MigrationPath migrationPath,
                                           File workfolder) {
        TempFile outputemp = migrationPath.getTempOutputFile();
        outputemp.setFile(createTemp(workfolder,outputemp));

    }

    private void handleTempSourceFile(MigrationPath migrationPath,
                                      DigitalObject sourceObject,
                                      File workfolder) throws IOException {
        TempFile sourcetempfile = migrationPath.getTempSourceFile();
        File realtemp = createTemp(workfolder,sourcetempfile);
        FileUtils.writeInputStreamToFile(sourceObject.getContent().read(),realtemp);
        sourcetempfile.setFile(realtemp);
    }

    private File handleTempfiles(MigrationPath migrationPath) {
        log.info("Entering handleTempFiles");

        //if useTempFiles
        //create work folder
        File workfolder = FileUtils.createWorkFolderInSysTemp(FileUtils.randomizeFileName(serviceDescription.getName()));

        log.info("Created workfolder "+ workfolder.getAbsolutePath());

        //for each normal tempfile,
        List<TempFile> tempfiles = migrationPath.getTempFileDeclarations();
        for (TempFile tempfile:tempfiles){
            tempfile.setFile(createTemp(workfolder,tempfile));

        }
        return workfolder;
    }

    private File createTemp(File workfolder, TempFile tempfile){

        String name = tempfile.getRequestedName();
        if ("".equals(name)){
            name = FileUtils.randomizeFileName(tempfile.getCodename());
        }

        File uncreatedfile = new File(workfolder.getAbsolutePath() + File.separator + name);
        log.info("For tempfile "
                 +tempfile.getCodename()
                 +" created tempfile in workfolder: "
                 +uncreatedfile.getAbsolutePath());
        return uncreatedfile;

    }

    /**
     * Get the ServiceDescription for this migrate service
     * @return the serviceDescription
     */
    public ServiceDescription describe(){
        return serviceDescription;
    }

    private ServiceReport executeToolProcess(ProcessRunner toolProcessRunner,
                                             List<String> command, InputStream processStandardInput) {

        toolProcessRunner.setInputStream(processStandardInput);
        toolProcessRunner.setCommand(command);
        toolProcessRunner.setCollection(true);
        toolProcessRunner.setOutputCollectionByteSize(-1);

        toolProcessRunner.run();
        ServiceReport serviceReport;
        boolean toolError = toolProcessRunner.getReturnCode() == -1;
        if (toolError) {
            serviceReport = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                                              toolProcessRunner.getProcessErrorAsString());
        } else {
            serviceReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                                              toolProcessRunner.getProcessOutputAsString());
        }
        return serviceReport;
    }



}
