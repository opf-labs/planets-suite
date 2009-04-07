/**
 *
 */
package eu.planets_project.services.validate;

import java.net.URI;
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
 * Validation of a DigitalObject.
 *
 * @author Fabian Steeg, Andrew Jackson, Asger Blekinge-Rasmussen
 */
@WebService(name = Validate.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Validate extends PlanetsService {

    /** The interface name */
    public static final String NAME = "Validate";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS,
            Validate.NAME);

    /**
     * @param digitalObject
     *            The Digital Object to be validated.
     * @param format
     *            The format that digital object purports to be in
     * @param parameters
     *            a list of parameters to provide fine grained tool control
     * @return Returns a ValidateResult object with the result of the validation
     */
    @WebMethod(operationName = Validate.NAME, action = PlanetsServices.NS
            + "/" + Validate.NAME)
    @WebResult(name = Validate.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Validate.NAME, partName = Validate.NAME
            + "Result")
    public ValidateResult validate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "digitalObject")
            DigitalObject digitalObject,
            @WebParam(name = "format", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "format")
            URI format,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Validate.NAME, partName = "parameters")
            List<Parameter> parameters );

}
