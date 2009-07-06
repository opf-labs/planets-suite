package eu.planets_project.services.migration.dia.impl;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Migration paths created from a XML config file.
 * 
 * @author Asger Blekinge-Rasmussen
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
        tempFiles = new HashMap<String, String>();
        tempInputFile = new HashMap<String, String>();
        tempOutputFile = new HashMap<String, String>();
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
        return !tempInputFile.isEmpty();
    }

    /**
     * Get the desired name of the temporary input file.
     * 
     * @return <code>String</code> containing the desired input file name for
     *         the tool or <code>null</code> if no name has been specified.
     */
    public String getTempInputFileName() {

        final Set<String> inputFileNameKey = tempInputFile.keySet();
        if (!inputFileNameKey.isEmpty()) {
            return tempInputFile.get(inputFileNameKey.iterator().next());
        }
        return null;
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
        return !tempOutputFile.isEmpty();
    }

    /**
     * Get the desired name of the temporary output file.
     * 
     * @return <code>String</code> containing the desired name for the output
     *         file for the tool or <code>null</code> if no name has been
     *         specified.
     */
    public String getTempOutputFileName() {

        final Set<String> outputFileNameKey = tempOutputFile.keySet();
        if (!outputFileNameKey.isEmpty()) {
            return tempOutputFile.get(outputFileNameKey.iterator().next());
        }
        return null;
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
    public Map<String, String> getTempInputFileDeclarations() {
        return tempFiles;
    }

    /**
     * Set a map defining the relationship between the labels in the command
     * line that should be substituted with file names of temporary files with
     * the actual names of these.
     * 
     * @param tempFileDeclarations
     *            a map containing a paring of temp. file labels and optionally
     *            a file name.
     */
    public void setTempFilesDeclarations(
            Map<String, String> tempFileDeclarations) {
        this.tempFiles = tempFileDeclarations;
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
     * G
     * 
     * @return
     */
    public Object getToolPresets() {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        return "CliMigrationPath: " + sourceFormatURI + " -> "
                + destinationFormatURI + " Command: " + commandLine;
    }
}
