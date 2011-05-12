
package eu.planets_project.pp.plato.services.characterisation.fpmtool;

import javax.xml.ws.WebFault;


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
@WebFault(name = "PlanetsException", targetNamespace = "http://planets-project.eu/services")
public class PlanetsException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private PlanetsException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public PlanetsException_Exception(String message, PlanetsException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public PlanetsException_Exception(String message, PlanetsException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.pp.plato.services.characterisation.fpmtool.PlanetsException
     */
    public PlanetsException getFaultInfo() {
        return faultInfo;
    }

}
