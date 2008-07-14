package eu.planets_project.ifr.core.services.validation.jhove.impl;

import java.io.Serializable;
import java.net.URI;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification;

/**
 * JHOVE validation service
 * 
 * @author Fabian Steeg
 */
@WebService(name = JhoveValidation.NAME, serviceName = BasicValidateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicValidateOneBinary.class)
@Remote(BasicValidateOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class JhoveValidation implements BasicValidateOneBinary, Serializable {
	private static final long serialVersionUID = 2127494848765937613L;
	public static final String NAME = "JhoveValidation";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicValidateOneBinary.NAME);

	@WebMethod(operationName = BasicValidateOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + BasicValidateOneBinary.NAME)
	@WebResult(name = BasicValidateOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicValidateOneBinary.NAME, partName = BasicValidateOneBinary.NAME
			+ "Result")
	public boolean basicValidateOneBinary(
			@WebParam(name = "binary", targetNamespace = PlanetsServices.NS
					+ "/" + BasicValidateOneBinary.NAME, partName = "binary")
			byte[] binary,
			@WebParam(name = "fmt", targetNamespace = PlanetsServices.NS + "/"
					+ BasicValidateOneBinary.NAME, partName = "fmt")
			URI fmt) throws PlanetsException {
		/* Identify the binary: */
		JhoveIdentification identification = new JhoveIdentification();
		Types result = identification.identifyOneBinary(binary);
		/* And check it it is what we expected: */
		for (URI uri : result.types) {
			if (uri.equals(fmt)) {
				/* One of the identified types is the one we expected: */
				return true;
			}
		}
		return false;
	}
}
