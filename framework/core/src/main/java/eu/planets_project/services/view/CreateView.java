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
package eu.planets_project.services.view;

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
 * The purpose of the CreateView operation is to take a Digital Object and to wrap it up so
 * that the user can examine it more easily.  The service returns a URL pointing to the web site or 
 * downloadable package that will provide the rendering experience to the user.
 * 
 * It is envisaged that this URL will be passed back to the user as a new link to open in a new browser window.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(name = CreateView.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface CreateView extends PlanetsService {

    /** The interface name. */
    public static final String NAME = "CreateView";
    /** The qualified name. */
    public static final QName QNAME = new QName(PlanetsServices.NS,
            CreateView.NAME);

    /**
     * @param digitalObjects
     *            The Digital Objects to be viewed.
     * @return Returns a CreateViewResult that contains to a URL where the objects may be viewed.
     */
    @WebMethod(operationName = CreateView.NAME, action = PlanetsServices.NS
            + "/" + CreateView.NAME)
    @WebResult(name = CreateView.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CreateView.NAME, partName = CreateView.NAME
            + "Result")
    public CreateViewResult createView(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + CreateView.NAME, partName = "digitalObjects") 
            List<DigitalObject> digitalObjects,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + CreateView.NAME, partName = "parameters")
            List<Parameter> parameters );

    
    /**
     * @param sessionIdentifier A key that allows the service  to uniquely identify the user's session.
     * @return A description of the current state of that session, optionally including properties determined from the session.
     */
    @WebMethod(operationName = CreateView.NAME + "_status", action = PlanetsServices.NS
            + "/" + CreateView.NAME + "/status")
    @WebResult(name = CreateView.NAME + "Status", targetNamespace = PlanetsServices.NS
            + "/" + CreateView.NAME, partName = CreateView.NAME
            + "Status")
    public ViewStatus getViewStatus( 
            @WebParam(name = "sessionIdentifier", targetNamespace = PlanetsServices.NS
                    + "/" + CreateView.NAME, partName = "sessionIdentifier") 
            String sessionIdentifier );
    
    /**
     * FIXME Should this have an optional list of parameters?
     * @param sessionIdentifier A key that allows the service  to uniquely identify the user's session.
     * @return A description of the current state of that session, optionally including properties determined from the session.
     */
    @WebMethod(operationName = CreateView.NAME + "_viewactionresult", action = PlanetsServices.NS
            + "/" + CreateView.NAME + "/viewactionresult")
    @WebResult(name = CreateView.NAME + "ViewActionResult", targetNamespace = PlanetsServices.NS
            + "/" + CreateView.NAME, partName = CreateView.NAME
            + "ViewActionResult")
    public ViewActionResult doAction( 
            @WebParam(name = "sessionIdentifier", targetNamespace = PlanetsServices.NS
                    + "/" + CreateView.NAME, partName = "sessionIdentifier") 
            String sessionIdentifier, String action );
    
    
}
