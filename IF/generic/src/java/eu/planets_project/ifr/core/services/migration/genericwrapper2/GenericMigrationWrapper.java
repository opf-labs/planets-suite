package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationInitialisationException;
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

public class GenericMigrationWrapper {

    private PlanetsLogger log = PlanetsLogger
	    .getLogger(GenericMigrationWrapper.class);

    private MigrationPaths migrationPaths;
    private final String toolIdentifier;
    private final TemporaryFileFactory tempFileFactory;

    private ServiceDescription serviceDescription; // TODO: Consider building
    // IF-dependent objects
    // outside this class.

    private boolean returnByReference; // TODO: This must be a parameter of the

    // generic wrapper!! Default to return by
    // reference!!

    // TODO: It would probably be nice to pass a factory for creation of
    // temporary files on order to avoid a tight coupling with the Planets J2EE
    // way of creating such.
    public GenericMigrationWrapper(Document configuration, String toolIdentifier)
	    throws MigrationInitialisationException {

	this.toolIdentifier = toolIdentifier;
	tempFileFactory = new J2EETempFileFactory(toolIdentifier);

	try {
	    MigrationPathFactory pathsFactory = new DBMigrationPathFactory(
		    configuration);
	    migrationPaths = pathsFactory.getAllMigrationPaths();
	    ServiceDescriptionFactory serviceFactory = new ServiceDescriptionFactory();
	    List<eu.planets_project.services.datatypes.MigrationPath> planetsPaths = convertToPlanetsPaths(migrationPaths
		    .getAllMigrationPaths());
	    serviceDescription = serviceFactory.getServiceDescription(
		    configuration, planetsPaths, toolIdentifier);
	    String result = configuration.getDocumentElement().getAttribute(
		    "returnByReference");// FIXME! This will become a input
	    // parameter of the migrate method.
	    if (result != null) {
		returnByReference = Boolean.valueOf(result);
	    }

	} catch (Exception e) {
	    throw new MigrationInitialisationException(
		    "Failed initialising migration path data from the configuration document: "
			    + configuration.getNodeName(), e);
	}

	// TODO: parse and initialise the ServiceDescription
    }

    /**
     * Get the ServiceDescription for this migrate service
     * 
     * @return the serviceDescription
     */
    public ServiceDescription describe() {
	return serviceDescription;
    }

    // FIXME! This method should be able to decide whether it should create
    // temporary files or not.
    /**
     * Migrate the digital object from the sourceFormat to the destination
     * format, with the given parameters
     * 
     * FIXME! Return by reference must be specified by a prameter...
     * 
     * @param sourceObject
     *            the digital object to migrate
     * @param sourceFormat
     *            the format of the digital object
     * @param destinationFormat
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
     */
    public MigrateResult migrate(DigitalObject sourceObject, URI sourceFormat,
	    URI destinationFormat, List<Parameter> toolParameters)
	    throws MigrationException, IOException {

	/*
	 * Validate that the proper parameters are set for the migration path
	 * identified by sourceFormat and destinationFormat
	 */
	final MigrationPath migrationPath = migrationPaths.getMigrationPath(
		sourceFormat, destinationFormat);

	// If called with null parameters, use an empty list instead
	if (toolParameters == null) {
	    log.warn("Called with null parameters. Assuming the caller ment"
		    + " to calle with an empty list.");
	    toolParameters = new ArrayList<Parameter>();
	}

	File inputTempFile = null;
	InputStream standardInputStream = null;

	if (migrationPath.getToolInputProfile().usePipedIO()) {
	    // Serve the digital object through standard input
	    standardInputStream = sourceObject.getContent().read();
	} else {
	    // Prepare a temporary input file containing the digital object.
	    inputTempFile = createTempInputFile(sourceObject, migrationPath);
	}

	// Prepare a temporary file for the migrated object if the tool writes
	// to a file.
	File outputTempFile = null;
	final ToolIOProfile outputIOProfile = migrationPath
		.getToolOutputProfile();
	if (outputIOProfile.usePipedIO() == false) {
	    outputTempFile = createTempFile(outputIOProfile);
	}

	List<String> command = migrationPath.getCommandLine(toolParameters);

	// FIXME! The command line keyword substitution should take place here
	// and not in the migration path object!

	// Execute the tool
	final ProcessRunner toolProcessRunner = new ProcessRunner();
	ServiceReport serviceReport = executeToolProcess(toolProcessRunner,
		command, standardInputStream);

	if (serviceReport.getType() == Type.ERROR) {
	    String message = "Failed migrating object with title '"
		    + sourceObject.getTitle() + "' from format URI: "
		    + sourceFormat + " to " + destinationFormat
		    + " Standard output: "
		    + toolProcessRunner.getProcessOutputAsString()
		    + "\nStandard error output: "
		    + toolProcessRunner.getProcessErrorAsString();
	    serviceReport = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
		    message);
	    return new MigrateResult(null, serviceReport);
	}

	// Cleanup temporary files.

	if (inputTempFile != null) {
	    inputTempFile.delete();
	}
	if (outputTempFile != null) {
	    outputTempFile.delete();
	}

	// TODO: Delete any intermediate temporary files.

	// READING THE OUTPUT
	// TODO return a reference to the outputfile
	DigitalObject.Builder builder;

