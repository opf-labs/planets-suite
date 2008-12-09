package eu.planets_project.ifr.core.registry.impl;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author CFWilson
 *
 */
public class Endpoint {
	private static Log log = LogFactory.getLog(Endpoint.class);
	
	// This is a bodge to work around the
	private static final String notRegGraphic = "/images/notreg.gif"; 
	private static final String isRegGraphic = "/images/reg.gif"; 
	// private
	// properties
	private URL location = null;
	private String name = "";
	private String category = "";
	private String type = "";
	private String status = "Not tested";
	private boolean depracated = false;
	private boolean registered = false;
	private String regGraphic = notRegGraphic;
	/**
	 * We don't want Endpoints without URLs so disable no arg 
	 */
	@SuppressWarnings("unused")
	private Endpoint() {
	}
	
	/**
	 * 
	 * @param pse
	 */
	public Endpoint(PlanetsServiceExplorer pse) {
		this.location = pse.getWsdlLocation();
		// TODO name is just set from path, some extra work needed
		this.name = this.location.getPath();
		if (this.name.lastIndexOf('/') >= 0) {
			this.name = name.substring(this.name.lastIndexOf('/') + 1);
		}
		if (null != pse.getServiceClass()) {
			this.type = pse.getServiceClass().getCanonicalName();
			if (pse.getServiceClass().getCanonicalName().indexOf('.') >= 0) {
				this.setCategory(pse.getServiceClass().getCanonicalName().substring(pse.getServiceClass().getCanonicalName().lastIndexOf('.') + 1));
			}
			else {
				this.setCategory(pse.getServiceClass().getCanonicalName());
			}
			try {
				if (pse.isServiceInstanciable()) this.status = "OK";
				else this.status = "Failed";
			} catch (RuntimeException e) {
				this.status = "Unknown";
				this.depracated = true;
			}
		}
	}
	
	/**
	 * @param desc 
	 * 
	 */
	public Endpoint(ServiceDescription desc) {
		this.location = desc.getEndpoint();
		this.name = desc.getName();
		this.setCategory(desc.getType().substring(desc.getType().lastIndexOf('.') + 1));
		this.type = desc.getType();
		this.status = "OK";
		this.depracated = false;
		this.registered = true;
		this.regGraphic = isRegGraphic;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(URL location) {
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public URL getLocation() {
		return location;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param depracated the described to set
	 */
	public void setDepracated(boolean depracated) {
		this.depracated = depracated;
	}

	/**
	 * @return the described
	 */
	public boolean isDepracated() {
		return depracated;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param registered the registered to set
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
		if (this.registered) this.regGraphic = Endpoint.isRegGraphic;
		else this.regGraphic = Endpoint.notRegGraphic;
	}

	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * @param regGraphic the regGraphic to set
	 */
	public void setRegGraphic(String regGraphic) {
		this.regGraphic = regGraphic;
	}

	/**
	 * @return the regGraphic
	 */
	public String getRegGraphic() {
		return regGraphic;
	}
}
