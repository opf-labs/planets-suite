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
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        /*
         * Our defaults plus the fact that we are immutable guarantee that no attribute can ever be null, so we can skip
         * the tedious null checks here. FIXME: not true, only the builder checks this, not the one constructor...
         */
        result = prime * result + description.hashCode();
        result = prime * result + name.hashCode();
        result = prime * result + type.hashCode();
        result = prime * result + unit.hashCode();
        result = prime * result + uri.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        /* We know we have an instance of Property now, so casting is safe. */
        Property that = (Property) obj;
        /*
         * Our defaults plus the fact that we are immutable guarantee that no attribute can ever be null, so we can skip
         * the tedious null checks here. FIXME: not true, only the builder checks this, not the one constructor...
         */
        return this.uri.equals(that.uri) && this.name.equals(that.name) && this.value.equals(that.value)
                && this.type.equals(that.type) && this.unit.equals(that.unit)
                && this.description.equals(that.description);
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
     * This is a convenience method, equivalent to
     * "new Property(ServiceDescription.PROPERTY, ServiceDescription.AUTHORIZED_ROLES, roles)".
     * @param roles The authorized roles, comma-separated (e.g. "admin,provider")
     * @return A property to be used to indicate the given roles are authenticated (e.g. in a ServiceDescription)
     */
    public static Property authorizedRoles(final String roles) {
        return new Property(ServiceDescription.PROPERTY, ServiceDescription.AUTHORIZED_ROLES, roles);
    }
}
