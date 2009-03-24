package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.cli.CliMigrationPaths;

public class GenericCLIMigrationWrapper {

	private CliMigrationPaths migrationPaths;
	//	private String command;

	//FIXME! configFilePath really ought to be a File object or some kind of input stream. CLIMigrationPaths should do the same, the behaviour of its initialiseFromFile() method is pretty hard-wired to J2EE, and it is not quite obvious where to put the configuration file. 
	public GenericCLIMigrationWrapper(String configFilePath) throws MigrationInitialisationException {

		try {
			migrationPaths = CliMigrationPaths.initialiseFromFile(configFilePath);
		} catch (Exception e) {
			throw new MigrationInitialisationException("Failed initialising migration path data from the file: " + configFilePath, e);
		}
	}

	// FIXME! This method should be able to decide whether it should create temporary files or not. 
	public MigrateResult migrate(DigitalObject sourceObject, URI sourceFormat,
			URI destinationFormat, Parameters callerParameters) throws MigrationException {


		String command = getMigrationCommand(sourceFormat, destinationFormat);

		try {
			final ProcessRunner toolProcessRunner = new ProcessRunner();

			final boolean useTempFiles = true; // FIXME! Add field to the configuration file indicating whether the tool accepts input from stdin or a file. 

			File sourceTempFile, destinationTempFile;
			InputStream processStandardInput = null;
			
			if(useTempFiles) {
				// Create temporary files for the source and destination file for the migration.

				final String tempFileBaseName = generateTempFileBaseName(sourceObject, sourceObject.getTitle());
				sourceTempFile = createSourceTempFile(sourceObject, tempFileBaseName);
				destinationTempFile = createDestinationTempFile(tempFileBaseName);

				// Add the full path and file names of the temporary files to the command line. The command string read from the configuration file must contain %s where the source and destination file names should be inserted, also, the source file name must be specified before the destination file name.
				command = String.format(command, sourceTempFile.getCanonicalFile(), destinationTempFile.getCanonicalFile());			
			} else {
				// The tool can handle input and output from stdin/stdout.

				processStandardInput = sourceObject.getContent().read();
			}

			final ServiceReport serviceReport = executeToolProcess(toolProcessRunner, command, processStandardInput);

			if (serviceReport.getErrorState() != 0){
				serviceReport.setError("Failed migrating object with title '" + sourceObject.getTitle() + "' from format URI: " + sourceFormat + " to " + destinationFormat + " Standard output: " + toolProcessRunner.getProcessOutputAsString() + "\nStandard error output: " + toolProcessRunner.getProcessErrorAsString());
				return new MigrateResult(null, serviceReport);
			}

			byte[] destinationObjectBytes;

			if (useTempFiles) {

				destinationObjectBytes = FileUtils.writeInputStreamToBinary(new FileInputStream(destinationTempFile));

				// Delete the temporary files again.
				sourceTempFile.delete();
				destinationTempFile.delete();

				serviceReport.setInfo("Successfully migrated object with title '" + sourceObject.getTitle() + "' from format URI: " + sourceFormat + " to " + destinationFormat + " Standard output: " + toolProcessRunner.getProcessOutputAsString() + "\nStandard error output: " + toolProcessRunner.getProcessErrorAsString());
			} else {
				// Collect the output from stdout of the migration tool process.
				destinationObjectBytes = FileUtils.writeInputStreamToBinary(toolProcessRunner.getProcessOutput());

				serviceReport.setInfo("Successfully migrated object with title '" + sourceObject.getTitle() + "' from format URI: " + sourceFormat + " to " + destinationFormat + " Standard error output: " + toolProcessRunner.getProcessErrorAsString());
			}

			final DigitalObject destinationObject = new DigitalObject.Builder(Content.byValue(destinationObjectBytes)).build();

			return new MigrateResult(destinationObject, serviceReport);

		} catch(IOException ioe) {
			throw new MigrationException("Failed migrating object with title '" + sourceObject.getTitle() + " from format URI: " + sourceFormat + " to : "+ destinationFormat + " due to problems while handling temporary files.", ioe);			
		}
	}
	
	public CliMigrationPaths getMigrationPaths() {
		return migrationPaths;
	}

	private ServiceReport executeToolProcess(ProcessRunner toolProcessRunner,
			String command, InputStream processStandardInput) {

		final ServiceReport serviceReport = new ServiceReport();
		toolProcessRunner.setInputStream(processStandardInput);
		toolProcessRunner.setCommand(Arrays.asList("/bin/sh", "-c", command));
		toolProcessRunner.setCollection(true);
		toolProcessRunner.setOutputCollectionByteSize(-1);

		toolProcessRunner.run();

		serviceReport.setErrorState(toolProcessRunner.getReturnCode());
		return serviceReport;
	}

	private String getMigrationCommand(URI sourceFormat, URI destinationFormat) throws MigrationException {
		final String command = migrationPaths.findMigrationCommand(sourceFormat, destinationFormat);

		if (command == null){
			throw new MigrationException("No migration command defined for migration from format URI: " + sourceFormat + " to : "+ destinationFormat);
		}
		return command;
	}

//	FIXME! KILL
//	private File[] createTempFiles(String humanReadableClue) {
//
//		final Date now = new Date();
//		final String tempBaseFileName = humanReadableClue + UUID.randomUUID() + now.getTime();
//		final File sourceTempFile = FileUtils.getTempFile(tempBaseFileName, "source");
//		final File destinationTempFile = FileUtils.getTempFile(tempBaseFileName, "destination");
//		return new File[]{sourceTempFile, destinationTempFile};
//	}

	private File createSourceTempFile(DigitalObject sourceObject, String tempFileBaseName) {

		final File sourceTempFile = FileUtils.getTempFile(tempFileBaseName, "source");

		// Write the digital object to the temporary source file.
		FileUtils.writeInputStreamToFile(sourceObject.getContent().read(), sourceTempFile);

		return sourceTempFile;
	}

	private File createDestinationTempFile(String tempFileBaseName) {

		return FileUtils.getTempFile(tempFileBaseName, "destination");
	}

	private String generateTempFileBaseName(DigitalObject sourceObject, String humanReadableClue) {

		final Date now = new Date();
		return humanReadableClue + UUID.randomUUID() + now.getTime();
	}
}
