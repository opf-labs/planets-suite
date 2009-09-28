package eu.planets_project.tb.impl.services.util.wee;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.RegistryFactory;
import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.tb.gui.backing.exp.ExpTypeExecutablePP;

/**
 * An util object for getting instances of the WeeService, WftRegistryService and ServiceRegistry remote objects
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 24.09.2009
 *
 */
public class WeeRemoteUtil {
	
	private PlanetsLogger log = PlanetsLogger.getLogger(WeeRemoteUtil.class, "testbed-log4j.xml");
	private WftRegistryService wftRegImp;
	private WeeService weeService;
	private Registry registry;
	private static WeeRemoteUtil weeRemote= null;
	
	public static synchronized WeeRemoteUtil getInstance(){
		if(weeRemote == null)
			weeRemote = new WeeRemoteUtil();
		return weeRemote;
	}
	
	private WeeRemoteUtil(){
		this.getWeeRemoteObjects();
	}
	
	public WeeService getWeeService(){
		return weeService;
	}
	
	public WftRegistryService getWeeRegistryService(){
		return wftRegImp;
	}
	
	public Registry getServiceRegistry(){
		return registry;
	}
	
	private void getWeeRemoteObjects(){
		// get an instance of the Workflow Template Registry Service
		// and the Workflow Execution Service
		try {
			Context ctx = new javax.naming.InitialContext();
			wftRegImp = (WftRegistryService) PortableRemoteObject.narrow(ctx
					.lookup("planets-project.eu/WftRegistryService/remote"),
					WftRegistryService.class);
			weeService = (WeeService) PortableRemoteObject.narrow(ctx
					.lookup("planets-project.eu/WeeService/remote"),
					WeeService.class);
		} catch (NamingException e) {
			log.error("Could not retrieve the WeeService or WftRegistryService object"+e);
		}
	
		//get an instance of the ServiceRegistry
		registry = RegistryFactory.getRegistry();
	}

}
