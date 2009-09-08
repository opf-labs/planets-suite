package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Document;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

public class GenericCLIMigrationWrapper {

    private CliMigrationPaths migrationPaths;

    public GenericCLIMigrationWrapper(Document configuration)
            throws MigrationInitialisationException {

        try {
            CliMigrationPathsFactory pathsFactory = new CliMigrationPathsFactory();
            migrationPaths = pathsFactory.getInstance(configuration);
        } catch (Exception e) {
            throw new MigrationInitialisationException(
                    "Failed initialising migration path data from the configuration document: "
                            + configuration.getNodeName(), e);
        }
    }

    // FIXME! This method should be able to decide whether it should create
    // temporary files or not.
    public MigrateResult migrate(DigitalObject sourceObject, URI sourceFormat,
            URI destinationFormat, List<Parameter> toolParameters)
            throws MigrationException {

        /*
         * - Validate that the proper parameters are set for the migration path
         * identified by sourceFormat and destinationFormat
         */

        final CliMigrationPath migrationPath = migrationPaths
                .getMigrationPath(sourceFormat, destinationFormat);

        // TODO: May throw exception if the parameters are incorrect or
        // insufficient.
        String command = migrationPath.getCommandLine(toolParameters, new HashMap<String,String>());

        try {
            final ProcessRunner toolProcessRunner = new ProcessRunner();

            File sourceTempFile = null, destinationTempFile = null; // FIXME! I
            // didn't do
            // it....
            InputStream processStandardInput = null;

            if (migrationPath.useTempSourceFile()
                    && migrationPath.useTempDestinationFile()) {
                // Create temporary files for the source and destination file
                // for the migration.

                final String tempFileBaseName = generateTempFileBaseName(
                        sourceObject, sourceObject.getTitle());
                sourceTempFile = createSourceTempFile(sourceObject,
                        tempFileBaseName);
                destinationTempFile = createDestinationTempFile(tempFileBaseName);

                // Add the full path and file names of the temporary files to
                // the command line. The command string read from the
                // configuration file must contain %s where the source and
                // destination file names should be inserted, also, the source
                // file name must be specified before the destination file name.
                command = String.format(command, sourceTempFile
                        .getCanonicalFile(), destinationTempFile
                        .getCanonicalFile());
            } else {
                // The tool can handle input and output from stdin/stdout.

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

            byte[] destinationObjectBytes;

            if (migrationPath.useTempSourceFile()
                    && migrationPath.useTempDestinationFile()) {

                destinationObjectBytes = FileUtils
                        .writeInputStreamToBinary(new FileInputStream(
                                destinationTempFile));

                // Delete the temporary files again.
                FileUtils.delete(sourceTempFile);
                FileUtils.delete(destinationTempFile);

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
                // Collect the output from stdout of the migration tool process.
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

    public CliMigrationPaths getMigrationPaths() {
        return migrationPaths;
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
