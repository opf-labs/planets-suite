package eu.planets_project.services.characterise;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;


/**
 * Characterisation of one digital object.
 * 
 * This is intended to become the generic characterisation interface for characterisation tools
 * like JHove and Extractor.
 * 
 * It should:
 *  - cover (at least) those two characterisation tools under one interface
 *  - Support service description to facilitate discovery.
 *  - Allow parameters to be discovered and submitted to control the underlying tools (if needed).
 *  - Allow Files/bitstreams passed by value OR by reference.
 *  
 * @author Peter Melms (peter.melms@uni-koeln.de), Andrew Jackson <Andrew.Jackson@bl.uk>
 */

@WebService(
        name = Characterise.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")

public interface Characterise {
	
	    /***/
	    String NAME = "Characterise";
	    /***/
	    QName QNAME = new QName(PlanetsServices.NS, Characterise.NAME);

	    /**
	     * @param digitalObject The digital object to characterise
	     * @return A new digital object, the result of characterising the given digital
	     *         object
	     */
	    @WebMethod(operationName = Characterise.NAME, action = PlanetsServices.NS
	            + "/" + Characterise.NAME)
	    @WebResult(name = Characterise.NAME + "Result", targetNamespace = PlanetsServices.NS
	            + "/" + Characterise.NAME, partName = Characterise.NAME
	            + "Result")
	    @RequestWrapper(className="eu.planets_project.services.characterise."+Characterise.NAME+"Characterise")
	    @ResponseWrapper(className="eu.planets_project.services.characterise."+Characterise.NAME+"CharacteriseResponse")
	    public CharacteriseResult characterise(
	            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
	                    + "/" + Characterise.NAME, partName = "digitalObject") 
	                final DigitalObject digitalObject,
	            @WebParam(name = "optional_format_XCEL", targetNamespace = PlanetsServices.NS
	                    + "/" + Characterise.NAME, partName = "optional_format_XCEL") 
	                String optionalFormatXCEL, 
	            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
	                    + "/" + Characterise.NAME, partName = "parameters") 
	                Parameters parameters );

	    
	    
	    /**
	     * A method that can be used to recover a rich service description, and thus populate a service registry.
	     * @return An MigrateServiceDescription object that describes this service, to aid service discovery.
	     */
	    @WebMethod(operationName = Characterise.NAME + "_" + "describe", action = PlanetsServices.NS
	            + "/" + Characterise.NAME + "/" + "describe")
	    @WebResult(name = Characterise.NAME + "Description", targetNamespace = PlanetsServices.NS
	            + "/" + Characterise.NAME, partName = Characterise.NAME
	            + "Description")
	    @ResponseWrapper(className="eu.planets_project.services.characterise."+Characterise.NAME+"DescribeResponse")
	    public ServiceDescription describe();
	    
	    
	    
	    @WebMethod(operationName = Characterise.NAME + "_" + "listProperties", action = PlanetsServices.NS
	            + "/" + Characterise.NAME + "/" + "listProperties")
	    @WebResult(name = Characterise.NAME + "Property_List", targetNamespace = PlanetsServices.NS
	            + "/" + Characterise.NAME, partName = Characterise.NAME
	            + "Property_List")
	    @ResponseWrapper(className="eu.planets_project.services.characterise."+Characterise.NAME+"listPropertiesResponse")
	    public List<FileFormatProperty> listProperties(URI formatURI);
}
