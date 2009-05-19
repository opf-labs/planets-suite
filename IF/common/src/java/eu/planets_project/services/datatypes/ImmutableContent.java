package eu.planets_project.services.datatypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ByteArrayDataSource;

/**
 * Content for digital objects, either by reference or by value.
 * @see ContentTests
 * @author Asger Blekinge-Rasmussen (abr@statsbiblioteket.dk)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
/*
 * NOTE: This class is intentionally NOT PUBLIC. Clients should use the factory
 * methods in the Content class to instantiate content.
 */
final class ImmutableContent implements DigitalObjectContent {
    private static Log log = LogFactory.getLog(ImmutableContent.class);

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

    @XmlElement(namespace = PlanetsServices.OBJECTS_NS)
    private Checksum checksum = null;

    /**
     * @param value The content value
     */
    ImmutableContent(final byte[] value) {
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
    ImmutableContent(final File value) {
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
    ImmutableContent(final URL reference) {
        this.length = -1;
        this.reference = reference;
        log.info("Created Content from file '" + reference);
    }

    /** No-args constructor for JAXB. Clients should not use this. */
    @SuppressWarnings("unused")
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
     * @see eu.planets_project.services.datatypes.DigitalObjectContent#read()
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
