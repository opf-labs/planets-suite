/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A report from a preservation service defined based on the need identified in
 * the 1st and 4th Service Developers Meetings. Where possible, information
 * concerning the quality of the outputs should be placed in Events associated
 * with DigitalObjects.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = PlanetsServices.SERVICES_NS)
public final class ServiceReport {

    @XmlElement(namespace = PlanetsServices.SERVICES_NS, required = true)
    private String message;

    @XmlElement(namespace = PlanetsServices.SERVICES_NS, required = true)
    private Status status;

    @XmlElement(namespace = PlanetsServices.SERVICES_NS, required = true)
    private Type type;

    /**
     * Type of information returned by a service.
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static enum Type {
        /** Roughly corresponding to Standard Out. */
        INFO,
        /**
         * Things the user should be aware of, but are not fatal and do not
         * imply significant data loss.
         */
        WARN,
        /**
         * Serious problems invoking the service, implying that no valid output
         * will exist and the workflow should not continue.
         */
        ERROR
    }

    /**
     * Service report status.
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static enum Status {
        /**
         * The service was invoked successfully. This does not guarantee that
         * the service did what it was supposed to do, just that the service
         * wrapping registered no errors. For further detail, examine the info
         * string and the warn string
         */
        SUCCESS,
        /**
         * The service failed. For further details, examine the error string
         */
        TOOL_ERROR,
        /**
         * The service failed in such a way, that further invocations of this
         * service will also likely fail. Do not invoke the service again<br/>
         * This is the correct error state, if the service has unfulfilled
         * dependencies from the environment, or suffers a catastrophic error
         * like OutOfMemory For further detail on the error, examine the error
         * string
         */
        INSTALLATION_ERROR
    }

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private ServiceReport() {}

    /**
     * Create a service report with specified status, type and message.
     * @param type The Type enum element
     * @param status The Status enum element
     * @param message The message
     */
    public ServiceReport(final Type type, final Status status,
            final String message) {
        this.type = type;
        this.status = status;
        this.message = message;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return The status, an element of {@link ServiceReport.Status}
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return The type, an element of {@link ServiceReport.Type}
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "ServiceReport of type '%s', status '%s', message: %s", type,
                status, message);
    }
}
