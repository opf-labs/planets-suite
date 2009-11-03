package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.io.File;

import eu.planets_project.services.utils.FileUtils;

/**
 * Temporary file factory working with the PLANETS IF framework.
 * 
 * @author Thomas Skou Hansen <tsh@statsbiblioteket.dk>
 */
class J2EETempFileFactory implements TemporaryFileFactory {

    final File tempFileDir;

    /**
     * Create a factory for creation of temporary files. The files will be
     * created in the default system dir used by <code>{@link FileUtils}</code>
     * .
     * <p/>
     * This constructor accepts a human readable ID which will be included in
     * the name of the directory containing the temporary files produced. The
     * directory name will also have a random number added to avoid name
     * collisions with existing directories.
     * 
     * @param tempFileDirID
     *            Human readable ID for the temp. file directory.
     */
    J2EETempFileFactory(String tempFileDirID) {

        final String randomizedTempFileDirName = FileUtils
                .randomizeFileName(tempFileDirID);
        tempFileDir = FileUtils
                .createWorkFolderInSysTemp(randomizedTempFileDirName);
    }

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with a completely random name. The file will be located in the temporary
     * file folder described by the constructor of this class.
     * 
     * @return <code>File</code> instance identifying a randomly named file in
     *         the temporary file folder.
     */
    public File prepareRandomNamedTempFile() {

        return prepareRandomNamedTempFile("");
    }

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with a completely random name which includes the string specified by
     * <code>humanReadableID</code>. Providing an ID can be very useful if it
     * becomes necessary to debug the application.
     * <p/>
     * The file will be located in the temporary file folder described by the
     * constructor of this class.
     * 
     * @param humanReadableID
     *            String containing a human readable ID to add to the randomly
     *            generated filename.
     * @return <code>File</code> instance identifying a randomly named
     *         (including a human readable ID) file in the temporary file
     *         folder.
     */
    public File prepareRandomNamedTempFile(String humanReadableID) {
        final String randomizedName = FileUtils
                .randomizeFileName(humanReadableID);

        return prepareTempFile(randomizedName);
    }

    /**
     * Prepare a <code>File</code> object representing a (not yet created) file
     * with specific name specified by <code>desiredName</code>.
     * <p/>
     * The file will be located in the temporary file folder described by the
     * constructor of this class. This may mean that the path the file contains
     * random names even though the actual filename has been specified here.
     * 
     * @param desiredName
     *            String containing desired name of the temporary file. If the
     *            name is a file path rather than a simple name then the path
     *            will be added to the temporary file path.
     * @return <code>File</code> instance identifying a randomly named
     *         (including a human readable ID) file in the temporary file
     *         folder.
     */
    public File prepareTempFile(String desiredName) {

        if (desiredName == null || desiredName.length() == 0) {
            throw new IllegalArgumentException(
                    String
                            .format(
                                    "The desired name of a temporary file must not be null or empty (was: '%s')",
                                    desiredName));
        }

        return new File(tempFileDir, desiredName);
    }
}
