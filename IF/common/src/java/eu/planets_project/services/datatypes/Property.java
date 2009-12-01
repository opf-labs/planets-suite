/**
 *
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;

/**
 * Property representation using an URI as ID.
 * <p>
 * For the most common case (a property with ID, name and value), use the {@link #Property(URI, String, String)}
 * constructor:
 * </p>
 * <p>
 * {@code Property p = new Property(uri, name, value);}
 * </p>
 * Only the ID is actually required. To create properties with less or more attributes, use a {@link Property.Builder}:
 * <p>
 * {@code Property p = new Property.Builder(uri).unit(unit).build();}
 * </p>
 * <p>
 * Instances of this class are immutable and so can be shared.
 * </p>
 * @author Andrew Jackson
 * @author Fabian Steeg
 */
@XmlType(name = "property", namespace = PlanetsServices.DATATYPES_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public final class Property {

    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private URI uri = null;
    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private String name = "";
    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private String value = "";
    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private String unit = "";
    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private String description = "";
    @XmlElement(namespace = PlanetsServices.DATATYPES_NS)
    private String type = "";

    /** For JAXB. */
    @SuppressWarnings("unused")
    private Property() {}

    /**
     * Create a property with id, name and value. To create properties with less or more attributes, use a
     * {@link Property.Builder} instead.
     * @param uri The property ID
     * @param name The property name
     * @param value The property value
     */
    public Property(final URI uri, final String name, final String value) {
        this.uri = uri;
        this.name = name;
        this.value = value;
    }

    /**
     * @param builder The builder to create a property from
     */
    private Property(final Property.Builder builder) {
        this.uri = builder.uri;
        this.name = builder.name;
        this.value = builder.value;
        this.description = builder.description;
        this.unit = builder.unit;
        this.type = builder.type;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s [%s] '%s' = '%s' (description=%s unit=%s type=%s)", this.getClass().getSimpleName(),
                uri, name, value, description, unit, type);
    }

    /**
     * Builder to create property instances with optional attributes.
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static final class Builder {
        /* URI is required: */
        private final URI uri;
        /* Defaults for optional values are set here: */
        private String name = "";
        private String value = "";
        private String description = "";
        private String unit = "";
        private String type = "";

        /**
         * @param uri The property id
         * @throws IllegalArgumentException if the given URI is null
         */
        public Builder(final URI uri) {
            if (uri == null) {
                throw new IllegalArgumentException("Property ID uri must not be null!");
            }
            this.uri = uri;
        }

        /**
         * @param name The property name
         * @return This builder, for cascaded calls
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @param value The property value
         * @return This builder, for cascaded calls
         */
        public Builder value(final String value) {
            this.value = value;
            return this;
        }

        /**
         * @param description The property description
         * @return This builder, for cascaded calls
         */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param unit The property unit
         * @return This builder, for cascaded calls
         */
        public Builder unit(final String unit) {
            this.unit = unit;
            return this;
        }

        /**
         * @param type The property type
         * @return This builder, for cascaded calls
         */
        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        /**
         * @return The finished immutable property instance
         */
        public Property build() {
            return new Property(this);
        }
    }
    
    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (description == null ? 0 : description.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        result = prime * result + (unit == null ? 0 : unit.hashCode());
        result = prime * result + (uri == null ? 0 : uri.hashCode());
        result = prime * result + (value == null ? 0 : value.hashCode());
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
        Property other = (Property) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * This is a convenience method, equivalent to
     * "new Property(ServiceDescription.PROPERTY, ServiceDescription.AUTHORIZED_ROLES, roles)".
     * @param roles The authorized roles, comma-separated (e.g. "admin,provider")
     * @return A property to be used to indicate the given roles are authenticated (e.g. in a ServiceDescription)
     */
    public static Property authorizedRoles(final String roles) {
        return new Property(ServiceDescription.PROPERTY, ServiceDescription.AUTHORIZED_ROLES, roles);
    }
}
