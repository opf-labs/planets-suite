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
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Comparison of digital objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = Compare.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Compare extends PlanetsService {
    /***/
    String NAME = "Compare";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, Compare.NAME);

    /**
     * @param objects The digital objects to compare
     * @param config A configuration file
     * @return A new digital object, the result of comparing the given digital
     *         object, wrapped in a result object
     */
    @WebMethod(operationName = Compare.NAME, action = PlanetsServices.NS + "/"
            + Compare.NAME)
    @WebResult(name = Compare.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Compare.NAME, partName = Compare.NAME + "Result")
    @RequestWrapper(className = "eu.planets_project.services.compare."
            + Compare.NAME + "Compare")
    @ResponseWrapper(className = "eu.planets_project.services.compare."
            + Compare.NAME + "CompareResponse")
    CompareResult compare(
            @WebParam(name = "digitalObjects", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "digitalObjects") final DigitalObject[] objects,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "config") final DigitalObject config);

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    @WebMethod(operationName = Compare.NAME + "_" + "describe", action = PlanetsServices.NS
            + "/" + Compare.NAME + "/" + "describe")
    @WebResult(name = Compare.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + Compare.NAME, partName = Compare.NAME + "Description")
    @ResponseWrapper(className = "eu.planets_project.services.compare."
            + Compare.NAME + "DescribeResponse")
    ServiceDescription describe();

}
