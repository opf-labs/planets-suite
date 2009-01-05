package eu.planets_project.services.characterise;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;

/**
 * This is a basic migration service, with no parameters or metadata. It is not
 * clear how much of these annotations is really required to ensure
 * interoperability, but services are NOT interoperable with .NET without at
 * least some of this information.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 * @deprecated Use {@link Characterise} instead.
 */
@WebService(targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCharacteriseOneBinaryXCELtoBinary {

    /** The interface name */
    public static final String NAME = "BasicCharacteriseOneBinaryXCELtoBinary";
    /** The qualified name */
    public static final QName QNAME = new QName(PlanetsServices.NS,
            BasicCharacteriseOneBinaryXCELtoBinary.NAME);

    /**
     * @param binary
     * @param xcel a String holding the Contents of a XCEL file
     * @return an byte[] containing the created XCDL file
     */
    @WebMethod(operationName = BasicCharacteriseOneBinaryXCELtoBinary.NAME, action = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME)
    @WebResult(name = BasicCharacteriseOneBinaryXCELtoBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, partName = BasicCharacteriseOneBinaryXCELtoBinary.NAME
            + "Result")
    public byte[] basicCharacteriseOneBinaryXCELtoBinary(
            @WebParam(name = "binary", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, partName = "binary") byte[] binary,
            @WebParam(name = "XCEL_String", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, partName = "XCEL_String") String xcel);
}
