
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
@WebServiceClient(name = "BasicCompareTwoXcdlValues", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/ComparatorBasicCompareTwoXcdlValues?wsdl")
public class BasicCompareTwoXcdlValues
    extends Service
{

    private final static URL BASICCOMPARETWOXCDLVALUES_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/ComparatorBasicCompareTwoXcdlValues?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BASICCOMPARETWOXCDLVALUES_WSDL_LOCATION = url;
    }

    public BasicCompareTwoXcdlValues(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BasicCompareTwoXcdlValues() {
        super(BASICCOMPARETWOXCDLVALUES_WSDL_LOCATION, new QName("http://planets-project.eu/services", "BasicCompareTwoXcdlValues"));
    }

    /**
     * 
     * @return
     *     returns ComparatorBasicCompareTwoXcdlValues
     */
    @WebEndpoint(name = "ComparatorBasicCompareTwoXcdlValuesPort")
    public ComparatorBasicCompareTwoXcdlValues getComparatorBasicCompareTwoXcdlValuesPort() {
        return (ComparatorBasicCompareTwoXcdlValues)super.getPort(new QName("http://planets-project.eu/services", "ComparatorBasicCompareTwoXcdlValuesPort"), ComparatorBasicCompareTwoXcdlValues.class);
    }

}
