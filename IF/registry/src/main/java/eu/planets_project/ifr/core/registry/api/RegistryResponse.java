package eu.planets_project.ifr.core.registry.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class RegistryResponse {

    /** The response message. */
    private String message;
    /** Success flag. */
    private Boolean success = false;

    /**
     * @param message The response message
     * @param success The status of the registry request
     */
    public RegistryResponse(final String message, final Boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private RegistryResponse() {}

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return message;
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
