/**
 * 
 */
package eu.planets_project.services.identify;

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
 * This is a basic identification service, with no parameters or metadata.
 * 
 * It is not clear how much of these annotations is really required to 
 * ensure interoperability, but services are NOT interoperable with .NET
 * without at least some of this information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 *
 */
@WebService(
        name = BasicIdentifyOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@Deprecated
public interface BasicIdentifyOneBinary {
    public static final String NAME = "BasicIdentifyOneBinary";
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicIdentifyOneBinary.NAME );

    /**
     * 
     * @param binary
     * @return PLANETS URI showing the image format.
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicIdentifyOneBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME)
    @WebResult(
            name = BasicIdentifyOneBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME, 
            partName = BasicIdentifyOneBinary.NAME + "Result")
    public URI basicIdentifyOneBinary ( 
            @WebParam(
                    name = "binary", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME, 
                    partName = "binary")
            byte[] binary ) throws PlanetsException;    
}
