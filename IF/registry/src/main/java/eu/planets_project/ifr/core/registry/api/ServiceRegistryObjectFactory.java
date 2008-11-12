package eu.planets_project.ifr.core.registry.api;

import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Create registry object instances (Organizations, Services, Bindings) in a
 * registry.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryObjectFactory {
    /** Service testing values. */
    enum ServiceValue {
        NAME, DESCRIPTION, KEY
    }

    /** Service binding testing values. */
    enum Binding {
        ENDPOINT, DESCRIPTION, KEY
    }

    /** Organization testing values. */
    enum Organization {
        NAME, DESCRIPTION, KEY, CONTACT_NAME, CONTACT_MAIL
    }

    /** Category testing values. */
    enum Category {
        NAME1, NAME2
    }

    private ServiceRegistry registry;
    private String username;
    private String password;

    /* Create stuff methods: */

    /**
     * ATTENTION: This probably is temporary API which will be replaced by the
     * ServiceDescription API.
     * @param username The username
     * @param password The password
     * @param registry The registry to create objects in
     */
    public ServiceRegistryObjectFactory(String username, String password,
            ServiceRegistry registry) {
        this.password = password;
        this.username = username;
        this.registry = registry;
    }

    /**
     * @param name The service name
     * @param description The service description
     * @param organization The providing organization
     * @return A service with the given name and description, registered in the
     *         registry as a service of the given organization
     */
    public PsService createService(String name, String description,
            PsOrganization organization) {
        PsService service = new PsService();
        service.setName(name);
        service.setDescription(description);
        service.setOrganization(organization);
        ServiceRegistryMessage message = registry.saveService(username, password,
                service);
        service.setKey(message.registryObject.getKey());
        return service;
    }

    /**
     * @param organization The providing organization
     * @return Returns a dummy service for testing
     */
    PsService createService(PsOrganization organization) {
        return createService(ServiceValue.NAME.toString(),
                ServiceValue.DESCRIPTION.toString(), organization);
    }

    /**
     * @param name The organization name
     * @param description The organization description
     * @param contact The organization contact person
     * @param mail The organization mail
     * @return An organization with the given attributes, registered in the the
     *         registry
     */
    public PsOrganization createOrganization(String name, String description,
            String contact, String mail) {
        PsOrganization organization = new PsOrganization();
        organization.setName(name);
        organization.setDescription(description);
        organization.setContactName(contact);
        organization.setContactMail(mail);
        ServiceRegistryMessage message = registry.saveOrganization(username,
                password, organization);
        System.out.println(message);
        organization.setKey(message.registryObject.getKey());
        return organization;
    }

    /**
     * @return Returns a dummy organization for testing
     */
    PsOrganization createOrganization() {
        return createOrganization(Organization.NAME.toString(),
                Organization.DESCRIPTION.toString(), Organization.CONTACT_NAME
                        .toString(), Organization.CONTACT_MAIL.toString());
    }

    /**
     * @param description The binding description
     * @param endpoint The binding endpoint
     * @param service The service for the binding
     * @return A binding with the given attributes, associated to the given
     *         service and registered in the registry
     */
    public PsBinding createBinding(String description, String endpoint,
            PsService service) {
        PsBinding b = new PsBinding();
        b.setAccessURI(endpoint);
        b.setDescription(description);
        b.setService(service);
        ServiceRegistryMessage message = registry.saveBinding(username, password, b);
        b.setKey(message.registryObject.getKey());
        return b;
    }

    /**
     * @return Returns a dummy binding for testing
     */
    PsBinding createBinding(PsService service) {
        return createBinding(Binding.DESCRIPTION.toString(), Binding.ENDPOINT
                .toString(), service);
    }

    /*
     * Some helpers for the new description-based interface to the registry
     * (work in progress)
     */

    /**
     * ATTENTION: work in progress, this is not fully working yet.
     * @param service The description to convert into a PsService
     * @return A PsService corresponding to the given description, registered in
     *         this' registry
     */
    public PsService serviceOf(ServiceDescription service) {
        // TODO we have no organization info in the service description objects
        PsService psService = createService(service.getName(), service
                .getDescription(), createOrganization());
        return psService;
    }

    // TODO Hm, static... Both? None?

    /**
     * ATTENTION: work in progress, this is not fully working yet.
     * @param psService The service to convert into a service description
     * @return A service description corresponding to the given service
     */
    public static ServiceDescription descriptionOf(PsService psService) {
        // TODO what about the rest?
        // TODO how to map classifications and types
        ServiceDescription sd = new ServiceDescription(psService.getName(), null);
        sd.setDescription(psService.getDescription());
        return sd;
    }

}
