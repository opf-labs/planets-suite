package eu.planets_project.ifr.core.registry.impl.jaxr;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceTaxonomy;
import eu.planets_project.ifr.core.registry.api.jaxr.model.BindingList;
import eu.planets_project.ifr.core.registry.api.jaxr.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceList;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceRegistryMessage;
import eu.planets_project.services.PlanetsServices;

/**
 * This class exposes the IF service registry as a web service.
 * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry
 * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory
 * @see eu.planets_project.ifr.core.registry.api.jaxr.JaxrServiceRegistry
 * @author Thomas Kraemer <br/>
 * @author Fabian Steeg <br/>
 */
@WebService(name = ServiceRegistryManager.NAME, targetNamespace = PlanetsServices.NS, serviceName = ServiceRegistry.NAME, endpointInterface = "eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry")
@Stateless()
@Local(ServiceRegistry.class)
@Remote(ServiceRegistry.class)
//@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http")
//@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public final class ServiceRegistryManager implements ServiceRegistry {
    private ServiceRegistry registry = ServiceRegistryFactory.getInstance();
    /***/
    private static final long serialVersionUID = 3994805571958430140L;
    /***/
    private static final String LOG_CONFIG_FILE = "eu/planets_project/ifr/core/registry/servreg-log4j.xml";
    /***/
    private static Log log = LogFactory.getLog(ServiceRegistryManager.class
            .getName());

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#saveFreeClassification(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage saveFreeClassification(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "serviceId") final String serviceId,
            @WebParam(name = "freeClassification") final String classificationString) {
        return registry.saveFreeClassification(username, password, serviceId,
                classificationString);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#savePredefinedClassification(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage savePredefinedClassification(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "target") final String serviceId,
            @WebParam(name = "classification") final String classificationId) {
        return registry.savePredefinedClassification(username, password,
                serviceId, classificationId);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#saveService(java.lang.String,
     *      java.lang.String,
     *      eu.planets_project.ifr.core.registry.api.jaxr.model.PsService)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage saveService(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "service") final PsService service) {
        return registry.saveService(username, password, service);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#saveBinding(java.lang.String,
     *      java.lang.String,
     *      eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage saveBinding(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "binding") final PsBinding binding) {
        return registry.saveBinding(username, password, binding);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#findTaxonomy(java.lang.String,
     *      java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceTaxonomy findTaxonomy(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password) {
        return registry.findTaxonomy(username, password);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#findOrganizations(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public OrganizationList findOrganizations(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "query") final String query) {
        return registry.findOrganizations(username, password, query);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#findServices(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceList findServices(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "query") final String queryStr,
            @WebParam(name = "category") final String categoryId) {
        return registry.findServices(username, password, queryStr, categoryId);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#findBindings(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public BindingList findBindings(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "query") final String servKey) {
        return registry.findBindings(username, password, servKey);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#clear(java.lang.String,
     *      java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage clear(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password) {
        return registry.clear(username, password);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#saveOrganization(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage saveOrganization(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "organization") final PsOrganization organization) {
        ServiceRegistryMessage saveOrganization = registry.saveOrganization(
                username, password, organization);
        return saveOrganization;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#deleteBinding(eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage deleteBinding(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "binding") final PsBinding serviceBinding) {
        return registry.deleteBinding(username, password, serviceBinding);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#deleteOrganization(eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage deleteOrganization(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "organization") final PsOrganization organization) {
        return registry.deleteOrganization(username, password, organization);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#deleteService(eu.planets_project.ifr.core.registry.api.jaxr.model.PsService)
     */
    @WebMethod
    @WebResult
    public ServiceRegistryMessage deleteService(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "service") final PsService service) {
        return registry.deleteService(username, password, service);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry#findServicesForInputFormats(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String[])
     */
    @WebMethod
    @WebResult
    public ServiceList findServicesForInputFormats(
            @WebParam(name = "username") final String username,
            @WebParam(name = "password") final String password,
            @WebParam(name = "type") final String type,
            @WebParam(name = "inputFormats") final String... inputFormats) {
        return registry.findServicesForInputFormats(username, password, type,
                inputFormats);
    }
}
