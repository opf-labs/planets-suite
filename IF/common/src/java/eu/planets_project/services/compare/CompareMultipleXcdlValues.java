/**
 * 
 */
package eu.planets_project.services.compare;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;

/**
 * PP comparator service, comparing multiple XCDL strings, using the given
 * configuration string and returning a result string for the comparison.
 * @author Fabian Steeg
 * @deprecated Use {@link Compare} instead
 */
@WebService(name = CompareMultipleXcdlValues.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface CompareMultipleXcdlValues {
    /***/
    String NAME = "CompareMultipleXcdlValues";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, CompareMultipleXcdlValues.NAME);

    /**
     * @param xcdls The XCDL strings to compare
     * @param config The configuration to use
     * @return Returns the result of the comparison of the first and the second
     *         XCDL
     */
    @WebMethod(operationName = CompareMultipleXcdlValues.NAME, action = PlanetsServices.NS
            + "/" + CompareMultipleXcdlValues.NAME)
    @WebResult(name = CompareMultipleXcdlValues.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CompareMultipleXcdlValues.NAME, partName = CompareMultipleXcdlValues.NAME
            + "Result")
    String compareMultipleXcdlValues(
            @WebParam(name = "xcdls", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlValues.NAME) String[] xcdls,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlValues.NAME) String config);

}
