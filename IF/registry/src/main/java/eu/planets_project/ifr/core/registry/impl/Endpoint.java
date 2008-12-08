package eu.planets_project.ifr.core.registry.impl;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author CFWilson
 *
 */
public class Endpoint {
	private static Log log = LogFactory.getLog(Endpoint.class);
	// private
	// properties
	private URL location = null;
	private String name = "";
	private String category = "";
	private String Type = "";
	private String status = "Not tested";
	private boolean described = true;

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
			this.Type = pse.getServiceClass().getCanonicalName();
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
				this.described = false;
			}
		}
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
	 * @param described the described to set
	 */
	public void setDescribed(boolean described) {
		this.described = described;
	}

	/**
	 * @return the described
	 */
	public boolean isDescribed() {
		return described;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		Type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return Type;
	}
}
