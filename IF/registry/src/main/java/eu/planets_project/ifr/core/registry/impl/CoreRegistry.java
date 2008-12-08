package eu.planets_project.ifr.core.registry.impl;

import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.Response;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Core in-memory (non-persistent) registry for service description instances.
 * @see ServiceDescription
 * @see Registry
 * @see PersistentRegistry
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class CoreRegistry implements Registry {

    private List<ServiceDescription> descriptions = null;

    /***/
    private CoreRegistry() {
        descriptions = new ArrayList<ServiceDescription>();
    }

    /**
     * @return A simple, non-persistent registry for service descriptions.
     */
    public static Registry getInstance() {
        return new CoreRegistry();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#clear()
     */
    public Response clear() {
        descriptions.clear();
        return new Response("Cleared in-memory registry.", true);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription sample) {
        return new Query(descriptions).byExample(sample);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        if (serviceDescription.getEndpoint() == null) {
            return new Response(
                    String
                            .format(
                                    "Could not add service description %s, as it has no endpoint",
                                    serviceDescription), false);
        }
        if (!descriptions.contains(serviceDescription)) {
            descriptions.add(serviceDescription);
            return new Response("Registered: " + serviceDescription, true);
        }
        return new Response("Already registered: " + serviceDescription, false);
    }

}
