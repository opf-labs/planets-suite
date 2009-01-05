package eu.planets_project.services.datatypes;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents immutable tagged metadata.
 * 
 * @see MetadataTests
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * 
 */
public final class Metadata implements Comparable<Metadata>, Serializable {
    /** Generat3ed UID. */
    private static final long serialVersionUID = 1299020544765389245L;
    /** @see #getContent() */
    @XmlAttribute
    private String content;
    /** @see Metadata#getType() */
    @XmlAttribute
    private URI type;

    /**
     * @param type The metadata type
     * @param content The actual metadata
     */
    public Metadata(final URI type, final String content) {
        this.type = type;
        this.content = content;
    }

    /** No-args constructor for JAXB. Should not be used by clients. */
    @SuppressWarnings("unused")
    private Metadata() {}

    /**
     * @return The actual metadata.
     */
    public String getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Metadata && this.compareTo((Metadata) obj) == 0;
    }

    /**
     * {@inheritDoc}
     * 
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
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Metadata of type '%s' with content: %s", type,
                content);
    }

    /**
     * @return The metadata type.
     */
    public URI getType() {
        return type;
    }

    /**
     * @param o the metadata 
     * @return 0 if equal
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Metadata o) {
        if (this.type.equals(o.type)) {
            return this.content.compareTo(o.content);
        }
        return this.type.compareTo(o.type);
    }
}
