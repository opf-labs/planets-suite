/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

/**
 * <code>ToolIOProfile</code> instances carries information about the input or
 * output profile of migration tools applied in the command line of a
 * <code>{@link MigrationPath}</code> instance. The profile describes whether
 * the tool expects input or output to be piped through standard input/output or
 * if it should be passed through a temporary file, depending on whether a
 * <code>ToolIOProfile</code> instance describes an input or output profile.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class ToolIOProfileImpl implements ToolIOProfile {

    /**
     * Flag indicating whether a tool expects input or output to be piped
     * through standard input/output.
     */
    private boolean usePipedIO;

    /**
     * If <code>usePipedIO</code> is <code>false</code> then this field may
     * contain a specific filename desired by the tool if necessary.
     */
    private String desiredTempFileName;

    /**
     * If <code>usePipedIO</code> is <code>false</code> then this field must
     * contain a label identifying a temporary input or output file in the
     * command line held by the <code>{@link MigrationPath}</code> instance
     * holding this <code>ToolIOProfileImpl</code> instance.
     * 
     * specific filename desired by the tool if necessary.
     */
    private String tempFileLabel;

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.ToolIOProfile
     * #getCommandLineFileLabel()
     */
    public String getCommandLineFileLabel() {
	if (usePipedIO()) {
	    return null;
	} else {
	    return tempFileLabel;
	}
    }

    /**
     * Set the label which identifies the temporary file in the command line
     * held by the <code>{@link MigrationPath}</code> holding this
     * <code>ToolIOProfile</code> instance.
     * <p/>
     * This value must be <code>null</code> (or un-set) if
     * <code>{@link #usePipedIO()}</code> returns <code>true</code>.
     * 
     * @param tempFileLabel
     *            the label identifying the temporary file.
     */
    void setCommandLineFileLabel(String fileLabel) {
	tempFileLabel = fileLabel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.ToolIOProfile
     * #getDesiredTempFileName()
     */
    public String getDesiredTempFileName() {
	if (usePipedIO()) {
	    return null;
	} else {
	    return desiredTempFileName;
	}
    }

    /**
     * Set a <code>String</code> containing the desired file name of the
     * temporary file associated with the label returned by
     * <code>{@link #getCommandLineFileLabel()}</code>. The desired name must be
     * set to <code>null</code> (or be un-set) if no specific file name is
     * desired by the tool or if <code>{@link #usePipedIO()}</code> returns
     * <code>true</code>.
     * 
     * @param desiredTempFileName
     *            <code>String</code> containing the desired name of the
     *            temporary file.
     */
    void setDesiredTempFileName(String desiredTempFileName) {
	this.desiredTempFileName = desiredTempFileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.ifr.core.services.migration.genericwrapper2.ToolIOProfile
     * #usePipedIO()
     */
    public boolean usePipedIO() {
	return usePipedIO;
    }

    /**
     * Set a flag indicating whether the tool with this IO profile expects the
     * object to be passed via standard input/output depending on whether the
     * <code>ToolIOProfile</code> describes an input or output profile.
     * <p/>
     * If this flag is set <code>true</code> then the tool associated with this
     * profile expects that the object (i.e. file) are passed through the
     * standard input/output and if it set <code>false</code> then it expects it
     * to be passed as a temporary file. In the latter case a command line label
     * for identification of this file must be specified by calling
     * <code>{@link #setCommandLineFileLabel()}</code> and optionally also a
     * desired name for the temporary file, by calling the
     * <code>{@link #setDesiredTempFileName()}</code> if the tool requires a
     * specific file name.
     * 
     * @return
     * 
     * @param usePipedIO
     *            <code>boolean</code> flag indicating whether the tool expects
     *            transferring digital objects via standard input/output.
     */
    void setUsePipedIO(boolean usePipedIO) {
	this.usePipedIO = usePipedIO;
    }
}
