package eu.planets_project.ifr.core.registry.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    /** The response message. */
    public String message;
    /** Success flag. */
    public boolean success = false;

    /**
     * @param message The response message
     * @param success
     */
    public Response(final String message, boolean success) {
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
        return message;
    }
}
