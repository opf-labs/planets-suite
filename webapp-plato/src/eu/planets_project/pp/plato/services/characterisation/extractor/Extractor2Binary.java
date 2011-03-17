
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
@WebService(name = "Extractor2Binary", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Extractor2Binary {


    /**
     * 
     * @param binary
     * @param xcelString
     * @return
     *     returns byte[]
     * @throws PlanetsException_Exception
     */
    @WebMethod(operationName = "BasicCharacteriseOneBinaryXCELtoBinary", action = "http://planets-project.eu/services/BasicCharacteriseOneBinaryXCELtoBinary")
    @WebResult(name = "BasicCharacteriseOneBinaryXCELtoBinaryResult", partName = "BasicCharacteriseOneBinaryXCELtoBinaryResult")
    public byte[] basicCharacteriseOneBinaryXCELtoBinary(
        @WebParam(name = "binary", partName = "binary")
        byte[] binary,
        @WebParam(name = "XCEL_String", partName = "XCEL_String")
        String xcelString)
        throws PlanetsException_Exception
    ;

    /**
     * 
     * @param binary
     * @return
     *     returns byte[]
     * @throws PlanetsException_Exception
     */
    @WebMethod(operationName = "BasicCharacteriseOneBinaryXCELtoBinaryJustBinary", action = "http://planets-project.eu/services/BasicCharacteriseOneBinaryXCELtoBinary")
    @WebResult(name = "BasicCharacteriseOneBinaryXCELtoBinaryResult", partName = "BasicCharacteriseOneBinaryXCELtoBinaryResult")
    public byte[] basicCharacteriseOneBinaryXCELtoBinaryJustBinary(
        @WebParam(name = "binary", partName = "binary")
        byte[] binary)
        throws PlanetsException_Exception
    ;

}
