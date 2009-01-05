package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets services result list.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceList {
    /** A list of planets services */
    public List<PsService> services = new ArrayList<PsService>();
    /** Any error message goes here */
    public String errorMessage;
}
