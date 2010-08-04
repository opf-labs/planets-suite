
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
@WebServiceClient(name = "BasicCharacteriseOneBinaryXCELtoBinary", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor2Binary?wsdl")
public class BasicCharacteriseOneBinaryXCELtoBinary
    extends Service
{

    private final static URL BASICCHARACTERISEONEBINARYXCELTOBINARY_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor2Binary?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BASICCHARACTERISEONEBINARYXCELTOBINARY_WSDL_LOCATION = url;
    }

    public BasicCharacteriseOneBinaryXCELtoBinary(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BasicCharacteriseOneBinaryXCELtoBinary() {
        super(BASICCHARACTERISEONEBINARYXCELTOBINARY_WSDL_LOCATION, new QName("http://planets-project.eu/services", "BasicCharacteriseOneBinaryXCELtoBinary"));
    }

    /**
     * 
     * @return
     *     returns Extractor2Binary
     */
    @WebEndpoint(name = "Extractor2BinaryPort")
    public Extractor2Binary getExtractor2BinaryPort() {
        return (Extractor2Binary)super.getPort(new QName("http://planets-project.eu/services", "Extractor2BinaryPort"), Extractor2Binary.class);
    }

}
