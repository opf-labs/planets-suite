package eu.planets_project.ifr.core.registry.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Service registry response type.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class Response {

    /** The response message. */
    private String message;
    /** Success flag. */
    private Boolean success = false;

    /**
     * @param message The response message
     * @param success The status of the registry request
     */
    public Response(final String message, final Boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Response() {}

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return message + ", success: " + success;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the success
     */
    public Boolean success() {
        return success;
    }
}
