package eu.planets_project.services.datatypes;

import eu.planets_project.services.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Static factory methods for content creation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Content {

    private Content() {/* enforce non-instantiability */};

    /*
     * We use static factory methods to provide named constructors for the
     * different kinds of content instances:
     */

    /**
     * Create content by reference.
     * @param reference The URL reference to the actual content
     * @return A content instance referencing the given location
     */
    public static DigitalObjectContent byReference(final URL reference) {
        return new ImmutableContent(reference);
    }

    /**
     * Create (streamed) content by reference, from a file. Note that the file
     * must be left in place long enough for the web service client to complete
     * the access.
     * @param reference The reference to the actual content value, using a File
     *        whose content will be streamed over the connection.
     * @return A content instance referencing the given location.
     */
    public static DigitalObjectContent byReference(final File reference) {
        if (!reference.exists()) {
            throw new IllegalArgumentException("Given file does not exist: " + reference);
        }
        return new ImmutableContent(reference);
    }

    /**
     * Create (streamed) content by reference, from an input stream.
     * @param inputStream The InputStream containing the value for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byReference(final InputStream inputStream) {
        File tmpFile = FileUtils.writeInputStreamToTmpFile(inputStream,
                "tempContent", ".dat");
	// FIXME! This is error prone! Two (or more) simultaneously running
	// migration services may overwrite each others results! A unique random
	// file name should be used and I also think we should specify a proper
	// temp. dir for writing the file. - TSH (SB)

        return new ImmutableContent(tmpFile);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param value The value bytes for the content
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final byte[] value) {
        return new ImmutableContent(value);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param value The value file for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final File value) {
        if (!value.exists()) {
            throw new IllegalArgumentException("Given file does not exist: " + value);
        }
        byte[] bytes = FileUtils.readFileIntoByteArray(value);
        return new ImmutableContent(bytes);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param inputStream The InputStream containing the value for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final InputStream inputStream) {
        File tmpFile = FileUtils.writeInputStreamToTmpFile(inputStream,
                "tempContent", ".dat");
        return new ImmutableContent(FileUtils.readFileIntoByteArray(tmpFile));
    }
}
