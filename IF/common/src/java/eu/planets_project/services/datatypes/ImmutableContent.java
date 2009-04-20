package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ByteArrayDataSource;
import eu.planets_project.services.utils.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Content for digital objects, either by reference or by value. Create content
 * by reference or value: {@code Content c = ImmutableContent.byReference(url);}
 * or {@code Content c = ImmutableContent.byValue(bytes); } However created, you
 * can read the content form the instance: {@code InputStream s = c.read();}
 * @see ContentTests
 * @author Asger Blekinge-Rasmussen (abr@statsbiblioteket.dk)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class ImmutableContent implements Content {
    private static Log log = LogFactory.getLog(ImmutableContent.class);

    /***/
    private static final long serialVersionUID = 7135127983024589335L;

    /***/
    @XmlAttribute
    private URL reference;

    @XmlElement(namespace = PlanetsServices.OBJECTS_NS)
    @XmlMimeType("application/octet-stream")
    private DataHandler dataHandler;

    /***/
    @XmlAttribute
    private long length = -1;
    private Checksum checksum = null;

    /*
     * We use static factory methods to provide named constructors for the
     * different kinds of content instances:
     */

    /**
     * Create content by reference.
     * @param reference The reference to the actual content value
     * @return A content instance referencing the given location
     */
    public static Content byReference(final URL reference) {
        return new ImmutableContent(reference);
    }

    /**
     * Create content by reference, from a File. Note that the file must be left
     * in place long enough for the web service client to complete the access.
     * @param reference The reference to the actual content value, using a File
     *        whose content will be streamed over the connection.
     * @return A content instance referencing the given location.
     */
    public static Content byReference(final File reference) {
        return new ImmutableContent(reference);
    }

    /**
     * Create content by value, which means actually embedded in the request.
     * @param value The value for the content
     * @return A content instance with the specified value
     */
    public static Content byValue(final byte[] value) {
        return new ImmutableContent(value);
    }

    /**
     * Create content by value, embedding a file.
     * @param value The value for the content, a File that should be read into a
     *        byte array.
     * @return A content instance with the specified value
     */
    public static Content byValue(final File value) {
        byte[] bytes = FileUtils.readFileIntoByteArray(value);
        return new ImmutableContent(bytes);
    }

    /**
     * Create content by value, embedding the contents of an input stream.
     * @param inputStream The InputStream containing the value for the content.
     *        The InputStream is written to a byte[]
     * @return A content instance with the specified value
     */
    public static Content byValue(final InputStream inputStream) {
        File tmpFile = FileUtils.writeInputStreamToTmpFile(inputStream,
                "tempContent", ".dat");
        return new ImmutableContent(FileUtils.readFileIntoByteArray(tmpFile));
    }

    /**
     * Create content as a stream, drawn from a File.
     * @param value The value for the content, a File that should be read.
     * @return A content instance with the specified value
     */
    public static Content asStream(final File value) {
        return new ImmutableContent(value);
    }

    /**
     * Pass content as a stream, from an input stream.
     * @param inputStream The InputStream containing the value for the content.
     * @return A content instance with the specified value
     */
    public static Content asStream(final InputStream inputStream) {
        // create a File from the InputStream and call the Content.byValue(File)
        // to avoid having the whole (maybe large) file in memory
        File tmpFile = FileUtils.writeInputStreamToTmpFile(inputStream,
                "tempContent", ".dat");
        return new ImmutableContent(tmpFile);
    }

    /**
     * @param value The content value
     */
    private ImmutableContent(final byte[] value) {
        ByteArrayDataSource bads = new ByteArrayDataSource(value,
                "application/octet-stream");
        DataHandler dh = new DataHandler(bads);
        this.length = value.length;
        this.dataHandler = dh;
        log.info("Created Content from byte array: " + this.length
                + " bytes in length.");
    }

    /**
     * @param value The content value, from a file.
     */
    private ImmutableContent(final File value) {
        FileDataSource ds = new FileDataSource(value);
        ds.setFileTypeMap(FileTypeMap.getDefaultFileTypeMap());
        DataHandler dh = new DataHandler(ds);
        this.length = value.length();
        this.dataHandler = dh;
        log.info("Created Content from file '" + value.getAbsolutePath()
                + "': " + this.length + " bytes in length.");
    }

    /**
     * @param reference The content, passed as an explicit reference.
     */
    private ImmutableContent(final URL reference) {
        this.length = -1;
        this.reference = reference;
        log.info("Created Content from file '" + reference);
    }

    /** No-args constructor for JAXB. Clients should not use this. */
    private ImmutableContent() {}

    /**
     * @param immutableContent The content to copy
     * @param checksum The checksum to attach to the content copy
     */
    private ImmutableContent(final ImmutableContent immutableContent,
            final Checksum checksum) {
        this.dataHandler = immutableContent.dataHandler;
        this.length = immutableContent.length;
        this.reference = immutableContent.reference;
        this.checksum = checksum;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream read() {
        try {
            if (isByValue()) {
                log.info("Opening dataHandler stream of type: "
                        + dataHandler.getContentType());
                log.info("Opening dataHandler stream available: "
                        + dataHandler.getInputStream().available());
                return dataHandler.getDataSource().getInputStream();
            } else {
                log.info("Opening reference: " + reference);
                return reference.openStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Should be used with care. If used in web service context, large files
     * could cause errors, when sent as byte[] via SOAP. The read() method
     * should be used instead, to stream large files.
     * @return The value of this content.
     * @deprecated Use {@link #read()} instead
     */
    public byte[] getValue() {
        /* Should work for both content by reference and by value: */
        return FileUtils.writeInputStreamToBinary(read());
    }

    /**
     * @return The reference, if any (might be null). Clients should not use
     *         this method to access the actual data, but {@link #read()} or
     *         {@link #getValue()}, which will always return the actual content,
     *         no matter how it was created (by value or by reference).
     */
    public URL getReference() {
        return reference;
    }

    /**
     * @return True, if this Content contains the actual value, or false if it
     *         contains a reference
     */
    public boolean isByValue() {
        return reference == null;
    }

    /**
     * @return The size of the Content, in bytes. Returns -1 if this is a 'by
     *         reference' Content object.
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.Content#withChecksum(eu.planets_project.services.datatypes.Checksum)
     */
    public Content withChecksum(final Checksum checksum) {
        return new ImmutableContent(this, checksum);
    }

    /**
     * @return The checksum
     * @see ImmutableContent#getChecksum()
     */
    public Checksum getChecksum() {
        return checksum;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ImmutableContent)) {
            return false;
        }
        ImmutableContent other = (ImmutableContent) obj;
        /*
         * Two content object, even if they would be based on the same data, are
         * not equal if they are not both by reference or both by value:
         */
        if (this.isByValue() != other.isByValue()) {
            return false;
        }
        /* Else we compare either value or reference: */
        if (isByValue()) {
            return this.dataHandler.equals(other.dataHandler);
        } else {
            return this.reference.toString().equals(other.reference.toString());
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return isByValue() ? dataHandler.hashCode() : reference.toString()
                .hashCode();
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Content by %s: %s",
                isByValue() ? "value (DataHandler)" : "reference",
                isByValue() ? dataHandler : reference);
    }

}
