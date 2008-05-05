
package eu.planets_project.ifr.core.wdt.common.services.serviceRegistry;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Tue Apr 22 14:45:59 CEST 2008
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
     * @param userName
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedSrvcs", partName = "deletedSrvcs")
    public String deleteService(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @return
     *     returns java.lang.String
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "deletedBndngs", partName = "deletedBndngs")
    public String deleteServiceBindings(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param serviceKey
     * @return
     *     returns eu.planets_project.ifr.core.registry.BindingList
     */
    @WebMethod
    @WebResult(name = "bindings", partName = "bindings")
    public BindingList findBindings(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceKey", partName = "serviceKey")
        String serviceKey);

    /**
     * 
     * @param password
     * @param userName
     * @param queryStr
     * @return
     *     returns eu.planets_project.ifr.core.registry.ServiceList
     */
    @WebMethod
    @WebResult(name = "services", partName = "services")
    public ServiceList findServices(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "queryStr", partName = "queryStr")
        String queryStr);

    /**
     * 
     */
    @WebMethod
    public void retrieveTaxonomy();

    /**
     * 
     * @param password
     * @param userName
     * @param psservice
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServResp", partName = "saveServResp")
    public PsRegistryMessage saveService(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "psservice", partName = "psservice")
        PsService psservice)
        throws JAXRException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param serviceBinding
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     * @throws JAXRException_Exception
     */
    @WebMethod
    @WebResult(name = "saveServiceBinding", partName = "saveServiceBinding")
    public PsRegistryMessage saveServiceBinding(
        @WebParam(name = "userName", partName = "userName")
        String userName,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceBinding", partName = "serviceBinding")
        PsBinding serviceBinding)
        throws JAXRException_Exception
    ;

}
