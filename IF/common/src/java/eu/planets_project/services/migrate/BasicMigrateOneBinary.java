/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;

/**
 * This is a basic migration service, with no parameters or metadata.
 * 
 * It is not clear how much of these annotations is really required to 
 * ensure interoperability, but services are NOT interoperable with .NET
 * without at least some of this information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 * @deprecated Use {@link Migrate} instead.
 */
@WebService(
        name = BasicMigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@Deprecated
public interface BasicMigrateOneBinary {
    /** The interface name */
    public static final String NAME = "BasicMigrateOneBinary";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicMigrateOneBinary.NAME );

    /**
     * 
     * @param binary
     * @return the migrated binary in new format
     */
    @WebMethod(
            operationName = BasicMigrateOneBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME)
    @WebResult(
            name = BasicMigrateOneBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME, 
            partName = BasicMigrateOneBinary.NAME + "Result")
    public byte[] basicMigrateOneBinary ( 
            @WebParam(
                    name = "binary", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME, 
                    partName = "binary")
            byte[] binary );    
}
