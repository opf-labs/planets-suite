
package eu.planets_project.pp.plato.services.characterisation.fpmtool;

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
@WebServiceClient(name = "BasicCompareFormatProperties", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-fpmtool/FPMTool?wsdl")
public class BasicCompareFormatProperties
    extends Service
{

    private final static URL BASICCOMPAREFORMATPROPERTIES_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/pserv-pc-fpmtool/FPMTool?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BASICCOMPAREFORMATPROPERTIES_WSDL_LOCATION = url;
    }

    public BasicCompareFormatProperties(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BasicCompareFormatProperties() {
        super(BASICCOMPAREFORMATPROPERTIES_WSDL_LOCATION, new QName("http://planets-project.eu/services", "BasicCompareFormatProperties"));
    }

    /**
     * 
     * @return
     *     returns FPMTool
     */
    @WebEndpoint(name = "FPMToolPort")
    public FPMTool getFPMToolPort() {
        return (FPMTool)super.getPort(new QName("http://planets-project.eu/services", "FPMToolPort"), FPMTool.class);
    }

}
