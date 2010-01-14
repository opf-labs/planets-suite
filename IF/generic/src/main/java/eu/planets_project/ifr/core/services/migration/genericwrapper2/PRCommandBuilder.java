/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.ConfigurationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ProcessRunner;

/**
 * This utility class provides methods for the building a command to be executed
 * with the <code>{@link ProcessRunner}</code>.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class PRCommandBuilder {

    private final Logger log = Logger.getLogger(PRCommandBuilder.class
	    .getName());

    private final Collection<Parameter> environmentParameters;

    /**
     * Create a <code>PRCommandBuilder</code> for construction of commands,
     * using the environment parameters specified by
     * <code>environmentParameters</code>.
     * 
     * @param envrionmentParameters
     *            A parameter collection containing environment information such
     *            as absolute paths to executables used in the commands to be
     *            built.
     */
    PRCommandBuilder(Collection<Parameter> environmentParameters) {
	this.environmentParameters = environmentParameters;
    }

    /**
     * TODO: Revisit this documentation....
     * 
     * Build a command to be executed with the
     * <code>{@link ProcessRunner}</code>based on a
     * <code>{@link CommandLine}</code> instrance, a
     * <code>{@link ToolPresets}</code> instance and a collection of
     * <code>{@link Parameter}</code> instances provided by the caller.
     * 
     * @param commandLineTemplate
     * @param toolPresets
     * @param toolParameters
     * @param tempFileMappings
     * @return
     * @throws MigrationException
     *             if any non-configuration related errors are encountered.
     * @throws ConfigurationException
     *             if any configuration related errors are encountered.
     */
    List<String> buildCommand(MigrationPath migrationPath,
	    Collection<Parameter> toolParameters,
	    Map<String, File> tempFileMappings) throws MigrationException,
	    ConfigurationException {

	final CommandLine commandLine = migrationPath.getCommandLine();

	// Get a complete list of identifiers used in the command line.
	final Set<String> commandLineIdentifiers = getIdentifiers(commandLine);

	// Get the key-value pairs from toolParameters that are used in the
	// command line.
	Map<String, String> identifierMap = getRelevantParameterMappings(
		commandLineIdentifiers, toolParameters);

	// Overwrite any previous registered identifier mappings with a preset
	// if a preset has been specified in the tool parameters or add settings
	// from the default preset if no parameters were specified by the
	// caller.
	identifierMap = addPresetParameters(identifierMap, toolParameters,
		migrationPath.getToolPresets());

	identifierMap = addTempFileMappings(identifierMap, tempFileMappings);

	// TODO: Check the parameters and filename mappings for injection
	// attacks by sanity checking the parameters -
	// throw exception in case of failure. However, it may be safe omitting
	// the test for the file name mappings, as they are solely based on the
	// configuration file and what the generic wrapper is doing.

	// Verify that all identifiers in commandLineIdentifiers are associated
	// with a value in identifierMap
	if (!identifierMap.keySet().containsAll(commandLineIdentifiers)) {
	    commandLineIdentifiers.removeAll(identifierMap.keySet());
	    throw new ConfigurationException("Cannot build the command line. "
		    + "Missing values for these identifiers: "
		    + commandLineIdentifiers);
	}

	// Replace the identifiers in the command line fragments with their
	// associated value (parameter, temp. file path etc.).
	final List<String> executableCommandLine = new ArrayList<String>();

	// TODO/FIXME! We still need to add the precise (absolute) file path to
	// the command, however, the mechanism for obtaining this has not yet
	// been delivered from the IF team. It is probably possible to obtain by
	// using command line parameters which the generic wrapper class defines
	// based on information on the environment. Thus a comand line in the
	// config would look something like this: #commandPath/sh -c
	// #toolPath/myMigrationTool Making "commandPath" and "toolPath"
	// reserved names.
	executableCommandLine.add(commandLine.getCommand());
	executableCommandLine.addAll(commandLine.getParameters());

	for (int commandFragmentIdx = 0; commandFragmentIdx < executableCommandLine
		.size(); commandFragmentIdx++) {

	    final String commandFragment = executableCommandLine
		    .get(commandFragmentIdx);

	    final StringTokenizer stringTokenizer = new StringTokenizer(
		    commandFragment);

	    String substitudedString = "";
	    while (stringTokenizer.hasMoreTokens()) {

		String token = stringTokenizer.nextToken();
		if (token.charAt(0) == '#') {
		    token = token.substring(1);
		    substitudedString += identifierMap.get(token);
		} else {
		    substitudedString += token;
		}

		substitudedString += (stringTokenizer.hasMoreTokens()) ? " "
			: "";
	    }

	    // Replace the command line fragment with the one where the
	    // identifiers have been replaced with their respective values.
	    executableCommandLine.remove(commandFragmentIdx);
	    executableCommandLine.add(commandFragmentIdx, substitudedString);
	}

	return executableCommandLine;
    }

    /**
     * Add the temporary file mappings from <code>tempFileMappings</code> to the
     * <code>identifierMap</code> and throw an exception if any mapping in
     * <code>identifierMap</code> is about to be overwritten.
     * 
     * @param identifierMap
     *            A key-value map to add label-file name mappings to.
     * @param tempFileMappings
     *            A key-value map containing labels associated with file paths.
     * @return <code>identifierMap</code> having the
     *         <code>tempFileMappings</code> added.
     * @throws MigrationException
     *             if any key-value mapping in <code>identifierMap</code> would
     *             be overwritten by this operation or if any problems are
     *             encountered while handling the temporary files.
     */
    private Map<String, String> addTempFileMappings(
	    Map<String, String> identifierMap,
	    Map<String, File> tempFileMappings) throws MigrationException {

	for (String tempFileLabel : tempFileMappings.keySet()) {
	    if (identifierMap.containsKey(tempFileLabel)) {
		throw new MigrationException(String.format(
			"The identifier map already contains an element with "
				+ "the key '%s'. Cannot add the temporay file "
				+ "mapping '%s = %s'.", tempFileLabel,
			tempFileLabel, tempFileMappings.get(tempFileLabel)));
	    }

	    try {
		identifierMap.put(tempFileLabel, tempFileMappings.get(
			tempFileLabel).getCanonicalPath());
	    } catch (SecurityException se) {
		throw new MigrationException(
			String.format(
				"Failed accessing the canonical file path of the "
					+ "temporary file labeled '%s'",
				tempFileLabel), se);
	    } catch (IOException ioe) {
		throw new MigrationException(
			String.format(
				"Failed accessing the canonical file path of the "
					+ "temporary file labeled '%s'",
				tempFileLabel), ioe);
	    }
	}

	return identifierMap;
    }

    /**
     * Pick all key-value pairs from the parameters in
     * <code>toolParameters</code> and <code>environmentParameters</code> (given
     * at construction time), for each parameter which name exists in
     * <code>relevantIdentifiers</code>.
     * 
     * @param relevantIdentifiers
     *            A set of identifiers to look for in
     *            <code>toolParameters</code>.
     * @param toolParameters
     *            A collection of <code>Parameter</code> instances to pick
     *            key-value pairs from.
     * @return A <code>Map</code> containing all the key-value pairs for each
     *         <code>Parameter</code> in <code>toolParameters</code> which name
     *         exists in <code>relevantIdentifiers</code>.
     */
    private Map<String, String> getRelevantParameterMappings(
	    Set<String> relevantIdentifiers,
	    Collection<Parameter> toolParameters) {

	// Add all parameters from toolParameters that are listed in
	// relevantIdentifiers.
	final HashMap<String, String> parameterMappings = new HashMap<String, String>();
	for (Parameter parameter : toolParameters) {
	    if (relevantIdentifiers.contains(parameter.getName())) {
		parameterMappings
			.put(parameter.getName(), parameter.getValue());
	    }
	}

	// Add all parameters from the attribute environmentParameters that are
	// listed in relevantIdentifiers.
	for (Parameter environmentParameter : environmentParameters) {
	    if (relevantIdentifiers.contains(environmentParameter.getName())) {
		parameterMappings.put(environmentParameter.getName(),
			environmentParameter.getValue());
	    }
	}

	return parameterMappings;
    }

    /**
     * TODO: Revisit doc....
     * 
     * Add the <code>toolParameters</code> and a parameter set (a set of preset
     * settings) from <code>toolPresets</code> if <code>toolParameters</code>
     * has a preset variable set.
     * <p/>
     * If any of the parameters in <code>toolParameters</code> are used in a
     * selected preset, then these parameter values will be overwritten and if
     * more than one preset is specified by <code>toolParameters</code> then an
     * exception will be thrown.
     * 
     * @param identifierMap
     *            A map to add the parameter name and value pairs to.
     * @param toolParameters
     *            A collection of <code>Parameter</code> instances to add to
     *            <code>identifierMap</code> as key-value pairs.
     * @param toolPresets
     *            A number of predefined tool presets to search for any preset
     *            specified in <code>toolParameters</code>.
     * @return <code>identifierMap</code> with all relevant parameters added.
     * @throws ConfigurationException
     */
    private Map<String, String> addPresetParameters(
	    Map<String, String> identifierMap,
	    Collection<Parameter> toolParameters, ToolPresets toolPresets)
	    throws ConfigurationException {

	// See if the parameters specifies that a preset should be applied.
	PresetSetting presetSetting = null;
	String presetCategoryID = null;
	if (toolParameters.isEmpty()) {
	    // No parameters have been specified. See if there is a default
	    // preset available.
	    presetCategoryID = toolPresets.getDefaultPresetID();
	    if (presetCategoryID != null) {
		presetSetting = toolPresets.getPreset(presetCategoryID)
			.getDefaultSetting();
	    }
	} else {
	    // We have parameters. See if any of them specifies that a preset
	    // should be applied.
	    final Collection<String> presetCategories = toolPresets
		    .getToolPresetNames();
	    for (Parameter parameter : toolParameters) {
		if (presetCategories.contains(parameter.getName())) {
		    // The parameter name specifies a valid preset category.
		    if (presetSetting == null) {
			presetCategoryID = parameter.getName();
			final Preset preset = toolPresets
				.getPreset(presetCategoryID);
			presetSetting = preset.getSetting(parameter.getValue());
			if (presetSetting == null) {
			    throw new ConfigurationException(String.format(
				    "The preset '%s = %s' has not been defined"
					    + " in the configuration.",
				    parameter.getName(), parameter.getValue()));
			}
		    } else {
			throw new ConfigurationException(String.format(
				"More than one preset was specified in the "
					+ "parameters. Found '%s"
					+ " = %s' and '%s = %s'.",
				presetCategoryID, presetSetting.getName(),
				parameter.getName(), parameter.getValue()));
		    }
		}
	    }
	}

	// Add the parameters from any selected preset, thus overriding any
	// parameters provided by toolParameters.
	if (presetSetting != null) {
	    for (Parameter parameter : presetSetting.getParameters()) {

		final String previousValue = identifierMap.put(parameter
			.getName(), parameter.getValue());
		if (previousValue != null) {
		    log
			    .warning(String
				    .format(
					    "The parameter '%s' was specified"
						    + "by the caller while also specifying usage of "
						    + "the preset '%s = %s'. The specified value: '%s'"
						    + " has now been overwritten with the value: '%s'"
						    + " from the preset.",
					    parameter.getName(),
					    presetCategoryID, presetSetting
						    .getName(), previousValue,
					    parameter.getValue()));
		}
	    }
	}

	return identifierMap;
    }

    /**
     * Get the names of all identifiers (all words with a leading '#') of the
     * form <code>#myIdentifier</code> found in the parameters in
     * <code>commandLine</code>. If the previous example was found in the
     * string, then the returned set would contain the string
     * &quot;myIdentifier&quot;.
     * 
     * @param commandLine
     *            a <code>CommandLine</code> instance to find identifiers in.
     * @return a <code>Set</code> containing the identifiers found.
     */
    private Set<String> getIdentifiers(CommandLine commandLine) {

	final Set<String> foundIdentifiers = new HashSet<String>();
	final List<String> commandParameterStrings = new ArrayList<String>();
	commandParameterStrings.addAll(commandLine.getParameters());
	commandParameterStrings.add(commandLine.getCommand());

	for (String stringWithIdentifiers : commandParameterStrings) {

	    StringTokenizer stringTokenizer = new StringTokenizer(
		    stringWithIdentifiers);

	    while (stringTokenizer.hasMoreTokens()) {
		String token = stringTokenizer.nextToken();
		if (token.charAt(0) == '#') {
		    token = token.substring(1);
		    foundIdentifiers.add(token);
		}
	    }
	}

	return foundIdentifiers;
    }

}
