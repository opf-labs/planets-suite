
package eu.planets_project.pp.plato.services.dataregistry;

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
@WebFault(name = "LoginException", targetNamespace = "http://planets-project.eu/ifr/core/storage/data")
public class LoginException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private LoginException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public LoginException_Exception(String message, LoginException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public LoginException_Exception(String message, LoginException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.pp.plato.services.dataregistry.LoginException
     */
    public LoginException getFaultInfo() {
        return faultInfo;
    }

}
