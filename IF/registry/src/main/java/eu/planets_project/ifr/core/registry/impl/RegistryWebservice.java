package eu.planets_project.ifr.core.registry.impl;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.Response;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Registry web service implementation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Local(Registry.class)
@Remote(Registry.class)
@Stateless()
@WebService(name = RegistryWebservice.NAME, serviceName = Registry.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.ifr.core.registry.api.Registry")
public final class RegistryWebservice implements Registry {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private Registry registry = PersistentRegistry.getInstance(CoreRegistry
            .getInstance());
    /***/
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(RegistryWebservice.class
            .getName());
    /***/
    static final String NAME = "RegistryWebservice";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#clear()
     */
    public Response clear() {
        return registry.clear();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        if (serviceDescription == null) {
            throw new IllegalArgumentException(
                    "Can't register a service description that is null!");
        }
        return registry.register(serviceDescription);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#delete(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response delete(final ServiceDescription example) {
        return registry.delete(example);
    }

}
