
package eu.planets_project.pp.plato.services.characterisation.extractor;

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
@WebService(name = "Extractor2URI", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Extractor2URI {


    /**
     * 
     * @param inputImageURI
     * @param inputXcelURI
     * @return
     *     returns java.lang.String
     * @throws PlanetsException_Exception
     */
    @WebMethod(operationName = "BasicCharacteriseOneBinaryXCELtoURI", action = "http://planets-project.eu/services/BasicCharacteriseOneBinaryXCELtoURI")
    @WebResult(name = "BasicCharacteriseOneBinaryXCELtoURIResult", partName = "BasicCharacteriseOneBinaryXCELtoURIResult")
    public String basicCharacteriseOneBinaryXCELtoURI(
        @WebParam(name = "input_image_URI", partName = "input_image_URI")
        String inputImageURI,
        @WebParam(name = "input_xcel_URI", partName = "input_xcel_URI")
        String inputXcelURI)
        throws PlanetsException_Exception
    ;

    /**
     * 
     * @param inputImageURI
     * @return
     *     returns java.lang.String
     * @throws PlanetsException_Exception
     */
    @WebMethod(operationName = "BasicCharacteriseOneBinaryXCELtoURIJustBinary", action = "http://planets-project.eu/services/BasicCharacteriseOneBinaryXCELtoURI")
    @WebResult(name = "BasicCharacteriseOneBinaryXCELtoURIResult", partName = "BasicCharacteriseOneBinaryXCELtoURIResult")
    public String basicCharacteriseOneBinaryXCELtoURIJustBinary(
        @WebParam(name = "input_image_URI", partName = "input_image_URI")
        String inputImageURI)
        throws PlanetsException_Exception
    ;

}
