
package eu.planets_project.ifr.core.registry;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
@WebService(name = "ServiceRegistryManager", targetNamespace = "http://planets-project.eu/ifr/core/registry")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ServiceRegistryManager {


    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage clear(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password);

    /**
     * 
     * @param password
     * @param query
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.BindingList
     */
    @WebMethod
    @WebResult(partName = "return")
    public BindingList findBindings(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "query", partName = "query")
        String query);

    /**
     * 
     * @param password
     * @param query
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.OrganizationList
     */
    @WebMethod
    @WebResult(partName = "return")
    public OrganizationList findOrganizations(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "query", partName = "query")
        String query);

    /**
     * 
     * @param password
     * @param query
     * @param category
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.ServiceList
     */
    @WebMethod
    @WebResult(partName = "return")
    public ServiceList findServices(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "query", partName = "query")
        String query,
        @WebParam(name = "category", partName = "category")
        String category);

    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsSchema
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsSchema findTaxonomy(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password);

    /**
     * 
     * @param password
     * @param username
     * @param binding
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage saveBinding(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "binding", partName = "binding")
        PsBinding binding);

    /**
     * 
     * @param password
     * @param freeClassification
     * @param serviceId
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage saveFreeClassification(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "serviceId", partName = "serviceId")
        String serviceId,
        @WebParam(name = "freeClassification", partName = "freeClassification")
        String freeClassification);

    /**
     * 
     * @param password
     * @param username
     * @param organization
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage saveOrganization(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "organization", partName = "organization")
        PsOrganization organization);

    /**
     * 
     * @param password
     * @param classification
     * @param target
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage savePredefinedClassification(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "target", partName = "target")
        String target,
        @WebParam(name = "classification", partName = "classification")
        String classification);

    /**
     * 
     * @param password
     * @param service
     * @param username
     * @return
     *     returns eu.planets_project.ifr.core.registry.PsRegistryMessage
     */
    @WebMethod
    @WebResult(partName = "return")
    public PsRegistryMessage saveService(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password,
        @WebParam(name = "service", partName = "service")
        PsService service);

}
