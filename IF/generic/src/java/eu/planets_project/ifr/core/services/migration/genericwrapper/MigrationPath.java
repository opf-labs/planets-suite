package eu.planets_project.ifr.core.services.migration.genericwrapper;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.MigrationException;
import eu.planets_project.services.utils.PlanetsLogger;

import java.net.URI;
import java.util.*;

/**
 * Migration path containing all information necessary to execute a (chain of)
 * command-line migration tool(s).
 *
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class MigrationPath implements Cloneable {

    // TODO: Should implement an interface to allow
    // implement new versions to support new
    // configuration file formats.

    private PlanetsLogger log = PlanetsLogger
            .getLogger(MigrationPath.class);


    private URI sourceFormatURI;
    private URI destinationFormatURI;
    private List<TempFile> tempFiles;
    private TempFile tempSourceFile;
    private TempFile tempOutputFile;
    private Map<String, Parameter> parameters;
    private Map<String, Map<String, Collection<Parameter>>> presets;
    private String commandLine;
    private String defaultPresetCategory;
    private String defaultPresetCategoryValue;

    /**
     * The default constructor has default access, as it should only be used by
     * factories.
     */
    MigrationPath() {
        parameters = new HashMap<String, Parameter>();
        presets = new HashMap<String, Map<String, Collection<Parameter>>>();
        tempFiles = new ArrayList<TempFile>();
    }

    /**
     * Test whether the tool expects its input from a temporary input file or
     * standard input. If the response is <code>true</code> then the caller must
     * call {@link #getTempSourceFile()} to get hold of the temp input file
     *
     * @return <code>true</code> if the tool must have its input via a temporary
     *         file and otherwise <code>false</code>
     */
    public boolean useTempSourceFile() {
        return tempSourceFile != null;
    }

    /**
     * Get the temp file object for the input fule
     * @return the temp file
     */
    public TempFile getTempSourceFile() {
        return tempSourceFile;
    }

    /**
     * Set the temp input file to be used
     * @param tempFile the temp file
     */
    public void setTempInputFile(TempFile tempFile) {
        this.tempSourceFile = tempFile;
    }

    /**
     * Test whether the tool needs to write its output to a temporary file or
     * standard input. If the response is <code>true</code> then the caller must
     * call {@link #getTempOutputFile} to get the output file
     *
     * @return <code>true</code> if the tool needs a temporary file to be
     *         created for its output and otherwise <code>false</code>
     */
    public boolean useTempDestinationFile() {
        return tempOutputFile != null;
    }

    /**
     * Get the temp output file
     */
    public TempFile getTempOutputFile() {

        return tempOutputFile;
    }

    /**
     * Set the temp file
     */
    public void setTempOutputFile(TempFile tempOutputFile) {
        this.tempOutputFile = tempOutputFile;
    }

    /**
     * Get the command line with the parameter identifiers substituted with the
     * parameters specified by <code>toolParameters</code>, and any tempfiles
     * replaces with their absolute location.
     *
     * The command line should be ready to feed into the processrunner.
     *
     * Note that all the temp files must have been initialised with File objects
     * by this time, as the absolute location of these files are replaced into
     * the command line
     *
     * @param toolParameters the parameters to the tool
     * @return String containing the processed command line, ready for
     *         execution.
     * @throws MigrationException
     *             if not all necessary parameters, or temporary files were
     *             defined in order to substitute all the identifiers in the
     *             command line.
     */
    public String getCommandLine(Collection<Parameter> toolParameters) throws MigrationException {

        log.info("Entering getCommandLine");
        // Get a complete list of identifiers in the command line.
        final Set<String> usedIdentifiers = getIdentifiers(commandLine);

        //TODO: Work with other presets than the default one
        String defaultpreset = getDefaultPresetCategory();
        //if the tool parameters contain this preset
        boolean setDefault = false;
        for (Parameter toolparam: toolParameters){
            if (toolparam.getName().equals(defaultpreset)){
                setDefault = true;
                Collection<Parameter> presetparams = presets.
                        get(defaultpreset).get(toolparam.getValue());
                toolParameters.addAll(presetparams);
                break;
            }
        }

        //if the tool parameters does not contain this preset
        if (!setDefault){
            //lookup the value of the default preset
            //get the param names and values from there
            Collection<Parameter> defaultparams = presets.get(defaultpreset).get(
                    getDefaultPresetCategoryValue());
            toolParameters.addAll(defaultparams);
        }



        final Set<String> validIdentifiers = getValidParameterNames(toolParameters);

        log.info("Found these valid identifiers");
        log.info(validIdentifiers);

        log.info(tempSourceFile);
        for (TempFile tempfile: tempFiles){
            log.info("Adding tempfile "+tempfile.getCodename()+" as valid identifier");
            validIdentifiers.add(tempfile.getCodename());
        }

        if (useTempSourceFile()){
            log.info("Adding temp input file as valid identifier " + tempSourceFile.getCodename());
            validIdentifiers.add(tempSourceFile.getCodename());
        }

        if (useTempDestinationFile()){
            log.info("Adding temp dest file as valid identifier: "+ tempOutputFile.getCodename());
            validIdentifiers.add(tempOutputFile.getCodename());
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

        String executableCommandLine = commandLine;
        for (Parameter parameter : toolParameters) {
            executableCommandLine =
                    executableCommandLine
                            .replaceAll("#"
                                        + parameter.getName(),
                                        parameter.getValue());
        }

        for (TempFile tempFile : tempFiles) {
            executableCommandLine
                    = executableCommandLine
                    .replaceAll("#"
                                + tempFile.getCodename(),
                                tempFile.getFile().getAbsolutePath());
        }
        if (useTempSourceFile()){
            executableCommandLine
                    = executableCommandLine
                    .replaceAll("#"
                                + tempSourceFile.getCodename(),
                                tempSourceFile.getFile().getAbsolutePath());
        }
        if (useTempDestinationFile()){
            executableCommandLine
                    = executableCommandLine
                    .replaceAll("#" + tempOutputFile.getCodename(),
                                tempOutputFile.getFile().getAbsolutePath());
        }


        return executableCommandLine;
    }


    /**
     * Get a <code>Set</code> containing all the names of parameters from
     * <code>parameters</code> that have been initialised with a value, and are
     * thus valid.
     *
     * @param parameters
     *            a <code>Collection</code> of parameters to get valid parameters
     *            from.
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
     * @param stringWithIdentifiers
     *            a <code>String</code> containing identifiers.
     * @return a <code>Set</code> containing the identifiers found.
     */
    private Set<String> getIdentifiers(String stringWithIdentifiers) {
        Set<String> foundIdentifiers = new HashSet<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(
                stringWithIdentifiers);
        while (stringTokenizer.hasMoreTokens()) {
            String identifier = stringTokenizer.nextToken();
            if (identifier.charAt(0) == '#') {
                identifier = identifier.substring(1);
                foundIdentifiers.add(identifier);
            }
        }
        return foundIdentifiers;
    }

    /**
     * Get the unprocessed command line.
     *
     * @return String containing the unprocessed command line, containing
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
     * {@link #setToolParameters} or {@link #setTempFileDeclarations} methods.
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
     * Get the list of interim temp files.
     *
     * @return a list of temp files.
     */
    public List<TempFile> getTempFileDeclarations() {
        return tempFiles;
    }

    /**
     * Add a list of temp files
     */
    public void addTempFilesDeclarations(
            List<TempFile> tempFileDeclarations) {
        tempFiles.addAll(tempFileDeclarations);

    }

    public void addTempFilesDeclaration(
            TempFile tempFileDeclaration) {
        tempFiles.add(tempFileDeclaration);

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
     * TODO: The presets should have a class of their own
     *
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

    public String toString() {
        return "CliMigrationPath: " + sourceFormatURI + " -> "
               + destinationFormatURI + " Command: " + commandLine;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
