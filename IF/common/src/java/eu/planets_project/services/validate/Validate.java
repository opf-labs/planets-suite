/**
 * 
 */
package eu.planets_project.services.validate;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.ResponseWrapper;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Validation of a DigitalObject.
 * 
 * @author Fabian Steeg, Andrew Jackson.
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
			URI format );
	
    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An ServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = Validate.NAME + "_describe", action = PlanetsServices.NS
            + "/" + Validate.NAME + "/describe")
    @WebResult(name = Validate.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + Validate.NAME, partName = Validate.NAME
            + "Description")
    @ResponseWrapper(className="eu.planets_project.services.validate."+Validate.NAME+"DescribeResponse")
	public ServiceDescription describe();
}
