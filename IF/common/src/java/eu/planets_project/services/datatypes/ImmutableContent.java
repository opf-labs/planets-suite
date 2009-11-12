package eu.planets_project.services.datatypes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingDataHandler;

import eu.planets_project.services.PlanetsServices;

/**
 * Content for digital objects, either by reference or by value.
 * @see ContentTests
 * @author Asger Blekinge-Rasmussen (abr@statsbiblioteket.dk)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
/*
 * NOTE: This class is intentionally NOT PUBLIC. Clients should use the factory methods in the Content class to
 * instantiate content.
 */
final class ImmutableContent implements DigitalObjectContent, Serializable {
    private static Log log = LogFactory.getLog(ImmutableContent.class);

    private static final long serialVersionUID = 7135127983024589335L;

    /***/
    @XmlAttribute
    private URL reference;
    
    @XmlElement
    private byte[] bytes;

    @XmlElement(namespace = PlanetsServices.OBJECTS_NS)
    @XmlMimeType("application/octet-stream")
    /*
     * FIXME: This field is non-serializable and non-transient. We can't make it serializable because it's not ours and
     * we can't make it transient because then JAXB complains. Support for Java Serialization is currently required by
     * GUI components. Possible solutions: using different UI components; using some sort of wrapper object in the GUI
     * (SerializableDigitalObject).
     */
    @XmlAttachmentRef() //This appears to be required to actually enable the streaming data handler
    private DataHandler dataHandler;

    /***/
    @XmlAttribute
    private long length = -1;

    @XmlElement(namespace = PlanetsServices.OBJECTS_NS)
    private Checksum checksum = null;

    /**
     * @param value The content value
     */
    ImmutableContent(final byte[] value) {
        this.length = value.length;
        this.bytes = value;
        log.info("Created Content from byte array: " + this.length + " bytes in length.");
    }

    /**
     * @param reference The content reference, as a file.
     */
    ImmutableContent(final File reference) {
        FileDataSource ds = new FileDataSource(reference);
        ds.setFileTypeMap(FileTypeMap.getDefaultFileTypeMap());
        DataHandler dh = new DataHandler(ds);
        this.length = reference.length();
        this.dataHandler = dh;
        log.info("Created Content from file: " + reference.getAbsolutePath() + "': " + this.length + " bytes in length.");
    }

    /**
     * @param reference The content, passed as an explicit reference.
     */
    ImmutableContent(final URL reference) {
        this.length = -1;
        this.reference = reference;
        log.info("Created Content from URL: " + reference);
    }

    /** No-args constructor for JAXB. Clients should not use this. */
    @SuppressWarnings("unused")
    private ImmutableContent() {}

    /**
     * @param immutableContent The content to copy
     * @param checksum The checksum to attach to the content copy
     */
    private ImmutableContent(final ImmutableContent immutableContent, final Checksum checksum) {
        this.dataHandler = immutableContent.dataHandler;
        this.length = immutableContent.length;
        this.reference = immutableContent.reference;
        this.checksum = checksum;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObjectContent#read()
     */
    public InputStream read() {
        try {
            if (dataHandler != null) {
                log.info("Opening dataHandler stream of type: " + dataHandler.getContentType());
                log.info("Opening dataHandler stream available: " + dataHandler.getInputStream().available());
                if (dataHandler instanceof StreamingDataHandler) {
                    StreamingDataHandler h = (StreamingDataHandler) dataHandler;
                    return h.getInputStream(); //readOnce basically works but makes usage inconvenient
                }
                return dataHandler.getInputStream();
            } else if (bytes != null) {
                return new ByteArrayInputStream(bytes);
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
     * @return The reference, if any (might be null). Clients should not use this method to access the actual data, but
     *         {@link #read()} or {@link #getValue()}, which will always return the actual content, no matter how it was
     *         created (by value or by reference).
     */
    public URL getReference() {
        return reference;
    }
    
    /**
     * @return The data handler, or null (if this content uses a URL or byte[])
     */
    DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * @return True, if this Content contains the actual value, or false if it contains a reference
     */
    public boolean isByValue() {
        return reference == null && dataHandler == null;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObjectContent#length()
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObjectContent#withChecksum(eu.planets_project.services.datatypes.Checksum)
     */
    public DigitalObjectContent withChecksum(final Checksum checksum) {
        return new ImmutableContent(this, checksum);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObjectContent#getChecksum()
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
        ImmutableContent that = (ImmutableContent) obj;
        /*
         * Two content objects, even if they would be based on the same data, are not equal if they are not both by
         * reference or both by value:
         */
        if (this.isByValue() != that.isByValue()) {
            return false;
        }
        /* Else we compare either value or reference: */
        try {
            if (this.dataHandler != null && that.dataHandler != null) {
                return IOUtils.contentEquals(dataHandler.getInputStream(), that.dataHandler.getInputStream());
            } else if (this.bytes != null && that.bytes != null) {
                return IOUtils
                        .contentEquals(new ByteArrayInputStream(this.bytes), new ByteArrayInputStream(that.bytes));
            } else {
                return this.reference.toString().equals(that.reference.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return dataHandler != null ? dataHandler.hashCode() : bytes != null ? Arrays.hashCode(bytes) : reference
                .toString().hashCode();
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Content by %s: %s", isByValue() ? "value" : "reference",
                dataHandler != null ? dataHandler : bytes != null ? Arrays.asList(bytes) : reference);
    }

}
