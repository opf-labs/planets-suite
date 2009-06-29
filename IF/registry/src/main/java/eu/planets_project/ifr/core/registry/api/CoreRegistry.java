package eu.planets_project.ifr.core.registry.api;

import java.util.ArrayList;
import java.util.List;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Core in-memory (non-persistent) registry for service description instances.
 * NOTE: Clients should use the RegistryFactory to instantiate a Registry
 * @see ServiceDescription
 * @see Registry
 * @see PersistentRegistry
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
final class CoreRegistry implements Registry {

    private List<ServiceDescription> descriptions = null;

    /***/
    private CoreRegistry() {
        descriptions = new ArrayList<ServiceDescription>();
    }

    /**
     * NOTE: Clients should use the RegistryFactory to instantiate a Registry.
     * @return A simple, non-persistent registry for service descriptions.
     */
    static Registry getInstance() {
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
        return queryWithMode(sample, MatchingMode.EXACT);
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
        if (!endpointPresent(serviceDescription)
                && !descriptions.contains(serviceDescription)) {
            descriptions.add(serviceDescription);
            return new Response("Registered: " + serviceDescription, true);
        }
        return new Response("Already registered: " + serviceDescription, false);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#delete(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response delete(final ServiceDescription description) {
        List<ServiceDescription> list = query(description);
        boolean removed = descriptions.removeAll(list);
        return new Response("Attempted to delete " +list.size() + " matches for " + description, removed);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#queryWithMode(eu.planets_project.services.datatypes.ServiceDescription,
     *      eu.planets_project.ifr.core.registry.impl.Query.MatchingMode)
     */
    public List<ServiceDescription> queryWithMode(
            final ServiceDescription example, final MatchingMode mode) {
        return new Query(descriptions).byExample(example, mode);
    }
    
    /**
     * @param serviceDescription The description to check
     * @return True, if a description with the same endpoint is already
     *         registered
     */
    private boolean endpointPresent(final ServiceDescription serviceDescription) {
        for (ServiceDescription d : descriptions) {
            if (d.getEndpoint() != null
                    && d.getEndpoint().toString().equals(
                            serviceDescription.getEndpoint().toString())) {
                return true;
            }
        }
        return false;
    }

}
