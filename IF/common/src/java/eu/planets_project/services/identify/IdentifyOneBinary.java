/**
 * 
 */
package eu.planets_project.services.identify;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Types;

/**
 * Identification of a single binary represented as a byte array, returning a
 * types object containing the identified Pronom URIs and the status resulting
 * from the identification
 * 
 * @author Fabian Steeg
 */
@WebService(name = IdentifyOneBinary.NAME, serviceName = IdentifyOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface IdentifyOneBinary {
	public static final String NAME = "IdentifyOneBinary";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			IdentifyOneBinary.NAME);

	/**
	 * @param binary
	 *            The file to identify represented as a byte array
	 * @return Returns a Types object containing the identification result
	 */
	@WebMethod(operationName = IdentifyOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + IdentifyOneBinary.NAME)
	@WebResult(name = IdentifyOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + IdentifyOneBinary.NAME, partName = IdentifyOneBinary.NAME
			+ "Result")
	public Types identifyOneBinary(
	        @WebParam(targetNamespace = PlanetsServices.NS + "/"
			+ IdentifyOneBinary.NAME)
	byte[] binary);
}
