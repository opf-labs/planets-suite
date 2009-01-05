package eu.planets_project.services.characterise;


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
        name = BasicCharacteriseOneBinaryXCELtoURI.NAME, 
// This is not appropriate on the Interface when using the endpointInterface approach.
// However, every concrete Service will be required to use this serviceName.
//        serviceName= BasicCharacteriseOneBinaryXCELtoURI.NAME, 
        targetNamespace = PlanetsServices.NS)
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCharacteriseOneBinaryXCELtoURI {
	
    /** The interface name */
    public static final String NAME = "BasicCharacteriseOneBinaryXCELtoURI";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicCharacteriseOneBinaryXCELtoURI.NAME );

    /**
     * 
     * @param inputImageURI 
     * @param inputXcelURI 
     * @return an URI indicating the location of the XCDL-file in the DataRegistry
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCELtoURI.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCELtoURI.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, 
            partName = BasicCharacteriseOneBinaryXCELtoURI.NAME + "Result")
    public URI basicCharacteriseOneBinaryXCELtoURI ( 
            @WebParam(
                    name = "input_image_URI", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, 
                    partName = "input_image_URI")     
            URI inputImageURI,
            @WebParam(
                    name = "input_xcel_URI", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, 
                    partName = "input_xcel_URI")
            URI inputXcelURI
    ) throws PlanetsException;    
    
}

	
