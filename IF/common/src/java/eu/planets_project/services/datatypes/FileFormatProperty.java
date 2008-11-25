package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatProperty extends Property {
	
	private String id;
	private String unit;
	private String description;
	private Metric metric;
	
	
	
	public FileFormatProperty() {
		this.id = null;
		this.unit = null;
		this.description = null;
		this.metric = null;
	}
	
	
	
	public FileFormatProperty (String id, String unit, String description, Metric metric) {
		this.id = id;
		this.unit = unit;
		this.description = description;
		this.metric = metric;
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
	
	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	
	
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
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
	 * @return the metric
	 */
	public Metric getMetric() {
		return metric;
	}
	
	
	/**
	 * @param metric the metric to set
	 */
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	
}
