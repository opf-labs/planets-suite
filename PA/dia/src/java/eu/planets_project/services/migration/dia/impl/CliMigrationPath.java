package eu.planets_project.services.migration.dia.impl;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Migration path containing all information necessary to execute a (chain of)
 * command-line migration tool(s).
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class CliMigrationPath {

    private URI sourceFormatURI;
    private URI destinationFormatURI;
    private Map<String, String> tempFiles;
    private Map<String, String> tempInputFile;
    private Map<String, String> tempOutputFile;
    private Map<String, Parameter> parameters;
    private Map<String, Map<String, Collection<Parameter>>> presets;
    private String commandLine;
    private String defaultPresetCategory;
    private String defaultPresetCategoryValue;

    /**
     */
    CliMigrationPath(URI sourceFormatURI, URI destinationFormatURI,
	    String commandLine) {
	this();
	this.sourceFormatURI = sourceFormatURI;
	this.destinationFormatURI = destinationFormatURI;
	this.commandLine = commandLine;
    }

    /**
     * The default constructor has default access, as it should only be used by
     * factories.
     */
    CliMigrationPath() {
	parameters = new HashMap<String, Parameter>();
	presets = new HashMap<String, Map<String, Collection<Parameter>>>();
    }

    /**
     * Test whether the tool expects its input from a temporary input file or
     * standard input. If the response is <code>true</code> then the caller must
     * call {@link getTempInputFileName} to verify whether the tool expects that
     * the input file has a special name or not.
     * 
     * @return <code>true</code> if the tool must have its input via a temporary
     *         file and otherwise <code>false</code>
     */
    public boolean useTempSourceFile() {
	return (tempInputFile == null) ? false : !tempInputFile.isEmpty();
    }

    /**
     * Get the label identifying the temporary input file in the command line
     * string and the desired name of the actual input file.
     * 
     * @return <code>Map</code> containing the label of the temporary input file
     *         and the associated name of the file desired by the tool. However,
     *         the file name may be <code>null</code> if no name has been
     *         associated with the label.
     */
    public Map<String, String> getTempInputFileLabelAndName() {
	return tempInputFile;
    }

    /**
     * Set the label and the optional file name for the temporary input file
     * needed by the migration tool.
     * 
     * @param tempFileLabelAndName
     *            <code>Map</code> associating the label with the name of the
     *            temporary file.
     */
    public void setTempInputFile(Map<String, String> tempFileLabelAndName) {
	this.tempInputFile = tempFileLabelAndName;
    }

    /**
     * Test whether the tool needs to write its output to a temporary file or
     * standard input. If the response is <code>true</code> then the caller must
     * call {@link getTempOutputFileName} to verify whether the tool expects
     * that the output file has a special name or not.
     * 
     * @return <code>true</code> if the tool needs a temporary file to be
     *         created for its output and otherwise <code>false</code>
     */
    public boolean useTempDestinationFile() {
	return (tempOutputFile == null) ? false : !tempOutputFile.isEmpty();
    }

    /**
     * Get the label identifying the temporary output file in the command line
     * string and the desired name of the actual input file.
     * 
     * @return <code>Map</code> containing the label of the temporary output
     *         file and the associated name of the file desired by the tool.
     *         However, the file name may be <code>null</code> if no name has
     *         been associated with the label.
     */
    public Map<String, String> getTempOutputFileName() {

	return tempOutputFile;
    }

    /**
     * Set the label and the optional file name for the temporary output file
     * needed by the migration tool.
     * 
     * @param tempFileLabelAndName
     *            <code>Map</code> associating the label with the name of the
     *            temporary file.
     */
    public void setTempOutputFile(Map<String, String> tempFileLabelAndName) {
	this.tempOutputFile = tempFileLabelAndName;
    }

    /**
     * Get the command line with the parameter identifiers substituted with the
     * parameters specified by <code>toolParameters</code>.
     * 
     * @param toolParameters
     * @return String containing the processed command line, ready for
     *         execution.
     */
    public String getConmmandLine(List<Parameter> toolParameters) {
	// TODO: Substitute parameters
	// TODO: Check for injection attacks by sanity checking the parameters -
	// throw exception in case of failure.
	return commandLine;
    }

    /**
     * Get the un-processed command line.
     * 
     * @return String containing the un-processed command line, containing
     *         parameter identifiers/keys.
     */
    public String getCommandLine() {
	return commandLine;
    }

    /**
     * Set the command line attribute of this migration path. The command line
     * may contain tags of the form <code>%my_tag%</code> to indicate that
     * either a parameter or a name of a temporary file must be put in place at
     * a specific location. However, these tags must be defined using the
     * {@link setToolParameters} or {@link setTempFileDeclarations} methods.
     * 
     * @param commandLine
     *            <code>String</code> containing the command line to set.
     */
    public void setCommandLine(String commandLine) {
	this.commandLine = commandLine;
    }

    /**
     * Get the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @return ID of the default preset category
     */
    public String getDefaultPresetCategory() {
	return defaultPresetCategory;
    }

    /**
     * Set the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @param defaultPresetCategory
     *            the ID of the default preset category.
     */
    public void setDefaultPresetCategory(String defaultPresetCategory) {
	this.defaultPresetCategory = defaultPresetCategory;
    }

    /**
     * Get the ID of the default preset category value to apply together with
     * the default preset category ID, if no preset or parameters are specified
     * by the user of this migration path.
     * 
     * @return ID of the default preset category value
     */
    public String getDefaultPresetCategoryValue() {
	return defaultPresetCategoryValue;
    }

    /**
     * Set the ID of the default preset category value to apply together with
     * the default preset category ID, if no preset or parameters are specified
     * by the user of this migration path.
     * 
     * @param defaultPresetCategoryValue
     *            the ID of the default preset category value.
     */
    public void setDefaultPresetCategoryValue(String defaultPresetCategoryValue) {
	this.defaultPresetCategoryValue = defaultPresetCategoryValue;
    }

    /**
     * Get the destination format <code>URI</code> of this migration path.
     * 
     * @return <code>URI</code> identifying the destination format of this
     *         migration path.
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

    /**
     * Get the source format <code>URI</code> of this migration path.
     * 
     * @return <code>URI</code> identifying the source format of this migration
     *         path.
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

    /**
     * Get a map defining the relationship between the labels in the command
     * line that should be substituted with file names of temporary files with
     * the actual names of these.
     * 
     * @return a map containing a paring of temp. file labels and optionally a
     *         file name.
     */
    public Map<String, String> getTempFileDeclarations() {
	return tempFiles;
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

	for (String tempFileLabel : tempFileDeclarations.keySet()) {
	    addTempFilesDeclaration(tempFileLabel, tempFileDeclarations
		    .get(tempFileLabel));
	}
    }

    /**
     * Get all the parameters that must be initialised in order to execute the
     * command line. The returned <code>Parameter</code> instances have no value
     * specified, thus, their values must be initialised prior calling the
     * {@link getCommandLine} method to obtain the actual command line to
     * execute.
     * 
     * @return <code>Collection</code> containing an <code>Parameter</code>
     *         instance for each parameter that must be specified in order to
     *         execute the command line of this migration path.
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
     *            passed on to the {@link getCommandLine} method to get the
     *            actual command to execute.
     */
    public void setToolParameters(Collection<Parameter> toolParameters) {
	for (Parameter parameter : toolParameters) {
	    parameters.put(parameter.getName(), parameter);
	}
    }

    /**
     * Get a collection of all available preset categories for this migration
     * path.
     * 
     * @return <code>Collection</code> containing the names/IDs of all the
     *         available preset categories.
     */
    public Collection<String> getToolPresetCategories() {
	return presets.keySet();
    }

    /**
     * Get a collection of valid values of a preset category.
     * 
     * @param presetCategory
     *            <code>String</code> identifying the preset category to get the
     *            valid values for.
     * @return <code>Collection</code> of valid preset values for the specified
     *         category.
     */
    public Collection<String> getToolPresetValues(String presetCategory) {
	return presets.get(presetCategory).keySet();
    }

    /**
     * Get the pre-configured parameters for the specified
     * <code>presetCategory</code> and <code>presetValue</code>. A command-line
     * will be configured to behave as the preset specifies when the returned
     * parameters are passed on to {@link getCommandLine}.
     * 
     * @param presetCategory
     *            <code>String</code> identifying the preset category that the
     *            <code>presetValue</code> belongs to.
     * @param presetValue
     *            <code>String</code> identifying the collection of
     *            pre-configured parameters to get.
     * @return a <code>Collection</code> og pre-configured
     *         <code>Parameter</code> instances or <code>null</code> if no
     *         preset has been configured for the specified combination of
     *         preset category and value.
     * @throws NullPointerException
     *             if <code>presetCategory</code> has not been defined in the
     *             configuration.
     */
    public Collection<Parameter> getToolPresetParameters(String presetCategory,
	    String presetValue) {
	return presets.get(presetCategory).get(presetValue);
    }

    /**
     * Add a preset to this migration path. A preset belongs to a category and
     * its value is essentially just a collection of pre-defined parameters for
     * the command line to be wrapped. An example of a category could be
     * <code>&quot;quality&quot;</code> and an example of a category value could
     * be <code>&quot;best&quot;</code>. One of the <code>Parameter</code>
     * instances in the associated <code>categoryValueParameters</code> could
     * for example have the name <code>&quot;commandargs&quot;</code> and the
     * value <code>&quot;-v -a42 -z&quot;</code>
     * 
     * @param category
     *            <code>String</code> identifying the category of the preset to
     *            modify.
     * @param categoryValue
     *            <code>String</code> identifying the value of the category that
     *            should be modified.
     * @param categoryValueParameters
     *            a <code>Collection</code> containing the pre-defined
     *            parameters for the preset.
     * @return a <code>Collection</code> containing any parameters previously
     *         associated with the specified category value and category
     *         otherwise <code>null</code>
     * @throws NullPointerException
     *             if any of the parameters are <code>null</code>
     */
    public Collection<Parameter> addToolPreset(String category,
	    String categoryValue, Collection<Parameter> categoryValueParameters) {

	if (category == null || categoryValue == null || categoryValue == null) {
	    throw new NullPointerException(
		    "At least one parameter is null. category = " + category
			    + "  categoryValue = " + categoryValue
			    + "  categoryValueParameters = "
			    + categoryValueParameters);
	}

	// Ensure that the category exists.
	if (presets.get(category) == null) {
	    presets.put(category, new HashMap<String, Collection<Parameter>>());
	}

	// Add the value and its parameters to the category.
	final Map<String, Collection<Parameter>> categoryValueMappings = presets
		.get(category);
	return categoryValueMappings
		.put(categoryValue, categoryValueParameters);
    }

    public String toString() {
	return "CliMigrationPath: " + sourceFormatURI + " -> "
		+ destinationFormatURI + " Command: " + commandLine;
    }

}
