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
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;

/**
 * Comparison of property lists (the output of the {@link Characterise}
 * interface).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = CompareProperties.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface CompareProperties extends PlanetsService {
    /***/
    String NAME = "CompareProperties";
    /***/
    QName QNAME = new QName(PlanetsServices.NS, CompareProperties.NAME);

    /**
     * @param first The first of the two property lists to compare
     * @param second The second of the two property lists to compare
     * @param config A configuration parameter list
     * @return A list of result properties, the result of comparing the given
     *         digital object, wrapped in a result object
     */
    @WebMethod(operationName = CompareProperties.NAME, action = PlanetsServices.NS
            + "/" + CompareProperties.NAME)
    @WebResult(name = CompareProperties.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CompareProperties.NAME, partName = CompareProperties.NAME
            + "Result")
    @RequestWrapper(className = "eu.planets_project.services.compare."
            + CompareProperties.NAME + "Request")
    @ResponseWrapper(className = "eu.planets_project.services.compare."
            + CompareProperties.NAME + "Response")
    CompareResult compare(
            @WebParam(name = "first", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "firstPropertyList") final List<Property> first,
            @WebParam(name = "second", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "secondPropertyList") final List<Property> second,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "config") final List<Parameter> config);

    /**
     * Convert a tool-specific input file (like the output of a characterisation
     * tool) to the generic format the service. Use this method to pass as the
     * first two arguments to {@link #compare(List, List, List)}.
     * @param inputFile The tool-specific configuration file
     * @return A list of properties representing the given input file
     */
    @WebMethod(operationName = "InputProperties", action = PlanetsServices.NS
            + "/" + CompareProperties.NAME)
    @WebResult(name = CompareProperties.NAME + "InputProperties", targetNamespace = PlanetsServices.NS
            + "/" + CompareProperties.NAME, partName = CompareProperties.NAME
            + "InputProperties")
    List<Property> convertInput(
            @WebParam(name = "inputProperties", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "inputProperties") final DigitalObject inputFile);

    /**
     * Convert a tool-specific configuration file to the generic format of a
     * list of properties. Use this method to pass your configuration file as
     * the last argument to {@link #compare(List, List, List)}.
     * @param configFile The tool-specific configuration file
     * @return A list of parameters containing the configuration values
     */
    @WebMethod(operationName = CompareProperties.NAME + "ConfigProperties", action = PlanetsServices.NS
            + "/" + CompareProperties.NAME)
    @WebResult(name = CompareProperties.NAME + "ConfigProperties", targetNamespace = PlanetsServices.NS
            + "/" + CompareProperties.NAME, partName = CompareProperties.NAME
            + "ConfigProperties")
    List<Parameter> convertConfig(
            @WebParam(name = "configFile", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "configFile") final DigitalObject configFile);

}
