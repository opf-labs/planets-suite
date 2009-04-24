package eu.planets_project.ifr.core.registry.api.jaxr.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
class JAXRException extends RuntimeException {

    /** Generated. */
    private static final long serialVersionUID = -1595737814080437805L;
    protected boolean available;
    protected String message;
    protected String requestId;
    protected int status;

}
