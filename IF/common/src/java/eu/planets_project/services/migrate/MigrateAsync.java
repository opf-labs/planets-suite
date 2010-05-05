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
package eu.planets_project.services.migrate;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * This is intended to become the generic migration interface for complex migration services.
 * 
 * ******************* NOT IN SERVICE AT PRESENT **************************
 * 
 * It should:
 *  - Support service description to facilitate discovery.
 *  - Allow multiple input formats and output formats to be dealt with be the same service.
 *  - Allow parameters to be discovered and submitted to control the migration.
 *  - Allow digital objects composed of more than one file/bitstream.
 *  - Allow Files/bitstreams passed by value OR by reference.
 *  - Provide methods for asynchronous invocation.
 *  
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(
        name = MigrateAsync.NAME, 
        targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface MigrateAsync extends PlanetsService {
    /** The interface name */
    String NAME = "MigrateAsync";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, MigrateAsync.NAME);

    /*-----------------------------------------------------------------------------------
     * Asynchronous Migration:
     *-----------------------------------------------------------------------------------*/

    /**
     * @param digitalObject The digital object to migrate
     * @param inputFormat Format URI for the digital object path (migrate from)
     * @param outputFormat Format URI for the desired ouput (migrate to)
     * @param writeLocation a location (URL) to write the result to
     * @param parameters extra parameters to control particular tools
     * @return A new digital object, the result of migrating the given digital
     *         object
     */
    @WebMethod(operationName = MigrateAsync.NAME, action = PlanetsServices.NS
            + "/" + MigrateAsync.NAME)
    @WebResult(name = MigrateAsync.NAME + "Ticket", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Ticket")
    public String startMigrate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "digitalObject") 
                final DigitalObject digitalObject,
            @WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "inputFormat") 
                URI inputFormat, 
            @WebParam(name = "outoutFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "outputFormat") 
                URI outputFormat,
            @WebParam(name = "writeLocation", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "writeLocation") 
                URL writeLocation,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "parameters") 
                List<Parameter> parameters );

    
    /*
     * Is there a standard implementation?  No.
     * We create a 'polling' form, not a callback, as this is simpler to implement, firewall-friendly, and will suffice for now.
     * Client Async is NOT the point. Client is like this: http://today.java.net/pub/a/today/2006/09/19/asynchronous-jax-ws-web-services.html
     * but just masks a synchronous call.
     * 
     */
    /**
     * Enum to track the asynch process status
     */
    enum AsyncStatus { INVALID, IN_PROGRESS, COMPLETE }

    
    /**
     * @param ticket The ticket
     * @return the current status enum
     */
    @WebMethod(operationName = MigrateAsync.NAME + "_status", action = PlanetsServices.NS
            + "/" + MigrateAsync.NAME + "/status" )
    @WebResult(name = MigrateAsync.NAME + "Status", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Status")
    public AsyncStatus pollStatus( 
            @WebParam(name = "ticket", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "ticket") 
            String ticket );
    
    /**
     * @param ticket The ticket
     * @return the completed job MigrateResult
     */
    @WebResult(name = MigrateAsync.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Result")
    public MigrateResult getMigrateAsyncResult( 
            @WebParam(name = "ticket", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "ticket") 
            String ticket );
}
