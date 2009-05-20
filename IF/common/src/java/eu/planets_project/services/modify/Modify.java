/**
 * 
 */
package eu.planets_project.services.modify;

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

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;

/**
 * @author melmsp
 *
 */
@WebService(
        name = Modify.NAME, 
        targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Modify extends PlanetsService {
	 /** The interface name */
    String NAME = "Modify";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Modify.NAME);
    
    @WebMethod(operationName = Modify.NAME, action = PlanetsServices.NS
            + "/" + Modify.NAME)
    @WebResult(name = Modify.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Modify.NAME, partName = Modify.NAME
            + "Result")
    @RequestWrapper(className="eu.planets_project.services.modify." + Modify.NAME + "Modify")
    @ResponseWrapper(className="eu.planets_project.services.modify." + Modify.NAME + "ModifyResponse")
    public ModifyResult modify(
    		@WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
    				+ "/" + Modify.NAME, partName = "digitalObject") 
    				final DigitalObject digitalObject,
    		@WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
    				+ "/" + Modify.NAME, partName = "inputFormat")
    				final URI inputFormat,
    		@WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
    	            + "/" + Modify.NAME, partName = "parameters") 
    	            List<Parameter> parameters );

}
