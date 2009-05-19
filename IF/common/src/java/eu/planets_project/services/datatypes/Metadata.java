package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.net.URI;

/**
 * Representation of immutable tagged metadata.
 * @see MetadataTests
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Metadata implements Comparable<Metadata>, Serializable {
    /** Generated UID. */
    private static final long serialVersionUID = 1299020544765389245L;

    /**
     * The block of metadata.
     * @see #getContent()
     */
    @XmlElement(namespace = PlanetsServices.OBJECTS_NS, required = true)
    private String content;

    /**
     * @see Metadata#getType()
     */
    @XmlAttribute(required = true)
    private URI type;

    /**
     * @param type The metadata type. Represents the type of metadata. The URI
     *        could be the namespace of a xml schema, or a xml datatype like
     *        integer. But in short, given the URI, you should be able to figure
     *        out how to understand the metadata. No URI means that the metadata
     *        is readily readable, ie. clear text.
     * @param content The actual metadata
     */
    public Metadata(final URI type, final String content) {
        this.type = type;
        this.content = content;
    }

    /** No-args constructor for JAXB. */
    @SuppressWarnings("unused")
    private Metadata() {}

    /**
     * @return The actual metadata.
     */
    public String getContent() {
        return content;
    }

    /**
     * @return The metadata type.
     */
    public URI getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Metadata && this.compareTo((Metadata) obj) == 0;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        /* Following 'Effective Java'... */
        int i = 17;
        int j = 31;
        int result = i;
        result = j * result + type.hashCode();
        result = j * result + content.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Metadata of type '%s' with content: %s", type,
                content);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Metadata other) {
        if (this.type.equals(other.type)) {
            return this.content.compareTo(other.content);
        }
        return this.type.compareTo(other.type);
    }
}
