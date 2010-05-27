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
package eu.planets_project.services.validate;

import java.net.URI;
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
 * Validation of a DigitalObject.
 *
 * @author Fabian Steeg, Andrew Jackson, Asger Blekinge-Rasmussen
 */
@WebService(name = Validate.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface Validate extends PlanetsService {

    /** The interface name */
    public static final String NAME = "Validate";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS,
            Validate.NAME);

    /**
     * @param digitalObject
     *            The Digital Object to be validated.
     * @param format
     *            The format that digital object purports to be in
     * @param parameters
     *            a list of parameters to provide fine grained tool control
     * @return Returns a ValidateResult object with the result of the validation
     */
    @WebMethod(operationName = Validate.NAME, action = PlanetsServices.NS
            + "/" + Validate.NAME)
    @WebResult(name = Validate.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Validate.NAME, partName = Validate.NAME
            + "Result")
    public ValidateResult validate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "digitalObject")
            DigitalObject digitalObject,
            @WebParam(name = "format", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "format")
            URI format,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "parameters")
            List<Parameter> parameters );

}
