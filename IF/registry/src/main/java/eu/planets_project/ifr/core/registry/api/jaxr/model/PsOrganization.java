package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.User;

import eu.planets_project.ifr.core.registry.api.jaxr.JaxrServiceRegistryHelper;

/**
 * Planets organization.
 */
public class PsOrganization extends PsRegistryObject {

    private String contactName;
    private String contactMail;
    private List<PsService> services = new ArrayList<PsService>();

    /**
     * @param name
     * @param description
     * @param contact
     * @param mail
     */
    public PsOrganization(String name, String description, String contact,
            String mail) {
        this.name = name;
        this.description = description;
        this.contactName = contact;
        this.contactMail = mail;
    }

    /**
     * default no arg constructor
     */
    public PsOrganization() {}

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format(
                        "%s: Organization with name %s, contact person %s, contact mail %s, description %s, %s services",
                        super.toString(), name, contactName, contactMail,
                        description, services.size());
    }

    /**
     * @param o The JAXR organization to convert into a Planets organization
     * @return A planets organization
     */
    public static PsOrganization of(Organization o) {
        if (o == null) {
            throw new IllegalArgumentException(
                    "Can't convert a null organization");
        }
        try {
            InternationalString internationalString = o.getName();
            String value = internationalString.getValue();
            String description = o.getDescription().getValue();
            String fullName = o.getPrimaryContact().getPersonName()
                    .getFullName();
            String contact = o.getPrimaryContact().getEmailAddresses()
                    .iterator().next().toString();
            PsOrganization org = new PsOrganization(value, description,
                    fullName, contact);
            org.key = o.getKey().getId();
            return org;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the contactName to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return the contactMail
     */
    public String getContactMail() {
        return contactMail;
    }

    /**
     * @param contactMail the contactMail to set
     */
    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    /**
     * @return the services
     */
    public List<PsService> getServices() {
        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices(List<PsService> services) {
        this.services = services;
    }

    /**
     * @param blcm The lifecycle maneger
     * @param bqm The query manager
     * @return A JAXR organisation corresponding to this Planets organization
     */
    public Organization toJaxrOrganization(BusinessLifeCycleManager blcm,
            BusinessQueryManager bqm) {
        try {
            Organization jaxrOrganization = null;
            if (this.key != null) {
                jaxrOrganization = JaxrServiceRegistryHelper
                        .fetchOrganizationByKey(this.getKey(), bqm);
            }
            if (jaxrOrganization == null) {
                jaxrOrganization = blcm.createOrganization(this.name);
                jaxrOrganization.setKey(blcm.createKey(this.key));
                jaxrOrganization.setName(blcm
                        .createInternationalString(this.name));
                jaxrOrganization.setDescription(blcm
                        .createInternationalString(this.description));
                User user = blcm.createUser();
                user
                        .setPersonName(blcm.createPersonName(this
                                .getContactName()));
                EmailAddress mail = blcm.createEmailAddress(this
                        .getContactMail());
                user.setEmailAddresses(Arrays.asList(mail));
                jaxrOrganization.setPrimaryContact(user);
            }
            return jaxrOrganization;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }
}
