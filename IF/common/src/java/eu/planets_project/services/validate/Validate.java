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

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Validation of a DigitalObject.
 * 
 * @author Fabian Steeg, Andrew Jackson.
 */
@WebService(name = Validate.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Validate {
    
	public static final String NAME = "Validate";
	
	public static final QName QNAME = new QName(PlanetsServices.NS,
			Validate.NAME);

	/**
	 * @param digitalObject 
	 *            The Digital Object to be identified.
	 * @return Returns a Types object containing the identification result
	 */
	@WebMethod(operationName = Validate.NAME, action = PlanetsServices.NS
			+ "/" + Validate.NAME)
	@WebResult(name = Validate.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + Validate.NAME, partName = Validate.NAME
			+ "Result")
	public ValidateResult identify(
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
	public ServiceDescription describe();
}
