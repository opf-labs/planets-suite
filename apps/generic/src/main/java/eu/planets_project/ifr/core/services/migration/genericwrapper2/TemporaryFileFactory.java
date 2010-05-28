package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.io.File;

/**
 * Factory for creation of temporary files.
 * 
 * @author Thomas Skou Hansen <tsh@statsbiblioteket.dk>
 */
interface TemporaryFileFactory {

    /**
     * Get the path to the temporary directory where the temporary files,
     * created by this factory, are created.
     * 
     * @return <code>File</code> instance containing the path to the temporary
     *         files created by this factory.
     */
    File getTempFileDir();

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with a completely random name. The file will be located in the temporary
     * file folder described by the concrete implementation.
     * 
     * @return <code>File</code> instance identifying a randomly named file in
     *         the temporary file folder.
     */
    File prepareRandomNamedTempFile();

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with a completely random name which includes the string specified by
     * <code>humanReadableID</code>. Providing an ID can be very useful if it
     * becomes necessary to debug the application.
     * <p/>
     * The file will be located in the temporary file folder described by the
     * concrete implementation.
     * 
     * @param humanReadableID
     *            String containing a human readable ID to add to the randomly
     *            generated filename.
     * 
     * @return <code>File</code> instance identifying a randomly named
     *         (including a human readable ID) file in the temporary file
     *         folder.
     */
    File prepareRandomNamedTempFile(String humanReadableID);

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with specific name specified by <code>desiredName</code>.
     * <p/>
     * The file will be located in the temporary file folder described by the
     * concrete implementation. This may mean that the path the file contains
     * random names even though the actual filename has been specified here.
     * 
     * @param desiredName
     *            String containing desired name of the temporary file. If the
     *            name is a file path rather than a simple name then the path
     *            will be added to the temporary file path.
     * 
     * @return <code>File</code> instance identifying a randomly named
     *         (including a human readable ID) file in the temporary file
     *         folder.
     * 
     * @throws IllegalArgumentException
     *             if <code>desiredName</code> is either <code>null</code> or an
     *             empty string.
     */
    File prepareTempFile(String desiredName);
}
