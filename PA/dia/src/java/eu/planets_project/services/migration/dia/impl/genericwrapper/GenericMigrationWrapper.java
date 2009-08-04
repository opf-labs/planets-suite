package eu.planets_project.services.migration.dia.impl.genericwrapper;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions.MigrationException;
import eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions.MigrationInitialisationException;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GenericMigrationWrapper {

    private PlanetsLogger log = PlanetsLogger
            .getLogger(GenericMigrationWrapper.class);


    private MigrationPaths migrationPaths;

    private ServiceDescription serviceDescription;

    public GenericMigrationWrapper(Document configuration)
            throws MigrationInitialisationException {

        try {
            MigrationPathsFactory pathsFactory = new MigrationPathsFactory();
            migrationPaths = pathsFactory.getMigrationPaths(configuration);
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

        //If called with null parameters, use an empty list instead
        if (toolParameters == null){
            toolParameters = new ArrayList<Parameter>();
        }


        //make workfolder, and reserve filenames in this folder.
        File workfolder = handleTempfiles(migrationPath);

        //handle temp input file
        if (migrationPath.useTempSourceFile()){
            handleTempSourceFile(migrationPath,sourceObject, workfolder);
        }

        File outputFile= null;
        //handle temp output file
        if (migrationPath.useTempDestinationFile()){
            outputFile = handleTempDestinationFile(migrationPath,workfolder);
        }


        String command = migrationPath.getCommandLine(toolParameters);


        try {
            final ProcessRunner toolProcessRunner = new ProcessRunner();

            InputStream processStandardInput = null;
            if (migrationPath.useTempSourceFile()){
                //fine, is alreade written
            } else{
                //serve the file on standard input
                processStandardInput = sourceObject.getContent().read();
            }



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
            byte[] destinationObjectBytes;
            if (migrationPath.useTempDestinationFile()){
                //we should read a temp file afterwards

                destinationObjectBytes = FileUtils
                        .writeInputStreamToBinary(new FileInputStream(
                                migrationPath.getTempOutputFile().getFile()));
                migrationPath.getTempOutputFile().getFile().delete();
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
                //we should read the output
                destinationObjectBytes = FileUtils
                        .writeInputStreamToBinary(toolProcessRunner
                                .getProcessOutput());

                String message = "Successfully migrated object with title '"
                                 + sourceObject.getTitle() + "' from format URI: "
                                 + sourceFormat + " to " + destinationFormat
                                 + " Standard error output: "
                                 + toolProcessRunner.getProcessErrorAsString();
                serviceReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                                                  message);


            }

            //TODO cleanup the dir

            final DigitalObject destinationObject = new DigitalObject.Builder(
                    Content.byValue(destinationObjectBytes)).build();

            return new MigrateResult(destinationObject, serviceReport);

        } catch (IOException ioe) {
            throw new MigrationException("Failed migrating object with title '"
                                         + sourceObject.getTitle() + " from format URI: "
                                         + sourceFormat + " to : " + destinationFormat
                                         + " due to problems while handling temporary files.", ioe);
        }
    }

    private File handleTempDestinationFile(MigrationPath migrationPath,
                                           File workfolder) {
        TempFile outputemp = migrationPath.getTempOutputFile();
        outputemp.setFile(createTemp(workfolder,outputemp));
        return outputemp.getFile();
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
        File workfolder = FileUtils.createWorkFolderInSysTemp(FileUtils.randomizeFileName("hardcodename"));

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
                                             String command, InputStream processStandardInput) {

        toolProcessRunner.setInputStream(processStandardInput);
        toolProcessRunner.setCommand(Arrays.asList("/bin/sh", "-c", command));
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

    // FIXME! KILL
    // private File[] createTempFiles(String humanReadableClue) {
    //
    // final Date now = new Date();
    // final String tempBaseFileName = humanReadableClue + UUID.randomUUID() +
    // now.getTime();
    // final File sourceTempFile = FileUtils.getTempFile(tempBaseFileName,
    // "source");
    // final File destinationTempFile = FileUtils.getTempFile(tempBaseFileName,
    // "destination");
    // return new File[]{sourceTempFile, destinationTempFile};
    // }

    private File createSourceTempFile(DigitalObject sourceObject,
                                      String tempFileBaseName) {

        final File sourceTempFile = FileUtils.getTempFile(tempFileBaseName,
                                                          "source");

        // Write the digital object to the temporary source file.
        FileUtils.writeInputStreamToFile(sourceObject.getContent().read(),
                                         sourceTempFile);

        return sourceTempFile;
    }

    private File createDestinationTempFile(String tempFileBaseName) {

        return FileUtils.getTempFile(tempFileBaseName, "destination");
    }

    private String generateTempFileBaseName(DigitalObject sourceObject,
                                            String humanReadableClue) {

        final Date now = new Date();
        return humanReadableClue + UUID.randomUUID() + now.getTime();
    }
}
