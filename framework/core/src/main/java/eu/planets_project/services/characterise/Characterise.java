/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.characterise;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Characterisation of one digital object. This is the
 * generic characterisation interface for characterisation tools like the XCL
 * Extractor and the New Zealand Metadata Extractor. It supports service
 * description to facilitate discovery, allows parameters to be discovered and
 * submitted to control the underlying tools (if needed).
 * @author Peter Melms (peter.melms@uni-koeln.de), Andrew Jackson
 *         <Andrew.Jackson@bl.uk>
 */

@WebService(name = Characterise.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface Characterise extends PlanetsService {

    /** The service name. */
    String NAME = "Characterise";
    /** The qualified name. */
    QName QNAME = new QName(PlanetsServices.NS, Characterise.NAME);

    /**
     * @param digitalObject The digital object to characterise
     * @param parameters for fine grained tool control
     * @return A list of properties, wrapped into a CharacteriseResult
     */
    @WebMethod(operationName = Characterise.NAME, action = PlanetsServices.NS
            + "/" + Characterise.NAME)
    @WebResult(name = Characterise.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Characterise.NAME, partName = Characterise.NAME + "Result")
    @RequestWrapper(className = "eu.planets_project.services.characterise."
            + Characterise.NAME + "Characterise")
    @ResponseWrapper(className = "eu.planets_project.services.characterise."
            + Characterise.NAME + "CharacteriseResponse")
    CharacteriseResult characterise(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Characterise.NAME, partName = "digitalObject") final DigitalObject digitalObject,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Characterise.NAME, partName = "parameters") List<Parameter> parameters);

    /**
     * @param formatURI A format URI
     * @return The properties this characterisation service extracts for the
     *         given file format
     */
    @WebMethod(operationName = Characterise.NAME + "_" + "listProperties", action = PlanetsServices.NS
            + "/" + Characterise.NAME + "/" + "listProperties")
    @WebResult(name = Characterise.NAME + "Property_List", targetNamespace = PlanetsServices.NS
            + "/" + Characterise.NAME, partName = Characterise.NAME
            + "Property_List")
    @ResponseWrapper(className = "eu.planets_project.services.characterise."
            + Characterise.NAME + "listPropertiesResponse")
   List<Property> listProperties(URI formatURI);
}
