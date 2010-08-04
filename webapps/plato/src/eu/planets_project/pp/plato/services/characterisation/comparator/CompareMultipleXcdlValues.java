
package eu.planets_project.pp.plato.services.characterisation.comparator;

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
@WebServiceClient(name = "CompareMultipleXcdlValues", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/ComparatorCompareMultipleXcdlValues?wsdl")
public class CompareMultipleXcdlValues
    extends Service
{

    private final static URL COMPAREMULTIPLEXCDLVALUES_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/ComparatorCompareMultipleXcdlValues?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        COMPAREMULTIPLEXCDLVALUES_WSDL_LOCATION = url;
    }

    public CompareMultipleXcdlValues(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CompareMultipleXcdlValues() {
        super(COMPAREMULTIPLEXCDLVALUES_WSDL_LOCATION, new QName("http://planets-project.eu/services", "CompareMultipleXcdlValues"));
    }

    /**
     * 
     * @return
     *     returns ComparatorCompareMultipleXcdlValues
     */
    @WebEndpoint(name = "ComparatorCompareMultipleXcdlValuesPort")
    public ComparatorCompareMultipleXcdlValues getComparatorCompareMultipleXcdlValuesPort() {
        return (ComparatorCompareMultipleXcdlValues)super.getPort(new QName("http://planets-project.eu/services", "ComparatorCompareMultipleXcdlValuesPort"), ComparatorCompareMultipleXcdlValues.class);
    }

}
