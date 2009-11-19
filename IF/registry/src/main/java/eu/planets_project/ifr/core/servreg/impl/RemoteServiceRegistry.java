package eu.planets_project.ifr.core.servreg.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.servreg.api.MatchingMode;
import eu.planets_project.ifr.core.servreg.api.Response;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;

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
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@StreamingAttachment(parseEagerly = true)
public final class RemoteServiceRegistry implements ServiceRegistry {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private ServiceRegistry registry = ServiceRegistryFactory.getRegistry();
    /***/
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(RemoteServiceRegistry.class.getName());
    /***/
    static final String NAME = "RemoteServiceRegistry";

    /* Query methods available via web service: */

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#queryWithMode(eu.planets_project.services.datatypes.ServiceDescription,
     *      eu.planets_project.ifr.core.registry.impl.Query.MatchingMode)
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
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#delete(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response delete(final ServiceDescription example) {
        return registry.delete(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#clear()
     */
    public Response clear() {
        return registry.clear();
    }

}
