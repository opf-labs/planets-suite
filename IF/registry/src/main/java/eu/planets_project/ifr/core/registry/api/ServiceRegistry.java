/**
 * Author: Thomas Krämer Email: thomas.kraemer@uni-koeln.de Created : 18.02.2008
 */
/**
 * @author : Thomas Krämer
 * @Email : thomas.kraemer@uni-koeln.de Created : 18.02.2008
 */
package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import eu.planets_project.ifr.core.registry.api.model.BindingList;
import eu.planets_project.ifr.core.registry.api.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceList;
import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;

/**
 * Interface for the service registry web service.
 * @author Thomas Krämer (thomas.kraemer@uni-koeln.de)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = ServiceRegistry.NAME, serviceName = ServiceRegistry.NAME, targetNamespace = ServiceRegistry.NS)
public interface ServiceRegistry {
    /***/
    String NAME = "ServiceRegistryManager";
    /***/
    String NS = "http://planets-project.eu/ifr/core/registry";

    /**
     * @param username The username
     * @param password The password
     * @param service an existing or new service that should be saved.
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage saveService(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "service") PsService service);

    /**
     * @param username The username
     * @param password The password
     * @param binding a new or existing serviceBinding that should be saved
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage saveBinding(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "binding") PsBinding binding);

    /**
     * Create a new organization in the registry.
     * @param username The username
     * @param password The password
     * @param organization The org
     * @return Returns a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage saveOrganization(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "organization") PsOrganization organization);

    /**
     * @param username The username
     * @param password The password
     * @param serviceId The object to be categorized
     * @param classificationId The classification the registry object should be
     *        added to
     * @return Retunrs a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage savePredefinedClassification(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "target") String serviceId,
            @WebParam(name = "classification") String classificationId);

    /**
     * <br/>
     * Used to add a free classification (any String) to an registry object.
     * @param username The username
     * @param password The password
     * @param serviceId The ID
     * @param classificationString has to be filled with source format,action
     *        and target format separate by #, facilitates search by migration
     *        path if used in findServices as categoryId parameter "#jpeg" =
     *        Services with jpeg as target format, "jpeg#" = Services with jpeg
     *        as source format
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage saveFreeClassification(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "serviceId") String serviceId,
            @WebParam(name = "freeClassification") String classificationString);

    /**
     * @param username The username
     * @param password The password
     * @return Retruns the planets service classification scheme currently in
     *         use in the service registry
     */
    @WebMethod
    @WebResult
    ServiceTaxonomy findTaxonomy(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password);

    /**
     * @param username The username
     * @param password The password
     * @param query The name query, use % as wild card or substrings of service
     *        names
     * @param category The category ID. Optional, pass empty String for a search
     *        by name, pass a category identifier as listed in the taxonomy
     *        (@see getTaxonomy())
     * @return a list of services that match the given criteria
     */
    @WebMethod
    @WebResult
    ServiceList findServices(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "query") String query,
            @WebParam(name = "category") String category);

    /**
     * In this PLANETS IF release, most of the services are atomic, i.e. one
     * servicebinding per service.
     * @param username The username
     * @param password The password
     * @param serviceKey key of a service in question
     * @return bindings matching the criteria
     */
    @WebMethod
    @WebResult
    BindingList findBindings(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "query") String serviceKey);

    /**
     * In this PLANETS IF release, most of the services are atomic, i.e. one
     * servicebinding per service.
     * @param username The username
     * @param password The password
     * @param query a key of a service in question
     * @return bindings matching the criteria
     */
    @WebMethod
    @WebResult
    OrganizationList findOrganizations(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "query") String query);

    /**
     * Removes all registered services and organizations from the registry.
     * @param username The username
     * @param password The password
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage clear(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password);

    /**
     * @param username The username
     * @param password The password
     * @param organization The organization to delete
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage deleteOrganization(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "organization") PsOrganization organization);

    /**
     * @param username The username
     * @param password The password
     * @param service The service to delete
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage deleteService(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "service") PsService service);

    /**
     * @param username The username
     * @param password The password
     * @param serviceBinding The binding to delete
     * @return a status message
     */
    @WebMethod
    @WebResult
    ServiceRegistryMessage deleteBinding(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "binding") PsBinding serviceBinding);

    /**
     * @param username The username
     * @param password The password
     * @param type The type of the service to find (e.g. "Migration",
     *        "Identification"); pass null you you want to find all
     * @param inputFormats The input formats the services should support; pass
     *        null or omit if you want to find all
     * @return The matching services
     */
    ServiceList findServicesForInputFormats(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "type") String type,
            @WebParam(name = "inputFormats") String... inputFormats);

}