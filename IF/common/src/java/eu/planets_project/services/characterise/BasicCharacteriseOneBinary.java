package eu.planets_project.services.characterise;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;

/**
 * The most basic characterization interface yet: takes a binary and returns a
 * string characterizing the binary (in some unspecified way, i.e. not an XCDL),
 * e.g. to be used by the service wrapping the metadata extraction tool of the
 * national library of New Zealand
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = BasicCharacteriseOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCharacteriseOneBinary {

	public static final String NAME = "BasicCharacteriseOneBinary";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicCharacteriseOneBinary.NAME);

	@WebMethod(operationName = BasicCharacteriseOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + BasicCharacteriseOneBinary.NAME)
	@WebResult(name = BasicCharacteriseOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicCharacteriseOneBinary.NAME, partName = BasicCharacteriseOneBinary.NAME
			+ "Result")
	public String basicCharacteriseOneBinary(
			@WebParam(name = "binary", targetNamespace = PlanetsServices.NS
					+ "/" + BasicCharacteriseOneBinary.NAME, partName = "binary")
			byte[] binary) throws PlanetsException;

}
