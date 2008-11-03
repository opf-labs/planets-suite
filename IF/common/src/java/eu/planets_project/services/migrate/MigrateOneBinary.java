package eu.planets_project.services.migrate;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;

@WebService(name = MigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface MigrateOneBinary extends PlanetsService
{
	public static final String NAME = "MigrateOneBinary";
	public static final QName QNAME = new QName(PlanetsServices.NS, MigrateOneBinary.NAME);

	@WebMethod(operationName = MigrateOneBinary.NAME,
	           action = PlanetsServices.NS + "/" + MigrateOneBinary.NAME)
	@WebResult(name = MigrateOneBinary.NAME + "Result",
	           targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME,
	           partName = MigrateOneBinary.NAME + "Result")
	public MigrateOneBinaryResult migrate(
	        @WebParam(name = "binary",
	                  targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME, partName = "binary")
	        byte[] binary,
            @WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateOneBinary.NAME, partName = "inputFormat") 
                URI inputFormat, 
            @WebParam(name = "outoutFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateOneBinary.NAME, partName = "outputFormat") 
                URI outputFormat,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateOneBinary.NAME, partName = "parameters") 
                Parameters parameters );

    
    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An MigrateServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = MigrateOneBinary.NAME + "_" + "describe", action = PlanetsServices.NS
            + "/" + MigrateOneBinary.NAME + "/" + "describe")
    @WebResult(name = MigrateOneBinary.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + MigrateOneBinary.NAME, partName = MigrateOneBinary.NAME
            + "Description")
    public ServiceDescription describe();
    

}
