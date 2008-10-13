/**
 * 
 */
package eu.planets_project.services.migrate;

import java.net.URI;
import java.net.URL;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;

/**
 * Migration of one digital object. Currently, this is for testing purpose only.
 * 
 * This is intended to become the generic migration interface for complex migration services.
 * 
 * ******************* NOT IN SERVICE AT PRESENT **************************
 * 
 * It should:
 *  - Support service description to facilitate discovery.
 *  - Allow multiple input formats and output formats to be dealt with be the same service.
 *  - Allow parameters to be discovered and submitted to control the migration.
 *  - Allow digital objects composed of more than one file/bitstream.
 *  - Allow Files/bitstreams passed by value OR by reference.
 *  - Provide for asynchronous invocation.
 *  
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(
        name = Migrate.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Migrate {
    /***/
    String NAME = "Migrate";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, Migrate.NAME);

    /**
     * @param digitalObject The digital object to migrate
     * @return A new digital object, the result of migrating the given digital
     *         object
     */
    @WebMethod(operationName = Migrate.NAME, action = PlanetsServices.NS
            + "/" + Migrate.NAME)
    @WebResult(name = Migrate.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Migrate.NAME, partName = Migrate.NAME
            + "Result")
    public MigrateResult migrate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "digitalObject") 
                final DigitalObject digitalObject,
            @WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "inputFormat") 
                URI inputFormat, 
            @WebParam(name = "outoutFormat", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "outputFormat") 
                URI outputFormat,
            @WebParam(name = "writeLocation", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "writeLocation") 
                URL writeLocation,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "parameters") 
                Parameters parameters );

    
    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An MigrateServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = Migrate.NAME + "_" + "describe", action = PlanetsServices.NS
            + "/" + Migrate.NAME + "/" + "describe")
    @WebResult(name = Migrate.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + Migrate.NAME, partName = Migrate.NAME
            + "Description")
    public MigrateServiceDescription describe();
    
}
