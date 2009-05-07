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
     * @param name A name for the parameter. Must be uniquely meaningful to the
     *        service, but is not expected to carry any meaning outwith the
     *        service.
     * @param value The value for this parameter. Should be set to the default
     *        by the service when parameter discovery is happening.
     * @param type This is a String to hold the type, which should map to the
     *        xsd types and should be assumed to be a String if empty or null.
     *        In the future, we might add limits/validation? XSD-style?
     * @param description The description of this parameter/value pair. Might be
     *        used to give further information on the possible values and their
     *        meaning.
     */
    public Parameter(final String name, final String value, final String type,
            final String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    /**
     * @param name A name for the parameter. Must be uniquely meaningful to the
     *        service, but is not expected to carry any meaning outwith the
     *        service.
     * @param value The value for this parameter. Should be set to the default
     *        by the service when parameter discovery is happening.
     * @param type This is a String to hold the type, which should map to the
     *        xsd types and should be assumed to be a String if empty or null.
     *        In the future, we might add limits/validation? XSD-style?
     */
    public Parameter(final String name, final String value, final String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    /**
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

}
