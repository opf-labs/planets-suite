package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets Service Bindings.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BindingList {
    public List<PsBinding> bindings = new ArrayList<PsBinding>();
    public String errorMessage;
}
