package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * Planets Service Registry message.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceRegistryMessage {

    /** The message string */
    public String message;
    /** A public list of operand strings  */
    public List<String> operands = new ArrayList<String>();
    /** A planets service registry object */
    public PsRegistryObject registryObject;

    /**
     * construct from a message string
     * @param message
     */
    public ServiceRegistryMessage(String message) {
        this.message = message;
        operands = new ArrayList<String>();
    }

    /**
     * default no arg constructor
     */
    public ServiceRegistryMessage() {
        operands = new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "%s with text %s, operands %s and registry object %s",
                getClass().getSimpleName(), message, operands, registryObject);
    }

}
