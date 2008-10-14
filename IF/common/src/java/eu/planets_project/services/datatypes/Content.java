package eu.planets_project.services.datatypes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Content for digital objects, either by reference or by value. Create content
 * by reference or value: {@code Content c = Content.reference(url);} or {@code
 * Content c = Content.value(bytes); } However created, you can read the content
 * form the instance: {@code InputStream s = c.read();}
 * 
 * @see ContentTests
 * 
 * @author Asger Blekinge-Rasmussen (abr@statsbiblioteket.dk)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * 
 */
public final class Content implements Serializable {
    /** Generated UID. */
    private static final long serialVersionUID = 3352422791774816377L;
    @XmlElement
    private byte[] value;
    @XmlAttribute
    private URL reference;

    /*
     * We use static factory methods to provide named constructors for the
     * different kinds of content instances:
     */

    /**
     * Create content by reference.
     * 
     * @param reference The reference to the actual content value
     * @return A content instance referencing the given location
     */
    public static Content byReference(final URL reference) {
        return new Content(reference);
    }

    /**
     * Create content by value.
     * 
     * @param value The value for the content
     * @return A content instance with the specified value
     */
    public static Content byValue(final byte[] value) {
        return new Content(value);
    }

    /**
     * @param value The content value
     */
    private Content(final byte[] value) {
        this.value = value;
    }

    /**
     * @param reference The content reference.
     */
    private Content(final URL reference) {
        this.reference = reference;
    }

    /** No-args constructor for JAXB. Clients should not use this. */
    private Content() {}

    /**
     * @return An input stream for this content; this is either created for the
     *         actual value (if this is value content) or a stream for reading
     *         the reference (if this is a reference content)
     */
    public InputStream read() {
        if (isByValue()) {
            return new ByteArrayInputStream(value);
        } else {
            try {
                return reference.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    
    /**
     * @return the value
     */
    protected byte[] getValue() {
        return value;
    }

    /**
     * @return the reference
     */
    protected URL getReference() {
        return reference;
    }

    /**
     * @return True, if this Content contains the actual value, or false if it
     *         contains a reference
     */
    private boolean isByValue() {
        return reference == null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Content)) {
            return false;
        }
        Content other = (Content) obj;
        /*
         * Two content object, even if they would be based on the same data, are
         * not equal if they are not both by reference or both by value:
         */
        if (this.isByValue() != other.isByValue()) {
            return false;
        }
        /* Else we compare either value or reference: */
        if (isByValue()) {
            return this.value == other.value;
        } else {
            return this.reference.toString().equals(other.reference.toString());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return isByValue() ? value.hashCode() : reference.toString().hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Content by %s: %s", isByValue() ? "value"
                : "reference", isByValue() ? value : reference);
    }

}
