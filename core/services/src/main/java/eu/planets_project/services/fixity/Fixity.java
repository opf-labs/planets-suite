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
/**
 * 
 */
package eu.planets_project.services.fixity;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * @author CFWilson
 *
 */
@WebService(name = Fixity.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface Fixity extends PlanetsService {
    /** The interface name */
    String NAME = "Fixity";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Fixity.NAME);

    /**
     * @param digitalObject The Digital Object to be identified.
     * @param parameters 
     * @return Returns a Types object containing the identification result
     */
    @WebMethod(operationName = Fixity.NAME, action = PlanetsServices.NS + "/"
            + Fixity.NAME)
    @WebResult(name = Fixity.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Fixity.NAME, partName = Fixity.NAME + "Result")
    FixityResult calculateChecksum(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Fixity.NAME, partName = "digitalObject")
            DigitalObject digitalObject,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Fixity.NAME, partName = "parameters") 
            List<Parameter> parameters
            );
}
