/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public interface ToolIOProfile {

    /**
     * Test method for determining whether the tool expects the object to be
     * passed via standard input or output, depending on whether the
     * <code>ToolIOProfile</code> describes an input or output profile.
     * <p/>
     * If this method returns <code>true</code> then the tool associated with
     * this profile expects that the object (i.e. file) are passed through the
     * standard input/output and if it returns <code>false</code> then it
     * expects it to be passed as a temporary file. In the latter case the
     * <code>{@link #getCommandLineFileLabel()}</code> method will contain the
     * label that identifies this file in the command line and the
     * <code>{@link #getDesiredTempFileName()}</code> will contain any desired
     * name of the temp. file, in case it must have a specific name.
     * 
     * @return <code>true</code> if piped IO from standard input/output is
     *         expected and <code>false</code> if a temporary file is expected.
     */
    boolean usePipedIO();

    /**
     * Get a <code>String</code> containing the label which identifies the
     * temporary file in the command line held by the
     * <code>{@link MigrationPath}</code> holding this
     * <code>ToolIOProfile</code> instance.
     * 
     * @return <code>String</code> containing the label or <code>null</code> if
     *         <code>{@link #usePipedIO()}</code> returns <code>true</code>.
     */
    String getCommandLineFileLabel();

    /**
     * Get a <code>String</code> containing the desired file name of the
     * temporary file associated with the label returned by
     * <code>{@link #getCommandLineFileLabel()}</code>. The returned value will
     * be <code>null</code> if no specific file name is desired or if
     * <code>{@link #usePipedIO()}</code> returns <code>true</code>.
     * 
     * @return <code>String</code> containing the desired name of the temporary
     *         file or <code>null</code> if no specific name is desired or
     *         <code>{@link #usePipedIO()}</code> returns <code>true</code>.
     */
    String getDesiredTempFileName();
}
