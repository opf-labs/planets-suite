package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Migration path containing all information necessary to execute a (chain of)
 * command-line migration tool(s).
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class MigrationPathImpl implements MigrationPath {

    private ToolIOProfile toolInputProfile;
    private ToolIOProfile toolOutputProfile;
    private URI inputFormatURI;
    private URI outputFormatURI;
    private Map<String, String> tempFiles;
    private Map<String, Parameter> parameters;
    private ToolPresets toolPresets;
    private CommandLine commandLine;

    // TODO: We will probably need more command lines in connection with the
    // self-test functionality.

    /**
     * The default constructor has default access, as it should only be used by
     * factories.
     */
    MigrationPathImpl() {
	this.parameters = new HashMap<String, Parameter>();
	this.tempFiles = new HashMap<String, String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getCommandLine()
     */
    public CommandLine getCommandLine() {
	return this.commandLine;
    }

    /**
     * FIXME! Revisit documentation....
     * 
     * Set the command line attribute of this migration path. The command line
     * may contain tags of the form <code>#my_tag</code> to indicate that either
     * a parameter or a name of a temporary file must be put in place at a
     * specific location. However, these tags must be defined using the
     * {@link #setToolParameters} methods.
     * 
     * @param commandLine
     *            <code>String</code> containing the command line to set.
     */
    void setCommandLine(CommandLine commandLine) {
	this.commandLine = commandLine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getOutputFormat()
     */
    public URI getOutputFormat() {
	return this.outputFormatURI;
    }

    /**
     * Set the output format <code>URI</code> for this
     * <code>MigrationPath</code>.
     * 
     * @param outputFormatURI
     *            output format <code>URI</code> to set.
     */
    void setOutputFormat(URI outputFormatURI) {
	this.outputFormatURI = outputFormatURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getInputFormat()
     */
    public URI getInputFormat() {
	return this.inputFormatURI;
    }

    /**
     * Set the input format <code>URI</code> for this <code>MigrationPath</code>
     * .
     * 
     * @param inputFormatURI
     *            input format <code>URI</code> to set.
     */
    void setInputFormat(URI inputFormatURI) {
	this.inputFormatURI = inputFormatURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getTempFileDeclarations()
     */
    public Map<String, String> getTempFileDeclarations() {
	return this.tempFiles;
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
    void setTempFilesDeclarations(
	    Map<String, String> tempFileDeclarations) {

	this.tempFiles = tempFileDeclarations;
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
    void addTempFilesDeclarations(
	    Map<String, String> tempFileDeclarations) {

	this.tempFiles.putAll(tempFileDeclarations);
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
     * @return the value previously associated with <code>label</code> or
     *         <code>null</code> if no value was associated with it. However, it
     *         may also indicate that <code>label</code> was actually associated
     *         with <code>null</code>.
     */
    String addTempFilesDeclaration(String label, String filename) {

	return this.tempFiles.put(label, filename);
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
	return this.toolInputProfile;
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
	return this.toolOutputProfile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolParameters()
     */
    public Collection<Parameter> getToolParameters() {
	return this.parameters.values();
    }

    /**
     * FIXME! Revise documentation.... Declare a list of parameters that the
     * user of the tool must provide values for in order to execute the tool.
     * 
     * @param toolParameters
     *            Collection of tool parameters that must be initialised and
     *            passed on to the {@link #getCommandLine} method to get the
     *            actual command to execute.
     */
    void setToolParameters(Collection<Parameter> toolParameters) {
	this.parameters = new HashMap<String, Parameter>();

	for (Parameter parameter : toolParameters) {
	    this.parameters.put(parameter.getName(), parameter);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.MigrationPath
     * #getToolPresets()
     */
    public ToolPresets getToolPresets() {

	return this.toolPresets;
    }

    /**
     * Set the presets for this <code>MigrationPathImpl</code> instance.
     * 
     * @param toolPresets
     *            <code>ToolPresets</code> instance describing various parameter
     *            presets that can be applied with the tool used by this
     *            migration path.
     */
    void setToolPresets(ToolPresets toolPresets) {

	this.toolPresets = toolPresets;
    }

    @Override
	public String toString() {

	return "MigrationPathImpl: " + this.inputFormatURI + " -> "
		+ this.outputFormatURI + " Command line: " + this.commandLine;
    }
}
