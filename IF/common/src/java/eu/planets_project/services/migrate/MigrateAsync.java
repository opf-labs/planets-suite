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
import javax.xml.ws.ResponseWrapper;


import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
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
 *  - Provide methods for asynchronous invocation.
 *  
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(
        name = MigrateAsync.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface MigrateAsync extends PlanetsService {
    /***/
    String NAME = "MigrateAsync";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, MigrateAsync.NAME);

    /*-----------------------------------------------------------------------------------
     * Asynchronous Migration:
     *-----------------------------------------------------------------------------------*/

    /**
     * @param digitalObject The digital object to migrate
     * @return A new digital object, the result of migrating the given digital
     *         object
     */
    @WebMethod(operationName = MigrateAsync.NAME, action = PlanetsServices.NS
            + "/" + MigrateAsync.NAME)
    @WebResult(name = MigrateAsync.NAME + "Ticket", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Ticket")
    public String startMigrate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "digitalObject") 
                final DigitalObject digitalObject,
            @WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "inputFormat") 
                URI inputFormat, 
            @WebParam(name = "outoutFormat", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "outputFormat") 
                URI outputFormat,
            @WebParam(name = "writeLocation", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "writeLocation") 
                URL writeLocation,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "parameters") 
                Parameters parameters );

    
    /*
     * Is there a standard implementation?  No.
     * We create a 'polling' form, not a callback, as this is simpler to implement, firewall-friendly, and will suffice for now.
     * Client Async is NOT the point. Client is like this: http://today.java.net/pub/a/today/2006/09/19/asynchronous-jax-ws-web-services.html
     * but just masks a synchronous call.
     * 
     */

    enum AsyncStatus { INVALID, IN_PROGRESS, COMPLETE }

    
    @WebMethod(operationName = MigrateAsync.NAME + "_status", action = PlanetsServices.NS
            + "/" + MigrateAsync.NAME + "/status" )
    @WebResult(name = MigrateAsync.NAME + "Status", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Status")
    public AsyncStatus pollStatus( 
            @WebParam(name = "ticket", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "ticket") 
            String ticket );
    
    @WebResult(name = MigrateAsync.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Result")
    public MigrateResult getMigrateAsyncResult( 
            @WebParam(name = "ticket", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateAsync.NAME, partName = "ticket") 
            String ticket );
    
    
    
    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An MigrateServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = MigrateAsync.NAME + "_describe", action = PlanetsServices.NS
            + "/" + MigrateAsync.NAME + "/describe")
    @WebResult(name = MigrateAsync.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + MigrateAsync.NAME, partName = MigrateAsync.NAME
            + "Description")
    @ResponseWrapper(className="eu.planets_project.services.migrate."+MigrateAsync.NAME+"DescribeResponse")
    public ServiceDescription describe();
    
}
