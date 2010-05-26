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
package eu.planets_project.services.compare;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Determine common properties of different file formats. Implementing services
 * provide a lists of union or intersection of common file format properties
 * given identifiers of file formats.
 * @author Thomas Kraemer thomas.kraemer@uni-koeln.de, Fabian Steeg
 *         (fabian.steeg@uni-koeln.de)
 */
@WebService(name = CommonProperties.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface CommonProperties extends PlanetsService {
    /** The interface name. */
    String NAME = "CommonProperties";
    /** The qualified name. */
    QName QNAME = new QName(PlanetsServices.NS, CommonProperties.NAME);

    /**
     * @param formatIds File format IDs (PRONOM)
     * @return Returns the intersection set of common properties of the
     *         specified file formats (in a compare result object)
     */
    @WebMethod(operationName = CommonProperties.NAME + "IntersectionName", action = PlanetsServices.NS
            + "/" + CommonProperties.NAME + "IntersectionAction")
    @WebResult(name = CommonProperties.NAME + "IntersectionResultName", targetNamespace = PlanetsServices.NS
            + "/" + CommonProperties.NAME, partName = CommonProperties.NAME
            + "IntersectionResultPart")
    CompareResult intersection(List<URI> formatIds);

    /**
     * @param formatIds File format IDs (PRONOM)
     * @return Returns the union set of common properties of the specified file
     *         formats (in a compare result object)
     */
    @WebMethod(operationName = CommonProperties.NAME + "UnionName", action = PlanetsServices.NS
            + "/" + CommonProperties.NAME + "UnionAction")
    @WebResult(name = CommonProperties.NAME + "UnionResultName", targetNamespace = PlanetsServices.NS
            + "/" + CommonProperties.NAME, partName = CommonProperties.NAME
            + "UnionResultPart")
    CompareResult union(List<URI> formatIds);

}
