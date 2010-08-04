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

package eu.planets_project.ifr.core.registry;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.planets_project.ifr.core.registry package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.planets_project.ifr.core.registry
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PsRegistryObject }
     * 
     */
    public PsRegistryObject createPsRegistryObject() {
        return new PsRegistryObject();
    }

    /**
     * Create an instance of {@link PsBinding }
     * 
     */
    public PsBinding createPsBinding() {
        return new PsBinding();
    }

    /**
     * Create an instance of {@link ServiceList }
     * 
     */
    public ServiceList createServiceList() {
        return new ServiceList();
    }

    /**
     * Create an instance of {@link PsService }
     * 
     */
    public PsService createPsService() {
        return new PsService();
    }

    /**
     * Create an instance of {@link PsSchema }
     * 
     */
    public PsSchema createPsSchema() {
        return new PsSchema();
    }

    /**
     * Create an instance of {@link PsRegistryMessage }
     * 
     */
    public PsRegistryMessage createPsRegistryMessage() {
        return new PsRegistryMessage();
    }

    /**
     * Create an instance of {@link OrganizationList }
     * 
     */
    public OrganizationList createOrganizationList() {
        return new OrganizationList();
    }

    /**
     * Create an instance of {@link PsCategory }
     * 
     */
    public PsCategory createPsCategory() {
        return new PsCategory();
    }

    /**
     * Create an instance of {@link PsOrganization }
     * 
     */
    public PsOrganization createPsOrganization() {
        return new PsOrganization();
    }

    /**
     * Create an instance of {@link BindingList }
     * 
     */
    public BindingList createBindingList() {
        return new BindingList();
    }

}
