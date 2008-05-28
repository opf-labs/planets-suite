
package eu.planets_project.ifr.core.wdt.common.services.serviceRegistry;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Thu May 22 14:03:44 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "ServiceRegistryManager", targetNamespace = "http://planets-project.eu/ifr/core/registry")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ServiceRegistryManager {


    /**
     * 
     * @param password
     * @param serviceId
     * @param username
     * @param categoryId
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsRegistryMessage
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "addedClassificationTo", partName = "addedClassificationTo")
    public PsRegistryMessage addClassificationTo(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceId", partName = "serviceId")
        String serviceId,
        @WebParam(name = "categoryId", partName = "categoryId")
        String categoryId)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedSrvcs", partName = "deletedSrvcs")
    public String deleteService(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedBndngs", partName = "deletedBndngs")
    public String deleteServiceBindings(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param serviceKey
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.BindingList
     */
    @WebMethod
    @WebResult(name = "bindings", partName = "bindings")
    public BindingList findBindings(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceKey", partName = "serviceKey")
        String serviceKey);

    /**
     * 
     * @param password
     * @param username
     * @param querystr
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.OrganizationList
     */
    @WebMethod
    @WebResult(name = "organizations", partName = "organizations")
    public OrganizationList findOrganizations(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "querystr", partName = "querystr")
        String querystr);

    /**
     * 
     * @param password
     * @param username
     * @param categoryId
     * @param queryStr
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.ServiceList
     */
    @WebMethod
    @WebResult(name = "services", partName = "services")
    public ServiceList findServices(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "queryStr", partName = "queryStr")
        String queryStr,
        @WebParam(name = "categoryId", partName = "categoryId")
        String categoryId);

    /**
     * 
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsSchema
     */
    @WebMethod
    @WebResult(name = "JAXRClassificationScheme", partName = "JAXRClassificationScheme")
    public PsSchema getTaxonomy();

    /**
     * 
     * @param password
     * @param psservice
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsRegistryMessage
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServResp", partName = "saveServResp")
    public PsRegistryMessage saveService(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "psservice", partName = "psservice")
        PsService psservice)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param serviceBinding
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsRegistryMessage
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServiceBinding", partName = "saveServiceBinding")
    public PsRegistryMessage saveServiceBinding(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceBinding", partName = "serviceBinding")
        PsBinding serviceBinding)
        throws JAXRException_Exception
    ;

}
