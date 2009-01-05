package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Planets Organizations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationList {
    /** The list of planets service orgs */
    public List<PsOrganization> organizations = new ArrayList<PsOrganization>();
    /** Any error messages go here */
    public String errorMessage;
}
