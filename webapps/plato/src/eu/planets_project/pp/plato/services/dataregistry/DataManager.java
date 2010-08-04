
package eu.planets_project.pp.plato.services.dataregistry;

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
@WebService(name = "DataManager", targetNamespace = "http://planets-project.eu/ifr/core/storage/data")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface DataManager {


    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns eu.planets_project.pp.plato.services.dataregistry.StringArray
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray findFilesWithExtension(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns eu.planets_project.pp.plato.services.dataregistry.StringArray
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray findFilesWithNameContaining(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns eu.planets_project.pp.plato.services.dataregistry.StringArray
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray list(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String listDownladURI(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String read(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns byte[]
     * @throws SOAPException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public byte[] retrieveBinary(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @throws SOAPException_Exception
     */
    @WebMethod
    public void store(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1)
        throws SOAPException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @throws URISyntaxException_Exception
     * @throws RepositoryException_Exception
     * @throws LoginException_Exception
     */
    @WebMethod
    public void storeBinary(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        byte[] arg1)
        throws LoginException_Exception, RepositoryException_Exception, URISyntaxException_Exception
    ;

}
