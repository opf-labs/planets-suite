/**
 * 
 */
package eu.planets_project.services.fixity;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;

/**
 * @author CFWilson
 *
 */
@WebService(name = Fixity.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Fixity extends PlanetsService {
    /** The interface name */
    String NAME = "Fixity";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Fixity.NAME);

    /**
     * @param digitalObject The Digital Object to be identified.
     * @param parameters 
     * @return Returns a Types object containing the identification result
     */
    @WebMethod(operationName = Fixity.NAME, action = PlanetsServices.NS + "/"
            + Fixity.NAME)
    @WebResult(name = Fixity.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Fixity.NAME, partName = Fixity.NAME + "Result")
    FixityResult calculateChecksum(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Fixity.NAME, partName = "digitalObject")
            DigitalObject digitalObject,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Fixity.NAME, partName = "parameters") 
            List<Parameter> parameters
            );
}
