/**
 * 
 */
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
import eu.planets_project.services.identify.IdentifyOneBinary;

/**
 * This is a basic migration service, with no parameters or metadata.
 * 
 * It is not clear how much of these annotations is really required to 
 * ensure interoperability, but services are NOT interoperable with .NET
 * without at least some of this information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 *
 */
@WebService(
//        name = BasicMigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicMigrateOneBinary {
    public static final String NAME = "BasicMigrateOneBinary";
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicMigrateOneBinary.NAME );

    /**
     * 
     * @param binary
     * @return
     * @throws PlanetsException 
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
