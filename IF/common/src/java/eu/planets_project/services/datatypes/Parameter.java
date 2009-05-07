/**
 *
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This wraps the concept of a service parameter. When retrieved from a service,
 * the default values should be set. This form does not allow optional v.
 * required parameters, as ALL parameters should be explicitly specified. An
 * 'optional' parameter implies an implicit default that would end up not being
 * recorded in the audit trail.
 * @author AnJackson
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = PlanetsServices.SERVICES_NS)
public final class Parameter {

    private String name;
    private String value;
    private String type;
    private String description;

    /* ------------------------------------------------------------------------- */

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Parameter() {}

    /**
     * Constructor for the most common case: a Parameter with name and value.
     * For parameters with optional values (type, description), use a
     * Parameter.Builder.
     * @param name A name for the parameter. Must be uniquely meaningful to the
     *        service, but is not expected to carry any meaning outwith the
     *        service.
     * @param value The value for this parameter. Should be set to the default
     *        by the service when parameter discovery is happening.
     */
    public Parameter(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /* ------------------------------------------------------------------------- */

    /**
     * @param builder The builder to create a Parameter from
     */
    private Parameter(final Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.type = builder.type;
        this.description = builder.description;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.name + " = " + this.value;
    }

    /**
     * Builder for Parameters with optional values (type, description).
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static final class Builder {
        private String name;
        private String value;
        private String type;
        private String description;

        /** For JAXB. */
        @SuppressWarnings("unused")
        private Builder() {}

        /**
         * @param name The Parameter name, see
         *        {@link Parameter#Parameter(String, String)}
         * @param value The Parameter value, see
         *        {@link Parameter#Parameter(String, String)}
         */
        public Builder(final String name, final String value) {
            this.name = name;
            this.value = value;
            this.type = "";
            this.description = "";
        }

        /**
         * @param type This is a String to hold the type, which should map to
         *        the xsd types and should be assumed to be a String if empty or
         *        null. In the future, we might add limits/validation?
         *        XSD-style?
         * @return This builder, for cascaded calls
         */
        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        /**
         * @param description The description of this parameter/value pair.
         *        Might be used to give further information on the possible
         *        values and their meaning.
         * @return This builder, for cascaded calls
         */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @return The built Parameter object
         */
        public Parameter build() {
            return new Parameter(this);
        }
    }

}
