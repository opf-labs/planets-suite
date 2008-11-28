package eu.planets_project.ifr.core.registry.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    public String message;

    /**
     * @param message The response message
     */
    public Response(final String message) {
        this.message = message;
    }

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Response() {}

}
