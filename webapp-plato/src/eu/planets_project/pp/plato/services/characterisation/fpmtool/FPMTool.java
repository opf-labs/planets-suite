
package eu.planets_project.pp.plato.services.characterisation.fpmtool;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


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
@WebService(name = "FPMTool", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface FPMTool {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws PlanetsException_Exception
     */
    @WebMethod(operationName = "BasicCompareFormatProperties", action = "http://planets-project.eu/services/BasicCompareFormatProperties")
    @WebResult(name = "BasicCompareFormatPropertiesResult", partName = "BasicCompareFormatPropertiesResult")
    public String basicCompareFormatProperties(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws PlanetsException_Exception
    ;

}
