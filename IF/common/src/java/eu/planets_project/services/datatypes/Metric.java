/**
 * 
 */
package eu.planets_project.services.datatypes;

/**
 * @author melmsp
 *
 */
public class Metric {
	private String name;
	private String description;
	private String id;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String toString() {
		String nameOut;
		String idOut;
		String descriptionOut;
		
		if(name==null) {
			nameOut = "";
		}
		else {
			nameOut = name;
		}
		if(id==null) {
			idOut = "";
		}
		else {
			idOut = id;
		}
		
		if(description==null) {
			descriptionOut = "";
		}
		else {
			descriptionOut = description;
		}
		
		return "Metric name: " + nameOut + "\r\n" +
				"Metric id: " + idOut + "\r\n" +
				"Metric description: " + descriptionOut; 
	}
	
	
}
