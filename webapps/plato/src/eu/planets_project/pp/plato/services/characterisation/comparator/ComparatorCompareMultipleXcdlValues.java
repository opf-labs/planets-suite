
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
@WebService(name = "ComparatorCompareMultipleXcdlValues", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ComparatorCompareMultipleXcdlValues {


    /**
     * 
     * @param xcdls
     * @param config
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CompareMultipleXcdlValues", action = "http://planets-project.eu/services/CompareMultipleXcdlValues")
    @WebResult(name = "CompareMultipleXcdlValuesResult", partName = "CompareMultipleXcdlValuesResult")
    public String compareMultipleXcdlValues(
        @WebParam(name = "xcdls", partName = "xcdls")
        StringArray xcdls,
        @WebParam(name = "config", partName = "config")
        String config);

}
