/**
 * 
 */
package eu.planets_project.services.compare;

import java.util.List;

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
import eu.planets_project.services.datatypes.Parameter;

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
     * @param first The first of the two digital objects to compare
     * @param second The second of the two digital objects to compare
     * @param config A configuration parameter list
     * @return A list of result properties, the result of comparing the given
     *         digital object, wrapped in a result object
     */
    @WebMethod(operationName = Compare.NAME, action = PlanetsServices.NS + "/"
            + Compare.NAME)
    @WebResult(name = Compare.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Compare.NAME, partName = Compare.NAME + "Result")
    @RequestWrapper(className = "eu.planets_project.services.compare."
            + Compare.NAME + "Request")
    @ResponseWrapper(className = "eu.planets_project.services.compare."
            + Compare.NAME + "Response")
    CompareResult compare(
            @WebParam(name = "first", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "firstDigitalObject") final DigitalObject first,
            @WebParam(name = "second", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "secondDigitalObject") final DigitalObject second,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "config") final List<Parameter> config);

    /**
     * Convert a tool-specific configuration file to the generic format of a
     * list of properties. Use this method to pass your configuration file to
     * {@link #compare(DigitalObject, DigitalObject, List)}.
     * @param configFile The tool-specific configuration file
     * @return A list of parameters containing the configuration values
     */
    @WebMethod(operationName = "ConfigProperties", action = PlanetsServices.NS
            + "/" + Compare.NAME)
    @WebResult(name = Compare.NAME + "ConfigProperties", targetNamespace = PlanetsServices.NS
            + "/" + Compare.NAME, partName = Compare.NAME + "ConfigProperties")
    List<Parameter> convert(
            @WebParam(name = "configFile", targetNamespace = PlanetsServices.NS
                    + "/" + Compare.NAME, partName = "configFile") final DigitalObject configFile);

}
