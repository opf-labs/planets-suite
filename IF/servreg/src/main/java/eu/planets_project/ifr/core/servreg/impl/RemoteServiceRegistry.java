package eu.planets_project.ifr.core.servreg.impl;

import java.util.List;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.servreg.api.MatchingMode;
import eu.planets_project.ifr.core.servreg.api.Response;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Service registry web service implementation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Stateless
@WebService(
        name = RemoteServiceRegistry.NAME, 
        serviceName = ServiceRegistry.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.ifr.core.servreg.api.ServiceRegistry")
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public final class RemoteServiceRegistry implements ServiceRegistry {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private ServiceRegistry registry = ServiceRegistryFactory.getServiceRegistry();
    /***/
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(RemoteServiceRegistry.class.getName());
    /***/
    static final String NAME = "RemoteServiceRegistry";

    /* Query methods available via web service: */

    /**
     * {@inheritDoc}
     * @see ServiceRegistry#query(ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#queryWithMode(eu.planets_project.services.datatypes.ServiceDescription, eu.planets_project.ifr.core.servreg.api.MatchingMode)
     */
    public List<ServiceDescription> queryWithMode(final ServiceDescription example, final MatchingMode mode) {
        return registry.queryWithMode(example, mode);
    }

    /* Methods only available locally, not via web service: */

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        if (serviceDescription == null) {
            throw new IllegalArgumentException("Can't register a service description that is null!");
        }
        return registry.register(serviceDescription);
    }

    /**
     * {@inheritDoc}
     * @see ServiceRegistry#delete(ServiceDescription)
     */
    public Response delete(final ServiceDescription example) {
        return registry.delete(example);
    }

    /**
     * {@inheritDoc}
     * @see ServiceRegistry#clear()
     */
    public Response clear() {
        return registry.clear();
    }

}