	final ToolIOProfile toolOutputProfile = migrationPath
		.getToolOutputProfile();
	if (!toolOutputProfile.usePipedIO()) {
	    // we should read a temp file afterwards
	    File outputfile = new File("");// FIXME! Create temp. file!!
	    if (returnByReference) {
		builder = new DigitalObject.Builder(Content
			.byReference(outputfile));
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

	    if (returnByReference) {
		// we should read the output
		builder = new DigitalObject.Builder(Content
			.byReference(toolProcessRunner.getProcessOutput()));
	    } else {
		builder = new DigitalObject.Builder(Content
			.byValue(toolProcessRunner.getProcessOutput()));
	    }
	    String message = "Successfully migrated object with title '"
		    + sourceObject.getTitle() + "' from format URI: "
		    + sourceFormat + " to " + destinationFormat
		    + " Standard error output: "
		    + toolProcessRunner.getProcessErrorAsString();
	    serviceReport = new ServiceReport(Type.INFO, Status.SUCCESS,
		    message);

	}

	// TODO cleanup the dir
	DigitalObject destinationObject = builder.format(destinationFormat)
		.build();

	return new MigrateResult(destinationObject, serviceReport);

    }

    /**
     * Create a temporary input file and write the contents of the digital
     * object specified by <code>sourceObject</code> to it if the migration path
     * dictates the use of a temporary input file. If the migration path does
     * not dictate the use of a temporary input file, then <code>null</code> is
     * returned.
     * 
     * @param sourceObject
     *            The digital object to be written to a temporary file, if
     *            necessary.
     * 
     * @param migrationPath
     *            The migration path containing information about whether a
     *            temporary input file should be applied for this migration.
     * @return A <code>File</code> instance representing the temporary input
     *         file containing the data from the digital object if a temporary
     *         file must be used and otherwise <code>null</code>.
     */
    private File createTempInputFile(DigitalObject sourceObject,
	    MigrationPath migrationPath) {

	File inputTempFile = null;
	final ToolIOProfile inputIOProfile = migrationPath
		.getToolInputProfile();
	if (!inputIOProfile.usePipedIO()) {

	    // Create a temporary file and write the contents of the digital
	    // object to it.
	    inputTempFile = createTempFile(inputIOProfile);
	    FileUtils.writeInputStreamToFile(sourceObject.getContent().read(),
		    inputTempFile);
	}
	return inputTempFile;
    }

    /**
     * Create a temporary file based on the <code>{@link ToolIOProfile}</code>
     * provided by <code>toolIOProfile</code>. If the profile contains a desired
     * file name, then a temporary file with that name will be created and other
     * wise a file with a random name will be generated. However, files with
     * random names will have the command line file label included in their name
     * to make any debugging easier.
     * 
     * @param toolIOProfile
     *            <code>ToolIOProfile</code> with optional name information for
     *            the temporary file to create.
     * @return <code>File</code> representing a temporary file.
     */
    private File createTempFile(ToolIOProfile toolIOProfile) {
	if (toolIOProfile.getDesiredTempFileName() != null) {
	    return tempFileFactory.prepareTempFile(toolIOProfile
		    .getDesiredTempFileName());
	} else {
	    return tempFileFactory.prepareRandomNamedTempFile(toolIOProfile
		    .getCommandLineFileLabel());
	}
    }

    /**
     * TODO: This should go into a utility class.
     * 
     * Convert a collection of generic wrapper migration paths to a PLANETS
     * <code>MigrationPath</code> instances. During this conversion any presets
     * of the generic wrapper migration paths will be converted to a PLANETS
     * parameters and a list of valid values and their descriptions will be
     * appended to the description of the (preset) parameter.
     * 
     * @param genericWrapperMigrationPaths
     *            A collection of generic wrapper <code>MigrationPath</code>
     *            instances to convert.
     * @return a <code>List</code> of
     *         <code>eu.planets_project.services.datatypes.MigrationPath</code>
     *         created from the generic wrapper migration paths.
     */
    private List<eu.planets_project.services.datatypes.MigrationPath> convertToPlanetsPaths(
	    Collection<MigrationPath> genericWrapperMigrationPaths) {

	final ArrayList<eu.planets_project.services.datatypes.MigrationPath> planetsPaths = new ArrayList<eu.planets_project.services.datatypes.MigrationPath>();
	for (MigrationPath migrationPath : genericWrapperMigrationPaths) {

	    List<Parameter> planetsParameters = new ArrayList<Parameter>();
	    planetsParameters.addAll(migrationPath.getToolParameters());

	    // Add a parameter for each preset (category)
	    for (Preset preset : migrationPath.getAllToolPresets()) {

		Parameter.Builder parameterBuilder = new Parameter.Builder(
			preset.getName(), null);

		// Append a description of the valid values for the preset
		// parameter.
		String usageDescription = "\n\nValid values : Description\n";

		for (PresetSetting presetSetting : preset.getAllSettings()) {

		    usageDescription += "\n" + presetSetting.getName() + " : "
			    + presetSetting.getDescription();
		}

		parameterBuilder.description(preset.getDescription()
			+ usageDescription);

		planetsParameters.add(parameterBuilder.build());
	    }
	    planetsPaths
		    .add(new eu.planets_project.services.datatypes.MigrationPath(
			    migrationPath.getSourceFormat(), migrationPath
				    .getDestinationFormat(), planetsParameters));
	}

	return planetsPaths;
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

    private void handleTempSourceFile(MigrationPath migrationPath,
	    DigitalObject sourceObject, File workfolder) throws IOException {
	// TempFile sourcetempfile = migrationPath.getTempSourceFile();
	TempFile sourcetempfile = new TempFile("FIXME - Fake"); // FIXME! dooo
	// somesing
	// enterrigent
	// File realtemp = createTemp(workfolder, sourcetempfile);
	// FileUtils.writeInputStreamToFile(sourceObject.getContent().read(),
	// realtemp);
	// sourcetempfile.setFile(realtemp);
    }

}
