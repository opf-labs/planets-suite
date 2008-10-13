/**
 * 
 */
package eu.planets_project.services.compare;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;

/**
 * PP comparator service, comparing two XCDLs given as references into the IF
 * data registry, using the default configuration and returning a reference to
 * the result of the comparison.
 * @author Fabian Steeg
 */
@WebService(name = BasicCompareTwoXcdlReferences.NAME, serviceName = BasicCompareTwoXcdlReferences.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCompareTwoXcdlReferences {
    /***/
    String NAME = "BasicCompareTwoXcdlReferences";
    /***/
    QName QNAME = new QName(PlanetsServices.NS,
            BasicCompareTwoXcdlReferences.NAME);

    /**
     * @param xcdl1 The first XCDL reference
     * @param xcdl2 The second XCDL reference
     * @return Returns a referenceto the result of the comparison of the first
     *         and the second XCDL
     */
    @WebMethod(operationName = BasicCompareTwoXcdlReferences.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlReferences.NAME)
    @WebResult(name = BasicCompareTwoXcdlReferences.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlReferences.NAME, partName = BasicCompareTwoXcdlReferences.NAME
            + "Result")
    URI basicCompareTwoXcdlReferences(
            @WebParam(name = "xcdl1", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlReferences.NAME) URI xcdl1,
            @WebParam(name = "xcdl2", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlReferences.NAME) URI xcdl2);

}
