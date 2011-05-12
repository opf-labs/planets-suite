/**
 * Just for testing Web services
 */
package eu.planets_project.ifr.core.wdt.impl;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import eu.planets_project.ifr.core.wdt.api.BeanObject;
import eu.planets_project.ifr.core.wdt.api.Monitor;
import eu.planets_project.services.PlanetsServices;

/**
 * @author Rainer Schmidt
 *
 */

@javax.jws.WebService(
	name=MonitorImpl.NAME, 
	serviceName=Monitor.NAME,
	targetNamespace=PlanetsServices.NS,
	endpointInterface = "eu.planets_project.ifr.core.wdt.api.Monitor" )
@Stateless(mappedName="planets/LocalWdtMonitor")
@Local(Monitor.class)
@Remote(Monitor.class)
//@LocalBinding(jndiBinding="planets/LocalWdtMonitor")
//@RemoteBinding(jndiBinding="planets-project.eu/LocalWdtMonitor")
//@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
//@javax.jws.soap.SOAPBinding(style = SOAPBinding.Style.RPC)
public class MonitorImpl implements Monitor {

	static final String NAME = "MonitorImpl";
	//@javax.jws.WebMethod()
	//@javax.jws.WebResult(name="outBean")
	public BeanObject monitor(
		/*@javax.jws.WebParam(name="inBean")*/
		BeanObject obj) {
			
		long id = obj.getId();
		id = id + 1L;
		obj.setId(id);
		return obj;
	}	
}
