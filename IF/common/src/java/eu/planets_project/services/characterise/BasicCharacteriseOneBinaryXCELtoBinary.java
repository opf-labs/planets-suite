package eu.planets_project.services.characterise;


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
        name = BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
// This is not appropriate on the Interface when using the endpointInterface approach.
// However, every concrete Service will be required to use this serviceName.
//        serviceName= BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
        targetNamespace = PlanetsServices.NS)
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCharacteriseOneBinaryXCELtoBinary {
	
    public static final String NAME = "BasicCharacteriseOneBinaryXCELtoBinary";
    public static final QName QNAME = new QName(PlanetsServices.NS, BasicCharacteriseOneBinaryXCELtoBinary.NAME );

    /**
     * 
     * @param binary
     * @param xcel a String holding the Contents of a XCEL file
     * @return an URI indicating the location of the XCDL-file in the DataRegistry
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCELtoBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = BasicCharacteriseOneBinaryXCELtoBinary.NAME + "Result")
    public byte[] basicCharacteriseOneBinaryXCELtoBinary ( 
            @WebParam(
                    name = "binary", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
                    partName = "binary")     
            byte[] binary,
            @WebParam(
                    name = "XCEL_String", 
                    targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
                    partName = "XCEL_String")
            String xcel
    ) throws PlanetsException;   
}

	
