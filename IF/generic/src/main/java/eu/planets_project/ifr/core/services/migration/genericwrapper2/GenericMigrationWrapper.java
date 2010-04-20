package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.ConfigurationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationInitialisationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.ParameterBuilder;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.ParameterReader;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ServicePerformanceHelper;

/**
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class GenericMigrationWrapper {

    private Logger log = Logger.getLogger(GenericMigrationWrapper.class
	    .getName());

    private MigrationPaths migrationPaths;
    private final String toolIdentifier;
    private final TemporaryFileFactory tempFileFactory;
    private final List<Parameter> environmentParameters;

    private ServiceDescription serviceDescription;

    public GenericMigrationWrapper(Document configuration,
	    Configuration environmentSettings, String toolIdentifier)
	    throws MigrationInitialisationException {

	this.toolIdentifier = toolIdentifier;

	tempFileFactory = new J2EETempFileFactory(toolIdentifier);

	environmentParameters = ParameterBuilder.buid(environmentSettings);

	try {
	    MigrationPathFactory pathsFactory = new DBMigrationPathFactory(
		    configuration);
	    migrationPaths = pathsFactory.getAllMigrationPaths();

	    String serviceProvider = "Undefined - please assign the correct "
		    + "service provider identifier to the \"serviceprovider\""
		    + " property in the property file for this service.";
	    for (Parameter environmentParameter : environmentParameters) {
		if ("serviceprovider".equals(environmentParameter.getName())) {
		    serviceProvider = environmentParameter.getValue();
		}
	    }

	    final ServiceDescriptionFactory serviceFactory = new ServiceDescriptionFactory(
		    toolIdentifier, serviceProvider, configuration);
	    serviceDescription = serviceFactory.getServiceDescription();
	} catch (Exception e) {
	    throw new MigrationInitialisationException(
		    "Failed initialising migration path data from the configuration document: "
			    + configuration.getNodeName(), e);
	}
    }

    /**
     * Get the ServiceDescription for this migrate service
     * 
     * @return the serviceDescription
     */
    public ServiceDescription describe() {
	return serviceDescription;
    }

    /**
     * Migrate the <code>digitalObject</code> from <code>inputFormat</code> to
     * <code>outputFormat</code>, applying the parameters provided by
     * <code>toolParameters</code>.
     * 
     * FIXME! Return by reference must be specified by the parameter:
     * returnbyreference=true/false. Default is true.
     * 
     * @param digitalObject
     *            the digital object to migrate
     * @param inputFormat
     *            the format of the digital object
     * @param outputFormat
     *            The
     * @param toolParameters
     *            the parameters. If null, is initialised as an empty list.
     * @return the migrateResult for the migration
     * @throws MigrationException
     *             if the generic wrapper failed in invoking the tool and
     *             migrating the digital object. Tool failures are embedded in
     *             the migrateResult.
     * 
     *             FIXME! Consider not throwing exceptions as that will only
     *             force wrapping developers to add cut-and-paste exception
     *             handling on the outside. Thus, it should rather be put inside
     *             this method.
     *@throws ConfigurationException
     */
    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
	    URI outputFormat, List<Parameter> toolParameters)
	    throws MigrationException, ConfigurationException {

	final ServicePerformanceHelper servicePerformanceHelper = new ServicePerformanceHelper();
	final Date migrationStartTime = new Date();

	/*
	 * Validate that the proper parameters are set for the migration path
	 * identified by inputFormat and outputFormat
	 */
	final MigrationPath migrationPath = migrationPaths.getMigrationPath(
		inputFormat, outputFormat);

	// If called with null parameters, use an empty list instead
	if (toolParameters == null) {
	    log.warning("Called with null parameters. Assuming the caller ment"
		    + " to call with an empty list.");
	    toolParameters = new ArrayList<Parameter>();
	}

	// Prepare any necessary temporary files.
	final Map<String, File> temporaryFileMappings = createTemporaryFiles(migrationPath);

	// Prepare the data to migrate
	InputStream standardInputStream = null;
	final ToolIOProfile inputIOProfile = migrationPath
		.getToolInputProfile();
	if (inputIOProfile.usePipedIO()) {

	    // Serve the digital object through standard input
	    standardInputStream = digitalObject.getContent().getInputStream();
	} else {

	    // Serve the digital object through a temporary input file.
	    File inputTempFile = temporaryFileMappings.get(inputIOProfile
		    .getCommandLineFileLabel());
	    DigitalObjectUtils.toFile(digitalObject, inputTempFile);
	}

	// Create an executable command line for the process runner.
	final PRCommandBuilder commandBuilder = new PRCommandBuilder(
		environmentParameters);
	final List<String> prCommand = commandBuilder.buildCommand(
		migrationPath, toolParameters, temporaryFileMappings);

	if (log.isLoggable(Level.INFO)) {
	    String fullCommandLine = "";
	    for (String cmdfrag : prCommand) {
		fullCommandLine += cmdfrag + " ";
	    }
	    log.info("Executing command line: " + fullCommandLine);
	}

	// Execute the tool
	final ProcessRunner toolProcessRunner = new ProcessRunner();
	final boolean executionSuccessful = executeToolProcess(
		toolProcessRunner, prCommand, standardInputStream);

	// Delete temporary files. However, do NOT delete the output unless the
	// execution failed.

	final ToolIOProfile outputIOProfile = migrationPath
		.getToolOutputProfile();

	if ((outputIOProfile.usePipedIO() == false) && executionSuccessful) {
	    // OK, there should exist an output file. Avoid deleting it.
	    final String outputFileLabel = outputIOProfile
		    .getCommandLineFileLabel();
	    for (String tempFileLabel : temporaryFileMappings.keySet()) {
		if (outputFileLabel.equals(tempFileLabel) == false) {
		    temporaryFileMappings.get(tempFileLabel).delete();
		}
	    }
	} else {
	    // The output has been returned through a pipe, so it is safe to
	    // delete all files.
	    for (File tempFile : temporaryFileMappings.values()) {
		tempFile.delete();
	    }
	}

	if (executionSuccessful == false) {
	    return buildMigrationResult(migrationPath, digitalObject, null,
		    toolProcessRunner);
	}

	// Now create a digital object from the tools output.
	DigitalObject.Builder builder;

	final ParameterReader parameterReader = new ParameterReader(
		toolParameters);
	final boolean returnDataByReference = parameterReader
		.getBooleanParameter("returnByReference", true);

	final ToolIOProfile toolOutputProfile = migrationPath
		.getToolOutputProfile();
	if (toolOutputProfile.usePipedIO() == false) {

	    // The tool has written the output to a temporary file. Create a
	    // digital object based on that.
	    final File outputFile = temporaryFileMappings.get(toolOutputProfile
		    .getCommandLineFileLabel());
	    if (returnDataByReference) {
		builder = new DigitalObject.Builder(Content
			.byReference(outputFile));
		// We cannot tell when the temporary file can be deleted, so let
		// it live.
	    } else {
		builder = new DigitalObject.Builder(Content.byValue(outputFile));

		// It is now safe to delete the temporary file.
		outputFile.delete();
	    }
	} else {

	    // The tool has written the output to standard output. Create a
	    // digital object based on that output.
	    if (returnDataByReference) {
		// Direct the standard output contents to a temporary file.
		builder = new DigitalObject.Builder(Content
			.byReference(toolProcessRunner.getProcessOutput()));
	    } else {
		// Return the standard output contents by value.
		builder = new DigitalObject.Builder(Content
			.byValue(toolProcessRunner.getProcessOutput()));
	    }
	}

	final double migrationDuration = new Date().getTime()
		- migrationStartTime.getTime();

	builder.format(outputFormat);
	final Agent agent = new Agent(toolIdentifier, serviceDescription
		.getName(), serviceDescription.getType());

	String eventSummary = "Migration carried out by executing the command line:";
	for (String commandLineFragment : prCommand) {
	    eventSummary += " " + commandLineFragment;
	}
	eventSummary += "\n\nThe migration service was called with these parameters:\n\n";
	for (Parameter serviceParameter : toolParameters) {
	    eventSummary += serviceParameter.getName() + " = "
		    + serviceParameter.getValue() + "\n";
	}

	servicePerformanceHelper.stop();

	// Add information about the migration event to the digital object.
	final DateFormat defaultDateFormat = DateFormat.getDateInstance();
	final Event event = new Event(eventSummary, defaultDateFormat
		.format(migrationStartTime), migrationDuration, agent,
		servicePerformanceHelper.getPerformanceProperties());
	builder.events(event);

	final DigitalObject resultObject = builder.build();

	return buildMigrationResult(migrationPath, digitalObject, resultObject,
		toolProcessRunner);
    }

    private MigrateResult buildMigrationResult(MigrationPath migrationPath,
	    DigitalObject inputObject, DigitalObject resultObject,
	    ProcessRunner toolProcessRunner) {

	String stdoutPart = String.format("Standard output:\n%s\n\n",
		toolProcessRunner.getProcessOutputAsString());

	// Default to a service report for a failed execution.
	String statusDescription = "Failed migrating";
	Type messageType = Type.ERROR;
	Status messageStatus = Status.TOOL_ERROR;

	if (toolProcessRunner.getReturnCode() == 0) {
	    // Create a service report for a successful execution.

	    statusDescription = "Successfully migrated";
	    messageType = Type.INFO;
	    messageStatus = Status.SUCCESS;

	    final ToolIOProfile toolOutputProfile = migrationPath
		    .getToolOutputProfile();

	    if (toolOutputProfile.usePipedIO()) {
		// Avoid printing standard output if the digital object was
		// returned through that.
		stdoutPart = "";
	    }
	}

	// Fill in the blanks in the generic message template.
	final String reportMessage = String.format("%s object with title '%s'"
		+ " from format URI: '%s' to '%s'.\n%sStandard error output:"
		+ "\n%s", statusDescription, inputObject.getTitle(),
		migrationPath.getInputFormat(),
		migrationPath.getOutputFormat(), stdoutPart, toolProcessRunner
			.getProcessErrorAsString());

	final ServiceReport serviceReport = new ServiceReport(messageType,
		messageStatus, reportMessage);

	return new MigrateResult(resultObject, serviceReport);
    }

    /**
     * Create all the temporary files needed in order to perform a migration
     * using the migration path specified by <code>migrationPath</code> and
     * return a map associating the command labels with the appropriate file
     * path.
     * 
     * @param migrationPath
     *            The migration path to create temporary files for.
     * @return A <code>Map</code> associating the labels in the command line
     *         which identifies temporary files with the actual file paths for
     *         the files.
     */
    private Map<String, File> createTemporaryFiles(MigrationPath migrationPath)
	    throws MigrationException {

	Map<String, File> temporaryFileMappings = new HashMap<String, File>();

	final ToolIOProfile toolInputProfile = migrationPath
		.getToolInputProfile();

	try {
	    if (!toolInputProfile.usePipedIO()) {

		temporaryFileMappings = createTemporaryFile(
			temporaryFileMappings, toolInputProfile
				.getCommandLineFileLabel(), toolInputProfile
				.getDesiredTempFileName());

		final String tempFileLabel = toolInputProfile
			.getCommandLineFileLabel();
		log.info(String.format("Created a temporary input file. "
			+ "Label = '%s'. " + "File name: '%s'", tempFileLabel,
			temporaryFileMappings.get(tempFileLabel)
				.getCanonicalPath()));
	    }

	    final ToolIOProfile toolOutputProfile = migrationPath
		    .getToolOutputProfile();
	    if (!toolOutputProfile.usePipedIO()) {

		temporaryFileMappings = createTemporaryFile(
			temporaryFileMappings, toolOutputProfile
				.getCommandLineFileLabel(), toolOutputProfile
				.getDesiredTempFileName());

		final String tempFileLabel = toolOutputProfile
			.getCommandLineFileLabel();
		log.info(String.format("Created a temporary output file. "
			+ "Label = '%s'. " + "File name: '%s'", tempFileLabel,
			temporaryFileMappings.get(tempFileLabel)
				.getCanonicalPath()));
	    }

	    final Map<String, String> temporaryFileDeclarations = migrationPath
		    .getTempFileDeclarations();
	    for (String tempFileLabel : temporaryFileDeclarations.keySet()) {
		final String desiredFileName = temporaryFileDeclarations
			.get(tempFileLabel);

		temporaryFileMappings = createTemporaryFile(
			temporaryFileMappings, tempFileLabel, desiredFileName);
	    }
	} catch (IOException ioe) {
	    throw new MigrationException("Failed creating temporary files.",
		    ioe);
	}
	return temporaryFileMappings;
    }

    /**
     * Create a temporary file with a random name or with a desired name, if a
     * such is specified. If the caller has no desired file name then
     * <code>desiredFileName</code> must be <code>null</code>.
     * <p/>
     * The created file will be added to <code>tempFileMap</code>, using
     * <code>fileLabel</code> as the key.
     * <p/>
     * Files that are given a random name will have <code>fileLabel</code>
     * appended to the file name to make debugging easier.
     * 
     * @param tempFileMap
     *            A file map to add the created temporary file to.
     * @param fileLabel
     *            The label/key which the generated file must be associated with
     *            in the returned map.
     * @param desiredFileName
     *            The desired name of the temporary file or <code>null</code> if
     *            the file should be given a random name.
     * @return <code>tempFileMap</code> with the created file added.
     */
    private Map<String, File> createTemporaryFile(
	    Map<String, File> tempFileMap, String fileLabel,
	    String desiredFileName) {
	File temporaryFile = null;

	if (desiredFileName == null) {
	    // No desired name has been specified. Create a file with a random
	    // name having the file label added to give a clue in case of
	    // debugging becomes necessary.
	    temporaryFile = tempFileFactory
		    .prepareRandomNamedTempFile(fileLabel);
	} else {
	    // Create a temporary file with the desired base name.
	    temporaryFile = tempFileFactory.prepareTempFile(desiredFileName);
	}

	tempFileMap.put(fileLabel, temporaryFile);
	return tempFileMap;
    }

    /**
     * Execute the command line, described by the list of strings provided by
     * <code>command</code>, using the <code>ProcessRunner</code> provided by
     * <code>toolProcessRunner</code>. The process runner will pass any
     * information from <code>processStandardInput</code> on to the command
     * through the standard input.
     * 
     * @param toolProcessRunner
     *            <code>ProcessRunner</code> instance for execution of the
     *            command line.
     * @param command
     *            A list of strings constituting a command line to execute.
     * @param processStandardInput
     *            An input stream for passing information to be piped to the
     *            command through standard input.
     * @return <code>true</code> if the execution was successful and otherwise
     *         <code>false</code>.
     */
    private boolean executeToolProcess(ProcessRunner toolProcessRunner,
	    List<String> command, InputStream processStandardInput) {

	toolProcessRunner.setInputStream(processStandardInput);
	toolProcessRunner.setCommand(command);
	toolProcessRunner.setCollection(true);
	toolProcessRunner.setOutputCollectionByteSize(-1);

	toolProcessRunner.run();
	return toolProcessRunner.getReturnCode() == 0;
    }
}
