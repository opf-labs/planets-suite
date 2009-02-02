/**
 * 
 */
package eu.planets_project.services.compare;

import java.util.ArrayList;
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
import eu.planets_project.services.datatypes.Prop;

/**
 * Comparison of properties. NOTE: The {@link Prop} class used in this interface
 * (and therefore the interface itself, too) is work in progress. Eventually,
 * the input of this interface will be of the same class as the output of the
 * {@link Characterise} interface.
 * @see Compare
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
     * @param lists The property lists to compare
     * @param config A configuration properties list
     * @return A list of result properties, the result of comparing the given
     *         property lists, wrapped in a result object
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
            @WebParam(name = "propLists", targetNamespace = PlanetsServices.NS
            /*
             * A list is fine for the WS stack, but a list of list upsets JAXB,
             * so we specify an implementation (ArrayList):
             */
            + "/" + CompareProperties.NAME, partName = "propLists") final List<ArrayList<Prop>> lists,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "config") final List<Prop> config);

    /**
     * Convert a tool-specific input file (like the output of a characterisation
     * tool) to the generic format of a list of properties. Use this method to
     * pass your file to {@link #compare(ArrayList, ArrayList)}.
     * @param inputFile The tool-specific configuration file
     * @return A list of properties containing the configuration values
     */
    @WebMethod(operationName = "InputProperties", action = PlanetsServices.NS
            + "/" + CompareProperties.NAME)
    @WebResult(name = CompareProperties.NAME + "InputProperties", targetNamespace = PlanetsServices.NS
            + "/" + CompareProperties.NAME, partName = CompareProperties.NAME
            + "InputProperties")
    /*
     * Here, we specify ArrayList instead of List as well, because what is
     * returned here is meant to be one of the elements in the list above.
     */
    ArrayList<Prop> convertInput(
            @WebParam(name = "inputProperties", targetNamespace = PlanetsServices.NS
                    + "/" + CompareProperties.NAME, partName = "inputProperties") final DigitalObject inputFile);

}
