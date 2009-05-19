package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Immutable representation of a checksum, containing of the algorithm used and
 * the actual value.
 * @see ChecksumTests
 * @author Fabian Steeg
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Checksum implements Comparable<Checksum>, Serializable {
    /** Generated UID. */
    private static final long serialVersionUID = 8799717233710485566L;
    /** @see #getAlgorithm() */
    @XmlAttribute
    private String algorithm; /* Not final for JAXB */
    /** @see #getValue() */
    @XmlAttribute
    private String value;

    /**
     * @param algorithm The checksum algorithm.
     * @param value The checksum value.
     */
    public Checksum(final String algorithm, final String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    /** No-args constructor for JAXB usage. */
    @SuppressWarnings("unused")
    private Checksum() {}

    /**
     * @return The checksum algorithm.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @return The checksum value.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Checksum)) {
            return false;
        }
        Checksum other = (Checksum) obj;
        return this.compareTo(other) == 0;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("[%s, algorithm: %s, value: %s]", this.getClass()
                .getSimpleName(), algorithm, value);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Checksum o) {
        if (this.algorithm.equals(o.algorithm)) {
            return this.value.compareTo(o.value);
        }
        return this.algorithm.compareTo(o.algorithm);
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
        result = j * result + algorithm.hashCode();
        result = j * result + value.hashCode();
        return result;
    }
}
