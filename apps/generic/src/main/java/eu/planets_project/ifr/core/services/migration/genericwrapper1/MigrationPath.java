package eu.planets_project.ifr.core.services.migration.genericwrapper1;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions.MigrationException;

import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

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

    private Logger log = Logger.getLogger(MigrationPath.class.getName());

    private URI sourceFormatURI;
    private URI destinationFormatURI;
    private List<TempFile> tempFiles;
    private Map<String, String> newTempFiles;
    private TempFile tempSourceFile;
    private TempFile tempOutputFile;
    private Map<String, Parameter> parameters;
    private Map<String, Preset> presets;
    private List<String> commandLine;
    private String defaultPreset;

    /**
     * The default constructor has default access, as it should only be used by
     * factories.
     */
    MigrationPath() {
        this.parameters = new HashMap<String, Parameter>();
        this.presets = new HashMap<String, Preset>();
        this.tempFiles = new ArrayList<TempFile>();
        this.newTempFiles = new HashMap<String, String>();
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
        return this.tempSourceFile != null;
    }

    /**
     * Get the temp file object for the input fule
     * 
     * @return the temp file
     */
    public TempFile getTempSourceFile() {
        return this.tempSourceFile;
    }

    /**
     * Set the temp input file to be used
     * 
     * @param tempFile
     *            the temp file
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
        return this.tempOutputFile != null;
    }

    /**
     * Get the temp output file
     * @return the temporary output file
     */
    public TempFile getTempOutputFile() {
        return this.tempOutputFile;
    }

    /**
     * Set the temp file
     * @param tempOutputFile 
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
     * @param toolParameters
     *            the parameters to the tool
     * @return String containing the processed command line, ready for
     *         execution.
     * @throws MigrationException
     *             if not all necessary parameters, or temporary files were
     *             defined in order to substitute all the identifiers in the
     *             command line.
     */
    public List<String> getCommandLine(Collection<Parameter> toolParameters)
            throws MigrationException {

        this.log.info("Entering getCommandLine");
        // Get a complete list of identifiers in the command line.
        final Set<String> usedIdentifiers = getIdentifiers(this.commandLine);

        // TODO: Work with other presets than the default one

        String defaultpreset = getDefaultPreset();
        // if the tool parameters contain this preset
        boolean setDefault = false;
        for (Parameter toolparam : toolParameters) {
            if (toolparam.getName().equals(defaultpreset)) {
                setDefault = true;
                Collection<Parameter> presetparams = this.presets.get(defaultpreset)
                        .getParameters(toolparam.getValue());
                toolParameters.addAll(presetparams);
                break;
            }
        }

        // if the tool parameters does not contain this preset
        if (!setDefault) {
            // lookup the value of the default preset
            // get the param names and values from there
            Preset preset = this.presets.get(defaultpreset);
            if (preset != null) {
                Collection<Parameter> defaultparams = preset
                        .getDefaultParameters();
                toolParameters.addAll(defaultparams);
            } else {
                // There is no default preset
            }
        }

        final Set<String> validIdentifiers = getValidParameterNames(toolParameters);

        this.log.info("Found these valid identifiers");
        this.log.info(validIdentifiers.toString());

        this.log.info(this.tempSourceFile.toString());
        for (TempFile tempfile : this.tempFiles) {
            this.log.info("Adding tempfile " + tempfile.getCodename()
                    + " as valid identifier");
            validIdentifiers.add(tempfile.getCodename());
        }

        if (useTempSourceFile()) {
            this.log.info("Adding temp input file as valid identifier "
                    + this.tempSourceFile.getCodename());
            validIdentifiers.add(this.tempSourceFile.getCodename());
        }

        if (useTempDestinationFile()) {
            this.log.info("Adding temp dest file as valid identifier: "
                    + this.tempOutputFile.getCodename());
            validIdentifiers.add(this.tempOutputFile.getCodename());
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
        for (String cmd : this.commandLine) {
            for (Parameter parameter : toolParameters) {
                cmd = cmd.replaceAll("#" + parameter.getName(), parameter
                        .getValue());
            }

            for (TempFile tempFile : this.tempFiles) {
                cmd = cmd.replaceAll("#" + tempFile.getCodename(), tempFile
                        .getFile().getAbsolutePath());
            }
            if (useTempSourceFile()) {
                cmd = cmd.replaceAll("#" + this.tempSourceFile.getCodename(),
                        this.tempSourceFile.getFile().getAbsolutePath());
            }
            if (useTempDestinationFile()) {
                cmd = cmd.replaceAll("#" + this.tempOutputFile.getCodename(),
                        this.tempOutputFile.getFile().getAbsolutePath());
            }
            executableCommandLine.add(cmd);
        }

        return executableCommandLine;
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
    private Set<String> getValidParameterNames(@SuppressWarnings("hiding") Collection<Parameter> parameters) {
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

    /**
     * Get the unprocessed command line.
     * 
     * @return String containing the unprocessed command line, containing
     *         parameter identifiers/keys.
     */
    public List<String> getCommandLine() {
        return this.commandLine;
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

    /**
     * Get the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @return ID of the default preset category
     */
    public String getDefaultPreset() {
        return this.defaultPreset;
    }

    /**
     * Set the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @param defaultPreset
     *            the ID of the default preset category.
     */
    public void setDefaultPreset(String defaultPreset) {
        this.defaultPreset = defaultPreset;
    }

    /**
     * Get the destination format <code>URI</code> of this migration path.
     * 
     * @return <code>URI</code> identifying the destination format of this
     *         migration path.
     */
    public URI getDestinationFormat() {
        return this.destinationFormatURI;
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
        return this.sourceFormatURI;
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
        return this.tempFiles;
    }

    /**
     * Add a list of temp files
     * @param tempFileDeclarations 
     * @deprecated {@link MigrationPath#addTempFilesDeclarations(Map)} will replace this method
     */
    @Deprecated
    public void addTempFilesDeclarations(List<TempFile> tempFileDeclarations) {
        this.tempFiles.addAll(tempFileDeclarations);

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

        this.newTempFiles.putAll(tempFileDeclarations);
    }

    //TODO: Kill when the time is right.
    /**
     * @param tempFileDeclaration
     */
    @Deprecated
    public void addTempFilesDeclaration(TempFile tempFileDeclaration) {
        this.tempFiles.add(tempFileDeclaration);

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
     * @param label 
     * @param filename 
     * 
     * @return the value previously associated with <code>label</code> or
     *         <code>null</code> if no value was associated with it. However, it
     *         may also indicate that <code>label</code> was actually associated
     *         with <code>null</code>.
     */
    public String addTempFilesDeclaration(String label, String filename) {

        return this.newTempFiles.put(label, filename);
    }

    /**
     * Get all the parameters that must be initialised in order to execute the
     * command line. The returned <code>Parameter</code> instances have no value
     * specified, thus, their values must be initialised prior calling the
     * {@link #getCommandLine} method to obtain the actual command line to
     * execute.
     * 
     * @return <code>Collection</code> containing an <code>Parameter</code>
     *         instance for each parameter that must be specified in order to
     *         execute the command line of this migration path.
     */
    public Collection<Parameter> getToolParameters() {
        return this.parameters.values();
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
        for (Parameter parameter : toolParameters) {
            this.parameters.put(parameter.getName(), parameter);
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
        return this.presets.keySet();
    }

    @Override
	public String toString() {
        return "CliMigrationPath: " + this.sourceFormatURI + " -> "
                + this.destinationFormatURI + " Command: " + this.commandLine;
    }

    // TODO: clone() will probably be obsolete with the introduction of the new
    // version of the migration path factory
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // To change body of overridden methods use File |
                              // Settings | File Templates.
    }

    /**
     * @return the Migration Path
     */
    public eu.planets_project.services.datatypes.MigrationPath getAsPlanetsPath() {
        URI outformat = getDestinationFormat();
        URI informat = getSourceFormat();
        List<Parameter> params = new ArrayList<Parameter>();

        for (Preset preset : this.presets.values()) {
            params.add(preset.getAsPlanetsParameter());
        }
        return new eu.planets_project.services.datatypes.MigrationPath(
                informat, outformat, params);
    }

    /**
     * @param preset
     */
    public void addPreset(Preset preset) {
        this.presets.put(preset.getName(), preset);
    }
}
