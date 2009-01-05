package eu.planets_project.ifr.core.registry.api.jaxr.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
class JAXRException {

    protected boolean available;
    protected String message;
    protected String requestId;
    protected int status;

}
