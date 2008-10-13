/**
 * 
 */
package eu.planets_project.services.validate;

import java.net.URI;

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
 *
 */
@WebService(
        name = BasicValidateOneBinary.NAME, 
        serviceName= BasicValidateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicValidateOneBinary {
    public static final String NAME = "BasicValidateOneBinary";
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicValidateOneBinary.NAME );

    /**
     * 
     * @param binary
     * @param PLANETS URI 
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
