/**
 * 
 */
package eu.planets_project.services.validate;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;

/**
 * The Basic Validate interface.
 * @deprecated Use {@link Validate} instead.
 */
@WebService(
        name = BasicValidateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@Deprecated
public interface BasicValidateOneBinary {
    /** The interface name */
    public static final String NAME = "BasicValidateOneBinary";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicValidateOneBinary.NAME );

    /**
     * 
     * @param binary
     * @param fmt 
     * @return boolean.
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicValidateOneBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME)
    @WebResult(
            name = BasicValidateOneBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME, 
            partName = BasicValidateOneBinary.NAME + "Result")
    public boolean basicValidateOneBinary ( 
            @WebParam(
                    name = "binary", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME, 
                    partName = "binary")
            byte[] binary,
            @WebParam(
                    name = "fmt", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME, 
                    partName = "fmt")
            URI fmt ) throws PlanetsException;    
}
