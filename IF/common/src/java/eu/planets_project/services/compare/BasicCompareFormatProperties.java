/**
 *  @author : Thomas Krämer thomas.kraemer@uni-koeln.de
 *  created : 21.07.2008
 *  
 */
package eu.planets_project.services.compare;

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
 *  Comparison of file format properties.
 *  Implementing services provide a list of commmon file format properties given two identifiers of file formats
 *  @author: Thomas Kraemer thomas.kraemer@uni-koeln.de
 *  created: 21.07.2008
 */

@WebService(name = BasicCompareFormatProperties.NAME,
//		serviceName = BasicCompareFormatProperties.NAME,
		targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCompareFormatProperties {
	public static final String NAME = "BasicCompareFormatProperties";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicCompareFormatProperties.NAME);

	/**
	 * @param twoFormatIds
	 *            A String with two file format ids such as "fmt_10:fmt_13:"
	 * @return Returns the common set of properties as a string
	 */
	@WebMethod(operationName = BasicCompareFormatProperties.NAME, action = PlanetsServices.NS
			+ "/" + BasicCompareFormatProperties.NAME)
	@WebResult(name = BasicCompareFormatProperties.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicCompareFormatProperties.NAME, partName = BasicCompareFormatProperties.NAME
			+ "Result")
	public String basicCompareFormatProperties(
			@WebParam(targetNamespace = PlanetsServices.NS + "/"
					+ BasicCompareFormatProperties.NAME)
			String twoFormatIds) throws PlanetsException;

}
