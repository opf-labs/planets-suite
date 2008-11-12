package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets services result list.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceList {
    public List<PsService> services = new ArrayList<PsService>();
    public String errorMessage;
}
