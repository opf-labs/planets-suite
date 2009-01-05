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
 * PP comparator service, comparing multiple XCDLs given as references into the
 * IF data registry, using the referenced configuration and returning a
 * reference to the result of the comparison.
 * @author Fabian Steeg
 * @deprecated Use {@link Compare} instead
 */
@WebService(name = CompareMultipleXcdlReferences.NAME, serviceName = CompareMultipleXcdlReferences.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface CompareMultipleXcdlReferences {
    /***/
    String NAME = "CompareMultipleXcdlReferences";
    /***/
    QName QNAME = new QName(PlanetsServices.NS,
            CompareMultipleXcdlReferences.NAME);

    /**
     * @param xcdls The XCDL references to compare
     * @param config The configuration to use
     * @return Returns a reference to the result of the comparison of the first
     *         and the second XCDL
     */
    @WebMethod(operationName = CompareMultipleXcdlReferences.NAME, action = PlanetsServices.NS
            + "/" + CompareMultipleXcdlReferences.NAME)
    @WebResult(name = CompareMultipleXcdlReferences.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CompareMultipleXcdlReferences.NAME, partName = CompareMultipleXcdlReferences.NAME
            + "Result")
    URI compareMultipleXcdlReferences(
            @WebParam(name = "xcdls", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlReferences.NAME) URI[] xcdls,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlReferences.NAME) URI config);

}
