package eu.planets_project.ifr.core.common.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ServiceUtils;
	
/**
 * This is a basic migration service, with no parameters or metadata.
 * 
 * It is not clear how much of these annotations is really required to 
 * ensure interoperability, but services are NOT interoperable with .NET
 * without at least some of this information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 *
 */
@WebService(
        name = L2PlanetsService.NAME, 
        serviceName= L2PlanetsService.NAME, 
        targetNamespace = PlanetsServices.NS )
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface L2PlanetsService {
	 public static final String NAME = "L2PlanetsService";
	 public static final QName QNAME = new QName(PlanetsServices.NS, L2PlanetsService.NAME );

	 @WebMethod(
	            operationName = L2PlanetsService.NAME, 
	            action = PlanetsServices.NS + "/" + L2PlanetsService.NAME)
	 @WebResult(
	            name = L2PlanetsService.NAME+"Result", 
	            targetNamespace = PlanetsServices.NS + "/" + L2PlanetsService.NAME, 
	            partName = L2PlanetsService.NAME + "Result")
	 public String invokeService(
			 @WebParam(
             name = "xmlPDMString", 
             targetNamespace = PlanetsServices.NS + "/" + L2PlanetsService.NAME, 
             partName = "xmlPDMString")String xmlPDMString) throws PlanetsException;
	
}