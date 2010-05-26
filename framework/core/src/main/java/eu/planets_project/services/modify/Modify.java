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
package eu.planets_project.services.modify;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Interface for services modifying digital objects.
 * @author Peter Melms
 *
 */
@WebService(
        name = Modify.NAME, 
        targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface Modify extends PlanetsService {
	 /** The interface name */
    String NAME = "Modify";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Modify.NAME);
    
    /**
     * Modify a given object.
     * @param digitalObject the digital object
     * @param inputFormat The input format (this will probably be removed in a subsequent release)
     * @param parameters The parameters, if any
     * @return A modify result response object
     */
    @WebMethod(operationName = Modify.NAME, action = PlanetsServices.NS
            + "/" + Modify.NAME)
    @WebResult(name = Modify.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Modify.NAME, partName = Modify.NAME
            + "Result")
    @RequestWrapper(className="eu.planets_project.services.modify." + Modify.NAME + "Modify")
    @ResponseWrapper(className="eu.planets_project.services.modify." + Modify.NAME + "ModifyResponse")
    public ModifyResult modify(
    		@WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
    				+ "/" + Modify.NAME, partName = "digitalObject") 
    				final DigitalObject digitalObject,
    		@WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
    				+ "/" + Modify.NAME, partName = "inputFormat")
    				final URI inputFormat,
    		@WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
    	            + "/" + Modify.NAME, partName = "parameters") 
    	            List<Parameter> parameters );

}
