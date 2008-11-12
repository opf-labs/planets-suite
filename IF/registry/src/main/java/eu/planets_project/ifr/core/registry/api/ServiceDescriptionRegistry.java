package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Draft/Work-in-progress: Registry interface based on the new
 * ServiceDescription objects.
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface ServiceDescriptionRegistry {

    /**
     * @param serviceDescription The service description to register
     * @return A status message
     */
    ServiceRegistryMessage register(ServiceDescription serviceDescription);

    /**
     * @param serviceName The name of the services to find
     * @return The services whose names correspond to the given name
     */
    List<ServiceDescription> find(String serviceName);

    /**
     * Clears the registry of all entries.
     * @return A status message
     */
    ServiceRegistryMessage clear();

    /*
     * Some more query methods below, TODO: but what do we actually need? Name,
     * Classification (Type?)
     */

    /**
     * @param serviceId The ID of the service description to retrieve
     * @return A service description with the given ID, or null
     */
    // ServiceDescription findServiceById(String serviceId);
    /**
     * @param organizationName The name of the organization providing services
     * @return The service description registered by an organization
     *         corresponding to the given name
     */
    // List<ServiceDescription> findServicesByOrganization(String
    // organizationName);
    /**
     * @param classification The classification to find services for
     * @return Service descriptions classified with the given classification
     */
    // List<ServiceDescription> findServicesByClassification(String
    // classification);
}
