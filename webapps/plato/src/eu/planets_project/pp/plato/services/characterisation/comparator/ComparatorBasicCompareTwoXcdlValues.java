
package eu.planets_project.pp.plato.services.characterisation.comparator;

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
@WebService(name = "ComparatorBasicCompareTwoXcdlValues", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ComparatorBasicCompareTwoXcdlValues {


    /**
     * 
     * @param xcdl2
     * @param xcdl1
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "BasicCompareTwoXcdlValues", action = "http://planets-project.eu/services/BasicCompareTwoXcdlValues")
    @WebResult(name = "BasicCompareTwoXcdlValuesResult", partName = "BasicCompareTwoXcdlValuesResult")
    public String basicCompareTwoXcdlValues(
        @WebParam(name = "xcdl1", partName = "xcdl1")
        String xcdl1,
        @WebParam(name = "xcdl2", partName = "xcdl2")
        String xcdl2);

    /**
     * 
     * @param xcdl2Name
     * @param xcdl1Name
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String basicCompareTwoXcdlFiles(
        @WebParam(name = "xcdl1Name", partName = "xcdl1Name")
        String xcdl1Name,
        @WebParam(name = "xcdl2Name", partName = "xcdl2Name")
        String xcdl2Name);

    /**
     * 
     * @param xcdl2Base64
     * @param xcdl1Base64
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String basicCompareTwoXcdlValuesBase64(
        @WebParam(name = "xcdl1Base64", partName = "xcdl1Base64")
        String xcdl1Base64,
        @WebParam(name = "xcdl2Base64", partName = "xcdl2Base64")
        String xcdl2Base64);

    /**
     * 
     * @param config
     * @param xcdl2
     * @param xcdl1
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String compareTwoXcdlValues(
        @WebParam(name = "xcdl1", partName = "xcdl1")
        String xcdl1,
        @WebParam(name = "xcdl2", partName = "xcdl2")
        String xcdl2,
        @WebParam(name = "config", partName = "config")
        String config);

    /**
     * 
     * @param configBase64
     * @param xcdl2Base64
     * @param xcdl1Base64
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String compareTwoXcdlValuesBase64(
        @WebParam(name = "xcdl1Base64", partName = "xcdl1Base64")
        String xcdl1Base64,
        @WebParam(name = "xcdl2Base64", partName = "xcdl2Base64")
        String xcdl2Base64,
        @WebParam(name = "configBase64", partName = "configBase64")
        String configBase64);

}
