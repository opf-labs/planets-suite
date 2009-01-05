package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets Service Bindings.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BindingList {
    /** The List of planet service bindings */
    public List<PsBinding> bindings = new ArrayList<PsBinding>();
    /** Any error messages are here */
    public String errorMessage;
}
