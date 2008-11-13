/**
 * Just for testing Web services
 */
package eu.planets_project.ifr.core.wdt.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;

/**
 * @author Rainer Schmidt
 *
 */
@WebService(
	targetNamespace = PlanetsServices.NS )
	@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true"
)
public interface Monitor {
	public static final String NAME = "Monitor";
  public static final QName QNAME = new QName(PlanetsServices.NS, Monitor.NAME );

	@WebMethod(
		operationName = Monitor.NAME, 
    action = PlanetsServices.NS + "/" + Monitor.NAME
  )
  @WebResult(
  	name = Monitor.NAME+"Result", 
    targetNamespace = PlanetsServices.NS + "/" + Monitor.NAME, 
    partName = Monitor.NAME + "Result"
  )
	public BeanObject monitor(
		@WebParam(
    	name = "obj", 
      targetNamespace = PlanetsServices.NS + "/" + Monitor.NAME, 
      partName = "obj"
    )
		BeanObject obj
	);
}
