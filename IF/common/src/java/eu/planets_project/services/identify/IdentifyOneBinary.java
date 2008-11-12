/**
 * 
 */
package eu.planets_project.services.identify;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.ResponseWrapper;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Types;

/**
 * Identification of a single binary represented as a byte array, returning a
 * types object containing the identified Pronom URIs and the status resulting
 * from the identification.
 * 
 * @author Fabian Steeg
 */
@WebService(
        name = IdentifyOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@Deprecated
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
	byte[] binary );

    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An ServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = IdentifyOneBinary.NAME + "_describe", action = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME + "/describe")
    @WebResult(name = IdentifyOneBinary.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME, partName = IdentifyOneBinary.NAME
            + "Description")
    @ResponseWrapper(className="eu.planets_project.services.identify."+IdentifyOneBinary.NAME+"DescribeResponse")
    public ServiceDescription describe();

}
