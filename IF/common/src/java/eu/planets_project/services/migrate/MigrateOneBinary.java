package eu.planets_project.services.migrate;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Parameter;

@WebService(name = MigrateOneBinary.NAME, 
        serviceName = MigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS)
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface MigrateOneBinary
{
	public static final String NAME = "MigrateOneBinary";
	public static final QName QNAME = new QName(PlanetsServices.NS, MigrateOneBinary.NAME);

	@WebMethod(operationName = MigrateOneBinary.NAME,
	           action = PlanetsServices.NS + "/" + MigrateOneBinary.NAME)
	@WebResult(name = MigrateOneBinary.NAME + "Result",
	           targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME,
	           partName = MigrateOneBinary.NAME + "Result")
	public MigrateOneBinaryResult migrateOneBinary(
	        @WebParam(name = "binary",
	                  targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME, partName = "binary")
	        byte[] binary,
	        @WebParam(name = "parameters",
	                  targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME, partName = "parameters")
	        Parameter[] parameters) throws PlanetsException;

}
