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
 * PP comparator service, comparing two XCDL strings, using the default
 * configuration and returning a result string for the comparison.
 * @author Fabian Steeg
 */
@WebService(name = BasicCompareTwoXcdlValues.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface BasicCompareTwoXcdlValues {
    /***/
    String NAME = "BasicCompareTwoXcdlValues";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, BasicCompareTwoXcdlValues.NAME);

    /**
     * @param xcdl1 The first XCDL string
     * @param xcdl2 The second XCDL string
     * @return Returns the result of the comparison of the first and the second
     *         XCDL
     */
    @WebMethod(operationName = BasicCompareTwoXcdlValues.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlValues.NAME)
    @WebResult(name = BasicCompareTwoXcdlValues.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlValues.NAME, partName = BasicCompareTwoXcdlValues.NAME
            + "Result")
    String basicCompareTwoXcdlValues(
            @WebParam(name = "xcdl1", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlValues.NAME) String xcdl1,
            @WebParam(name = "xcdl2", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlValues.NAME) String xcdl2);

}
