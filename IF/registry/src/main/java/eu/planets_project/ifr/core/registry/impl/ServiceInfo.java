package eu.planets_project.ifr.core.registry.impl;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Helper class for the Service Registry, it stores the registered service information in
 * a web GUI friendly form
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class ServiceInfo {
	private ServiceDescription description;
	private String category;
	
	/**
	 * 
	 * @param description
	 */
	public ServiceInfo(ServiceDescription description) {
		this.setDescription(description);
		// Now let's make the category a little more friendly
		if (description.getType().indexOf('.') > -1)
		{
			this.category = description.getType().substring(description.getType().lastIndexOf('.') + 1);
		} else this.category = description.getType();
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
	 * @param description the description to set
	 */
	public void setDescription(ServiceDescription description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public ServiceDescription getDescription() {
		return description;
	}
}
