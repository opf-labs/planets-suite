package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * Migration path containing all information necessary to execute a (chain of)
 * command-line migration tool(s).
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class MigrationPathImpl implements MigrationPath {

    private PlanetsLogger log = PlanetsLogger
	    .getLogger(MigrationPathImpl.class);

    private ToolIOProfile toolInputProfile;
    private ToolIOProfile toolOutputProfile;
    private URI sourceFormatURI;
    private URI destinationFormatURI;
    private Map<String, String> tempFiles;
    private Map<String, Parameter> parameters;
    private Map<String, Preset> presets; // TODO: Consider whether it is an
    // advantage/necessary to use a map
    // rather than just a collectiton
    private List<String> commandLine; // TODO: It would be sensible having a
    // command class now that we will have to
    // have more commands in connection with
    // the selftest functionality.
    private String defaultPresetName;

    /**
     * The default constructor has default access, as it should only be used by
     * factories.
     */
    MigrationPathImpl() {
	parameters = new HashMap<String, Parameter>();
	presets = new HashMap<String, Preset>();
	tempFiles = new HashMap<String, String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getCommandLine(java.util.Collection)
     */
    public List<String> getCommandLine(Collection<Parameter> toolParameters)
	    throws MigrationException {

	log.info("Entering getCommandLine");
	// Get a complete list of identifiers in the command line.
	final Set<String> usedIdentifiers = getIdentifiers(commandLine);

	// TODO: Work with other presets than the default one

	String defaultpreset = getDefaultPreset();
	// if the tool parameters contain this preset
	boolean setDefault = false;
	for (Parameter toolparam : toolParameters) {
	    if (toolparam.getName().equals(defaultpreset)) {
		setDefault = true;
		Collection<Parameter> presetparams = presets.get(defaultpreset)
			.getSetting(toolparam.getValue()).getParameters();
		toolParameters.addAll(presetparams);
		break;
	    }
	}

	// if the tool parameters does not contain this preset
	if (!setDefault) {
	    // lookup the value of the default preset
	    // get the param names and values from there
	    Preset preset = presets.get(defaultpreset);
	    if (preset != null) {
		Collection<Parameter> defaultparams = preset
			.getDefaultSetting().getParameters();
		toolParameters.addAll(defaultparams);
	    } else {
		// There is no default preset
	    }
	}

	final Set<String> validIdentifiers = getValidParameterNames(toolParameters);

	validIdentifiers.addAll(getValidFileIdentifiers(tempFiles));

	final ToolIOProfile toolInputProfile = getToolInputProfile(); 
	if (!toolInputProfile.usePipedIO()) {
	    log.info("Adding temp input file as valid identifier "
		    + toolInputProfile.getCommandLineFileLabel());
	    validIdentifiers.add(toolInputProfile.getCommandLineFileLabel());
	}

	final ToolIOProfile toolOuptpuProfile = getToolOutputProfile();
	if (!toolOuptpuProfile.usePipedIO()) {
	    log.info("Adding temp dest file as valid identifier: "
		    + toolOuptpuProfile.getCommandLineFileLabel());
	    validIdentifiers.add(toolOuptpuProfile.getCommandLineFileLabel());
	}

	// TODO: Check the parameters and filename mappings for injection
	// attacks by sanity checking the parameters -
	// throw exception in case of failure. However, it may be safe omitting
	// the test for the file name mappings, as they are solely based on the
	// configuration file and what the generic wrapper is doing.

	if (!validIdentifiers.containsAll(usedIdentifiers)) {
	    usedIdentifiers.removeAll(validIdentifiers);
	    throw new MigrationException("Cannot build the command line. "
		    + "Missing values for these identifiers: "
		    + usedIdentifiers);
	}

	List<String> executableCommandLine = new ArrayList<String>();
	for (String cmd : commandLine) {
	    for (Parameter parameter : toolParameters) {
		cmd = cmd.replaceAll("#" + parameter.getName(), parameter
			.getValue());
	    }

	    for (String tempFileLabel : tempFiles.keySet()) {
		// FIXME! This is wrong! The temp file name must be absolute
		// paths, however, this is easily fixed if the command line is
		// constructed by the generic wrapper class.
		cmd = cmd.replaceAll("#" + tempFileLabel, tempFiles
			.get(tempFileLabel));
	    }
	    //FIXME! Broken!
	    if (!toolOuptpuProfile.usePipedIO()) {
//		cmd = cmd.replaceAll("#" + tempSourceFile.getCodename(),
//			tempSourceFile.getFile().getAbsolutePath());
	    } else {
//		cmd = cmd.replaceAll("#" + tempOutputFile.getCodename(),
//			tempOutputFile.getFile().getAbsolutePath());
	    }
	    executableCommandLine.add(cmd);
	}

	return executableCommandLine;
    }

    /**
     * Get a <code>Set</code> containing all the valid identifiers for file
     * names parameters from <code>tempFileMap</code>, that is, identifiers that
     * are not the empty string and that have an associated file name, which
     * also is not the empty string.
     * 
     * @param tempFileMap
     *            a <code>Map</code> of file identifiers associated with file
     *            names.
     * @return a <code>Set</code> containing the valid identifiers from
     *         <code>parameters</code>.
     */
    private Set<String> getValidFileIdentifiers(Map<String, String> tempFileMap) {

	Set<String> validFileIdentifiers = new HashSet<String>();
	for (String identifier : tempFileMap.keySet()) {
	    final String fileName = tempFileMap.get(identifier);
	    if ((fileName != null) && ("".equals(fileName) == false)
		    && ("".equals(identifier) == false)) {
		validFileIdentifiers.add(identifier);
	    }
	}
	return validFileIdentifiers;
    }

    /**
     * Get a <code>Set</code> containing all the names of parameters from
     * <code>parameters</code> that have been initialised with a value, and are
     * thus valid.
     * 
     * @param parameters
     *            a <code>Collection</code> of parameters to get valid
     *            parameters from.
     * @return a <code>Set</code> containing the names of all the parameters
     *         from <code>parameters</code> that have a value.
     */
    private Set<String> getValidParameterNames(Collection<Parameter> parameters) {
	Set<String> validParameters = new HashSet<String>();
	for (Parameter parameter : parameters) {
	    final String parameterName = parameter.getName();
	    if ((parameterName != null) && ("".equals(parameterName) == false)
		    && (parameter.getValue() != null)) {
		validParameters.add(parameterName);
	    }
	}

	return validParameters;
    }

    /**
     * Get the names of all identifiers (all words with a leading '#') of the
     * form <code>%myIdentifier%</code> found in
     * <code>stringWithIdentifiers</code>. If the previous example was found in
     * the string, then the returned set would contain the string
     * &quot;myIdentifier&quot;.
     * 
     * @param stringListWithIdentifiers
     *            a <code>String</code> containing identifiers.
     * @return a <code>Set</code> containing the identifiers found.
     */
    private Set<String> getIdentifiers(List<String> stringListWithIdentifiers) {
	Set<String> foundIdentifiers = new HashSet<String>();

	for (String stringWithIdentifiers : stringListWithIdentifiers) {
	    StringTokenizer stringTokenizer = new StringTokenizer(
		    stringWithIdentifiers);
	    while (stringTokenizer.hasMoreTokens()) {
		String identifier = stringTokenizer.nextToken();
		if (identifier.charAt(0) == '#') {
		    identifier = identifier.substring(1);
		    foundIdentifiers.add(identifier);
		}
	    }
	}
	return foundIdentifiers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getCommandLine()
     */
    public List<String> getCommandLine() {
	return commandLine;
    }

    /**
     * Set the command line attribute of this migration path. The command line
     * may contain tags of the form <code>%my_tag%</code> to indicate that
     * either a parameter or a name of a temporary file must be put in place at
     * a specific location. However, these tags must be defined using the
     * {@link #setToolParameters} methods.
     * 
     * @param commandLine
     *            <code>String</code> containing the command line to set.
     */
    public void setCommandLine(List<String> commandLine) {
	this.commandLine = commandLine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getDefaultPreset()
     */
    public String getDefaultPreset() {
	return defaultPresetName;
    }

    /**
     * Set the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @param defaultPreset
     *            the ID of the default preset category.
     */
    public void setDefaultPreset(String defaultPreset) {
	this.defaultPresetName = defaultPreset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getDestinationFormat()
     */
    public URI getDestinationFormat() {
	return destinationFormatURI;
    }

    /**
     * Set the destination format <code>URI</code> for this
     * <code>CliMigrationPath</code>.
     * 
     * @param destinationFormatURI
     *            destination format <code>URI</code> to set.
     */
    public void setDestinationFormat(URI destinationFormatURI) {
	this.destinationFormatURI = destinationFormatURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getSourceFormat()
     */
    public URI getSourceFormat() {
	return sourceFormatURI;
    }

    /**
     * Set the source format <code>URI</code> for this
     * <code>CliMigrationPath</code>.
     * 
     * @param sourceFormatURI
     *            source format <code>URI</code> to set.
     */
    public void setSourceFormat(URI sourceFormatURI) {
	this.sourceFormatURI = sourceFormatURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getTempFileDeclarations()
     */
    public Map<String, String> getTempFileDeclarations() {
	return tempFiles;
    }

    /**
     * Replace the current map defining the relationship between the labels in
     * the command line that should be substituted with file names of temporary
     * files with the actual names of these. Multiple calls to this method will
     * just add more mappings to the internal map, however, if a label in the
     * specified map has already been used in the internal map of this migration
     * path, then the previous mapping will be removed.
     * 
     * @param tempFileDeclarations
     *            a map containing a paring of temp. file labels and optionally
     *            a file name to replace the current internal map.
     */
    public void setTempFilesDeclarations(
	    Map<String, String> tempFileDeclarations) {

	tempFiles = tempFileDeclarations;
    }

    /**
     * Add a map defining the relationship between the labels in the command
     * line that should be substituted with file names of temporary files with
     * the actual names of these. Multiple calls to this method will just add
     * more mappings to the internal map, however, if a label in the specified
     * map has already been used in the internal map of this migration path,
     * then the previous mapping will be removed.
     * 
     * @param tempFileDeclarations
     *            a map containing a paring of temp. file labels and optionally
     *            a file name to be added to the internal map.
     */
    public void addTempFilesDeclarations(
	    Map<String, String> tempFileDeclarations) {

	tempFiles.putAll(tempFileDeclarations);
    }

    /**
     * Add a relationship between the <code>label</code> and
     * <code>filename</code>. The label must match a label used in the command
     * line, indicating that it should be substituted with a name of a temporary
     * file. That file name can either be specified by <code>filename</code> or
     * it will be auto-generated if it is omitted (<code>null</code>). Multiple
     * calls to this method will just add more mappings to the internal map,
     * however, if the specified label has already been used in the internal map
     * of this migration path, then the previous mapping will be removed.
     * 
     * @param tempFileDeclarations
     *            a map containing a paring of temp. file labels and optionally
     *            a file name to be added to the internal map.
     * @return the value previously associated with <code>label</code> or
     *         <code>null</code> if no value was associated with it. However, it
     *         may also indicate that <code>label</code> was actually associated
     *         with <code>null</code>.
     */
    public String addTempFilesDeclaration(String label, String filename) {

	return tempFiles.put(label, filename);
    }

    /**
     * Set the input IO profile for the tool used by this migration path.
     * 
     * @param toolInputProfile
     *            the toolInputProfile to set
     */
    void setToolInputProfile(ToolIOProfile toolInputProfile) {
	this.toolInputProfile = toolInputProfile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolInputProfile()
     */
    public ToolIOProfile getToolInputProfile() {
	return toolInputProfile;
    }

    /**
     * @param toolOutputProfile
     *            the toolOutputProfile to set
     */
    void setToolOutputProfile(ToolIOProfile toolOutputProfile) {
	this.toolOutputProfile = toolOutputProfile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolOutputProfile()
     */
    public ToolIOProfile getToolOutputProfile() {
	return toolOutputProfile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolParameters()
     */
    public Collection<Parameter> getToolParameters() {
	return parameters.values();
    }

    /**
     * Declare a list of parameters that the user of the tool must provide
     * values for in order to execute the tool.
     * 
     * @param toolParameters
     *            Collection of tool parameters that must be initialised and
     *            passed on to the {@link #getCommandLine} method to get the
     *            actual command to execute.
     */
    public void setToolParameters(Collection<Parameter> toolParameters) {
	parameters = new HashMap<String, Parameter>();

	for (Parameter parameter : toolParameters) {
	    parameters.put(parameter.getName(), parameter);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolPresetCategories()
     */
    public Collection<String> getToolPresetCategories() {
	return presets.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getAllToolPresets()
     */
    public Collection<Preset> getAllToolPresets() {
	return presets.values();
    }

    /**
     * Set the presets for this <code>MigrationPathImpl</code> instance.
     * 
     * @param toolPresets
     *            Collection of presets to set.
     */
    void setToolPresets(Collection<Preset> toolPresets) {

	presets = new HashMap<String, Preset>();
	for (Preset preset : toolPresets) {
	    presets.put(preset.getName(), preset);
	}
    }

    public String toString() {
	return "CliMigrationPath: " + sourceFormatURI + " -> "
		+ destinationFormatURI + " Command: " + commandLine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getAsPlanetsPath()
     */
    /*
     * FIXME! KILL, KILL. KILL it would be more suitable to put this in the
     * generic wrapper class. public
     * eu.planets_project.services.datatypes.MigrationPath getAsPlanetsPath() {
     * URI outformat = getDestinationFormat(); URI informat = getSourceFormat();
     * List<Parameter> params = new ArrayList<Parameter>();
     * 
     * for (Preset preset : presets.values()) {
     * params.add(preset.getAsPlanetsParameter()); } return new
     * eu.planets_project.services.datatypes.MigrationPath( informat, outformat,
     * params); }
     * 
     * public void addPreset(Preset preset) { presets.put(preset.getName(),
     * preset); }
     */
}
