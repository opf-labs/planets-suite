package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets Service Registry message.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceRegistryMessage {

    public String message;
    public List<String> operands = new ArrayList<String>();
    public PsRegistryObject registryObject;

    public ServiceRegistryMessage(String message) {
        this.message = message;
        operands = new ArrayList<String>();
    }

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
