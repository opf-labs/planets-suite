package eu.planets_project.services.datatypes;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import eu.planets_project.services.utils.FileUtils;

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
     * Create (streamed) content by reference.
     * @param reference The reference to the actual content value
     * @return A content instance referencing the given location
     */
    public static DigitalObjectContent byReference(final URL reference) {
        return new ImmutableContent(reference);
    }

    /**
     * Create (streamed) content by reference, from a File. Note that the file
     * must be left in place long enough for the web service client to complete
     * the access.
     * @param reference The reference to the actual content value, using a File
     *        whose content will be streamed over the connection.
     * @return A content instance referencing the given location.
     */
    public static DigitalObjectContent byReference(final File reference) {
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
        return new ImmutableContent(tmpFile);
    }

    /**
     * Create content by value, which means actually embedded in the request.
     * @param value The value for the content
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final byte[] value) {
        return new ImmutableContent(value);
    }

    /**
     * Create content by value, embedding a file.
     * @param value The value for the content, a File that should be read into a
     *        byte array.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final File value) {
        byte[] bytes = FileUtils.readFileIntoByteArray(value);
        return new ImmutableContent(bytes);
    }

    /**
     * Create content by value, embedding the contents of an input stream.
     * @param inputStream The InputStream containing the value for the content.
     *        The InputStream is written to a byte[]
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final InputStream inputStream) {
        File tmpFile = FileUtils.writeInputStreamToTmpFile(inputStream,
                "tempContent", ".dat");
        return new ImmutableContent(FileUtils.readFileIntoByteArray(tmpFile));
    }
}
