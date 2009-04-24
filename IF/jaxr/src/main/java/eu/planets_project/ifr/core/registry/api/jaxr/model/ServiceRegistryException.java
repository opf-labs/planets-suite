/**
 * Author: Thomas Krämer
 * Email: thomas.kraemer@uni-koeln.de
 * Created : 04.03.2008
 */
package eu.planets_project.ifr.core.registry.api.jaxr.model;

import javax.ejb.ApplicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
*  @author : Thomas Kraemer thomas.kraemer@uni-koeln.de
*  created :  27.05.2008
 */
@ApplicationException(rollback=true)
public class ServiceRegistryException extends RuntimeException{
	private static final long serialVersionUID = -4480441948962754123L;
	private static Log plogger = LogFactory.getLog(ServiceRegistryException.class.getName());


   
	/**
	 * default no arg constructor, logs a blank line
	 */
	public ServiceRegistryException() {
		super("ServiceRegistryException");
		plogger.error("");
	}
	/**
	 * construct from message and add log the message
	 * @param mesg
	 */
	public ServiceRegistryException(String mesg) {
		super("ServiceRegistryException");
		plogger.error(mesg);
		
	}
	/**
	 * construct from throwable and log the throwable's stack trace
	 * @param t
	 */
	public ServiceRegistryException(Throwable t) {
		super("ServiceRegistryException");
		plogger.error(t.getStackTrace());
		
	}

}
