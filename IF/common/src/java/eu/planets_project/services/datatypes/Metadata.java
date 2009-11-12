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
public final class Metadata implements Serializable {
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
     * @see Metadata#getName()
     */
    @XmlAttribute(required = true)
    private String name;

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

    /**
     * @param type The metadata type. Represents the type of metadata. The URI
     *        could be the namespace of a xml schema, or a xml datatype like
     *        integer. But in short, given the URI, you should be able to figure
     *        out how to understand the metadata. No URI means that the metadata
     *        is readily readable, ie. clear text.
     * @param name A name for the metadata, used to distinguish between metatdata
     * 		  fragments of the same type.
     * @param content The actual metadata
     */
    public Metadata(final URI type, final String name, final String content) {
        this.type = type;
        this.content = content;
        this.name = name;
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
     * @return The metadata name
     */
    public String getName() {
    	return name;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Metadata of type '%s' with name: %s and content: %s",
        		type, name, content);
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

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Metadata other = (Metadata) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!content.equals(other.content)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (name == null) {
        	if (other.name != null) {
        		return false;
        	}
        } else if (!name.equals(other.name)) {
        	return false;
        }
        return true;
    }
}
