/**
 * Author: Thomas Krämer
 * Email: thomas.kraemer@uni-koeln.de
 * Created : 04.03.2008
 */
package eu.planets_project.ifr.core.registry.api.model;

import javax.ejb.ApplicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
*  @author : Thomas Kraemer thomas.kraemer@uni-koeln.de
*  created :  27.05.2008
 */
@ApplicationException(rollback=true)
public class ServiceRegistryException extends Throwable{
	private static final long serialVersionUID = -4480441948962754123L;
	private static Log plogger = LogFactory.getLog(ServiceRegistryException.class.getName());


   
	public ServiceRegistryException() {
		super("ServiceRegistryException");
		plogger.error("");
	}
	public ServiceRegistryException(String mesg) {
		super("ServiceRegistryException");
		plogger.error(mesg);
		
	}
	public ServiceRegistryException(Throwable t) {
		super("ServiceRegistryException");
		plogger.error(t.getStackTrace());
		
	}

}
