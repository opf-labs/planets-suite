package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;

@WebService(name = GenericBasicMigration.NAME, serviceName = BasicMigrateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@Stateless
@Remote(BasicMigrateOneBinary.class)
@RemoteBinding(jndiBinding="planets-project.eu/GenericBasicMigrationServiceRemote")
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class GenericBasicMigration implements BasicMigrateOneBinary, Serializable
{
    private static final long serialVersionUID = -2186431821310098736L;

    public static final String NAME = "GenericBasicMigration";

	@WebMethod(operationName = BasicMigrateOneBinary.NAME,
	           action = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME)
	@WebResult(name = BasicMigrateOneBinary.NAME + "Result",
	           targetNamespace = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME,
	           partName = BasicMigrateOneBinary.NAME + "Result")
	public byte[] basicMigrateOneBinary(
	        @WebParam(name = "binary", targetNamespace = PlanetsServices.NS  + "/" + BasicMigrateOneBinary.NAME, partName = "binary")
	        byte[] binary)
	{
		return "Hello, World!".getBytes();
	}
}
