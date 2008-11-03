/**
 * 
 */
package eu.planets_project.services.characterise;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * This is the generic interface for the extraction of properties.
 * 
 * Note that this should NOT be used for merely extracting embedded metadata about properties.
 * This is why the class was given such a specific name.
 * 
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(name = DetermineProperties.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface DetermineProperties extends PlanetsService {

    
    public static final String NAME = "DetermineProperties";
    
    public static final QName QNAME = new QName(PlanetsServices.NS,
            DetermineProperties.NAME);


    /**
     * This method list all the available properties for a given digital object format type.
     * 
     * @param format
     * @return A Result object containing the list of available properties.
     */
    @WebMethod(operationName = DetermineProperties.NAME, action = PlanetsServices.NS
            + "/" + DetermineProperties.NAME + "/get_measurable_properties")
    @WebResult(name = DetermineProperties.NAME + "Properties", targetNamespace = PlanetsServices.NS
            + "/" + DetermineProperties.NAME, partName = DetermineProperties.NAME
            + "Properties")
    public Properties getMeasurableProperties( 
            @WebParam(name = "format", targetNamespace = PlanetsServices.NS
                    + "/" + DetermineProperties.NAME, partName = "format") 
            URI format 
            );

    /**
     * 
     * @param digitalObject
     * @param properties
     * @return
     */
    @WebMethod(operationName = DetermineProperties.NAME, action = PlanetsServices.NS
            + "/" + DetermineProperties.NAME)
    @WebResult(name = DetermineProperties.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + DetermineProperties.NAME, partName = DetermineProperties.NAME
            + "Result")
    public DeterminePropertiesResult measure( 
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + DetermineProperties.NAME, partName = "digitalObject") 
            DigitalObject digitalObject, 
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + DetermineProperties.NAME, partName = "properties") 
            Properties properties,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + DetermineProperties.NAME, partName = "parameters") 
                Parameters parameters );

    /**
     * @return
     */
    @WebMethod(operationName = DetermineProperties.NAME + "_describe", action = PlanetsServices.NS
            + "/" + DetermineProperties.NAME + "/describe")
    @WebResult(name = DetermineProperties.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + DetermineProperties.NAME, partName = DetermineProperties.NAME
            + "Description")
    public ServiceDescription describe();
    
}
