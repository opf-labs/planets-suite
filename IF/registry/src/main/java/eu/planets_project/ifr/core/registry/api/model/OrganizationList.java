package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets Organizations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationList {
    public List<PsOrganization> organizations = new ArrayList<PsOrganization>();
    public String errorMessage;
}
