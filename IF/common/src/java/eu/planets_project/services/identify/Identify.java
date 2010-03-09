/**
 * 
 */
package eu.planets_project.services.identify;

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
 * Identification of a DigitalObject, returning a types object containing the
 * identified Pronom URIs and the status resulting from the identification.
 * @author Fabian Steeg, Andrew Jackson
 */
@WebService(name = Identify.NAME, targetNamespace = PlanetsServices.NS)
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public interface Identify extends PlanetsService {

    /** The interface name */
    String NAME = "Identify";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Identify.NAME);

    /**
     * @param digitalObject The Digital Object to be identified.
     * @return Returns a Types object containing the identification result
     */
    @WebMethod(operationName = Identify.NAME, action = PlanetsServices.NS + "/"
            + Identify.NAME)
    @WebResult(name = Identify.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Identify.NAME, partName = Identify.NAME + "Result")
    IdentifyResult identify(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Identify.NAME, partName = "digitalObject")
            DigitalObject digitalObject,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Identify.NAME, partName = "parameters") 
            List<Parameter> parameters
            );
}
