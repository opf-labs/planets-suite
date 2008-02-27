
package eu.planets_project.ifr.core.wdt.common.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Tue Feb 12 17:07:12 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "ServiceRegistryManager", targetNamespace = "http://planets-project.eu/ifr/core/registry")
public interface ServiceRegistryManager {


    /**
     * 
     * @param password
     * @param userName
     * @throws JAXRException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "configure", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.Configure")
    @ResponseWrapper(localName = "configureResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.ConfigureResponse")
    public void configure(
        @WebParam(name = "userName", targetNamespace = "")
        String userName,
        @WebParam(name = "password", targetNamespace = "")
        String password)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedOrgnztns", targetNamespace = "")
    @RequestWrapper(localName = "deleteOrganization", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteOrganization")
    @ResponseWrapper(localName = "deleteOrganizationResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteOrganizationResponse")
    public String deleteOrganization()
        throws JAXRException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedSrvcs", targetNamespace = "")
    @RequestWrapper(localName = "deleteService", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteService")
    @ResponseWrapper(localName = "deleteServiceResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteServiceResponse")
    public String deleteService()
        throws JAXRException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedBndngs", targetNamespace = "")
    @RequestWrapper(localName = "deleteServiceBindings", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteServiceBindings")
    @ResponseWrapper(localName = "deleteServiceBindingsResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.DeleteServiceBindingsResponse")
    public String deleteServiceBindings()
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param query
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "matchedBndngs", targetNamespace = "")
    @RequestWrapper(localName = "findBindings", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindBindings")
    @ResponseWrapper(localName = "findBindingsResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindBindingsResponse")
    public String findBindings(
        @WebParam(name = "query", targetNamespace = "")
        String query);

    /**
     * 
     * @param query
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "matchedOrgnztns", targetNamespace = "")
    @RequestWrapper(localName = "findOrganizations", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindOrganizations")
    @ResponseWrapper(localName = "findOrganizationsResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindOrganizationsResponse")
    public String findOrganizations(
        @WebParam(name = "query", targetNamespace = "")
        String query);

    /**
     * 
     * @param queryStr
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "matchedSrvcs", targetNamespace = "")
    @RequestWrapper(localName = "findServices", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindServices")
    @ResponseWrapper(localName = "findServicesResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.FindServicesResponse")
    public String findServices(
        @WebParam(name = "queryStr", targetNamespace = "")
        String queryStr);

    /**
     * 
     * @param orgContactname
     * @param orgDesc
     * @param orgEmail
     * @param orgName
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveOrgResp", targetNamespace = "")
    @RequestWrapper(localName = "saveOrganization", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveOrganization")
    @ResponseWrapper(localName = "saveOrganizationResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveOrganizationResponse")
    public String saveOrganization(
        @WebParam(name = "orgName", targetNamespace = "")
        String orgName,
        @WebParam(name = "orgDesc", targetNamespace = "")
        String orgDesc,
        @WebParam(name = "orgContactname", targetNamespace = "")
        String orgContactname,
        @WebParam(name = "orgEmail", targetNamespace = "")
        String orgEmail)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param serviceName
     * @param serviceDesc
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServResp", targetNamespace = "")
    @RequestWrapper(localName = "saveService", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveService")
    @ResponseWrapper(localName = "saveServiceResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveServiceResponse")
    public String saveService(
        @WebParam(name = "serviceName", targetNamespace = "")
        String serviceName,
        @WebParam(name = "serviceDesc", targetNamespace = "")
        String serviceDesc)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param servBindURI
     * @param servBindDesc
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServBind", targetNamespace = "")
    @RequestWrapper(localName = "saveServiceBinding", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveServiceBinding")
    @ResponseWrapper(localName = "saveServiceBindingResponse", targetNamespace = "http://planets-project.eu/ifr/core/registry", className = "eu.planets_project.ifr.core.wdt.common.services.SaveServiceBindingResponse")
    public String saveServiceBinding(
        @WebParam(name = "servBindDesc", targetNamespace = "")
        String servBindDesc,
        @WebParam(name = "servBindURI", targetNamespace = "")
        String servBindURI)
        throws JAXRException_Exception
    ;

}
