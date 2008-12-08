package eu.planets_project.ifr.core.registry.gui;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.registry.impl.ServiceInfo;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class RegistryBackingBean {
	// keeping the logger just in case
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(RegistryBackingBean.class);
	private Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
	// TODO This shouldn't be hard coded, review role info and setup before V4
	private final static String adminRole = "admin";
	private final static String providerRole = "provider";
	private List<ServiceInfo> services = new ArrayList<ServiceInfo>();

	/**
	 * Default constructor
	 */
	public RegistryBackingBean() {
		// populate the list of endpoints deployed on this server
		log.info("getting persistent registry instance");
		this.refreshServiceList();
		log.info("count of registered services->" + this.services.size());
	}
	/**
	 * @return
	 */
	public List<ServiceInfo> getServices() {
		return services;
	}
	/**
	 * @param services
	 */
	public void setServices(List<ServiceInfo> services) {
		this.services = services;
	}
	/**
	 * 
	 * @return
	 *     The number of services registered with this IF server
	 */
	public int getRegisteredCount() {
		return this.services.size();
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public String registerService(ServiceDescription description) {
		registry.register(description);
		this.refreshServiceList();
		return "success";
	}
	/**
	 * TODO: This currently relies on the two hardcoded role names defined
	 * as class statics.  Not really the best.
	 * 
	 * @return
	 *     A boolean indicating if the user can edit the service registry
	 */
    public boolean getCanRegisterServices() {
    	return (FacesContext.getCurrentInstance().getExternalContext().isUserInRole(adminRole) |
    			FacesContext.getCurrentInstance().getExternalContext().isUserInRole(providerRole));
    }

    /* ------Private Methods----- */
    private void refreshServiceList() {
    	for (ServiceDescription desc : this.registry.query(null)) {
    		this.services.add(new ServiceInfo(desc));
    	}
    }
}
