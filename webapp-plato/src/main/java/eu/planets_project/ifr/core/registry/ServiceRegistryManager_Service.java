
package eu.planets_project.ifr.core.registry;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


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
@WebServiceClient(name = "ServiceRegistryManager", targetNamespace = "http://planets-project.eu/ifr/core/registry", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl")
public class ServiceRegistryManager_Service
    extends Service
{

    private final static URL SERVICEREGISTRYMANAGER_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SERVICEREGISTRYMANAGER_WSDL_LOCATION = url;
    }

    public ServiceRegistryManager_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServiceRegistryManager_Service() {
        super(SERVICEREGISTRYMANAGER_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
    }

    /**
     * 
     * @return
     *     returns ServiceRegistryManager
     */
    @WebEndpoint(name = "ServiceRegistryManagerPort")
    public ServiceRegistryManager getServiceRegistryManagerPort() {
        return (ServiceRegistryManager)super.getPort(new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManagerPort"), ServiceRegistryManager.class);
    }

}
