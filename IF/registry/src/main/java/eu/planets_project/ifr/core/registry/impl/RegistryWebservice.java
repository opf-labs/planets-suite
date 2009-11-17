package eu.planets_project.ifr.core.registry.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.registry.api.MatchingMode;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.RegistryFactory;
import eu.planets_project.ifr.core.registry.api.Response;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Registry web service implementation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Stateless
@WebService(
        name = RegistryWebservice.NAME, 
        serviceName = Registry.NAME, 
        targetNamespace = PlanetsServices.NS,
        // FIXME: a solution to access the web service on metro at all, but exposes all methods from the interface:
        endpointInterface = "eu.planets_project.ifr.core.registry.api.Registry")
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@StreamingAttachment(parseEagerly = true)
public final class RegistryWebservice implements Registry {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private Registry registry = RegistryFactory.getRegistry();
    /***/
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(RegistryWebservice.class.getName());
    /***/
    static final String NAME = "RegistryWebservice";

    /* Query methods available via web service: */

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    @WebMethod
    @WebResult
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#queryWithMode(eu.planets_project.services.datatypes.ServiceDescription,
     *      eu.planets_project.ifr.core.registry.impl.Query.MatchingMode)
     */
    @WebMethod
    @WebResult
    public List<ServiceDescription> queryWithMode(final ServiceDescription example, final MatchingMode mode) {
        return registry.queryWithMode(example, mode);
    }

    /* Methods only available locally, not via web service: */

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        if (serviceDescription == null) {
            throw new IllegalArgumentException("Can't register a service description that is null!");
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

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#clear()
     */
    public Response clear() {
        return registry.clear();
    }

}
