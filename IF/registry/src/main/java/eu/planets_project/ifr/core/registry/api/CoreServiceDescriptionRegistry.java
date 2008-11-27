package eu.planets_project.ifr.core.registry.api;

import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Core in-memory (non-persistent) registry for service description instances.
 * @see ServiceDescription
 * @see ServiceDescriptionRegistry
 * @see PersistentServiceDescriptionRegistry
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class CoreServiceDescriptionRegistry implements
        ServiceDescriptionRegistry {

    private List<ServiceDescription> descriptions = null;

    /***/
    private CoreServiceDescriptionRegistry() {
        descriptions = new ArrayList<ServiceDescription>();
    }

    /**
     * @return A simple, non-persistent registry for service descriptions.
     */
    public static ServiceDescriptionRegistry getInstance() {
        return new CoreServiceDescriptionRegistry();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#clear()
     */
    public ServiceRegistryMessage clear() {
        descriptions.clear();
        return new ServiceRegistryMessage("Cleared in-memory registry.");
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription sample) {
        return new QueryServiceDescriptions(descriptions).byExample(sample);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public ServiceRegistryMessage register(
            final ServiceDescription serviceDescription) {
        if (!descriptions.contains(serviceDescription)) {
            descriptions.add(serviceDescription);
        }
        return new ServiceRegistryMessage("Added: " + serviceDescription);
    }

}
