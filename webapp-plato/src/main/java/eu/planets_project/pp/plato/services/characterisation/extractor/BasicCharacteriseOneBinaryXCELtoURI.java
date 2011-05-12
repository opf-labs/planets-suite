
package eu.planets_project.pp.plato.services.characterisation.extractor;

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
@WebServiceClient(name = "BasicCharacteriseOneBinaryXCELtoURI", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor2URI?wsdl")
public class BasicCharacteriseOneBinaryXCELtoURI
    extends Service
{

    private final static URL BASICCHARACTERISEONEBINARYXCELTOURI_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor2URI?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BASICCHARACTERISEONEBINARYXCELTOURI_WSDL_LOCATION = url;
    }

    public BasicCharacteriseOneBinaryXCELtoURI(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BasicCharacteriseOneBinaryXCELtoURI() {
        super(BASICCHARACTERISEONEBINARYXCELTOURI_WSDL_LOCATION, new QName("http://planets-project.eu/services", "BasicCharacteriseOneBinaryXCELtoURI"));
    }

    /**
     * 
     * @return
     *     returns Extractor2URI
     */
    @WebEndpoint(name = "Extractor2URIPort")
    public Extractor2URI getExtractor2URIPort() {
        return (Extractor2URI)super.getPort(new QName("http://planets-project.eu/services", "Extractor2URIPort"), Extractor2URI.class);
    }

}
