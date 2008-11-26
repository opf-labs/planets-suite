package eu.planets_project.services.datatypes;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatProperty extends Property {
	
	private String id;
	private String unit;
	private String description;
	private String type;
	private List<Metric> metrics;
	
	
	
	public FileFormatProperty() {
		super(null, null);
		this.id = null;
		this.unit = null;
		this.type = null;
		this.description = null;
		this.metrics = null;
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
	public List<Metric> getMetrics() {
		return metrics;
	}
	
	
	/**
	 * @param metric the metric to set
	 */
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
	
	public void setName(String name) {
		super.setName(name);
	}
	
	public String getName() {
		return super.getName();
	}
	
	public void setValue(String value) {
		super.setValue(value);
	}
	
	public String getValue() {
		return super.getValue();
	}



	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		
		String toString = "Property id: " + id + "\n" + 
				"Property name: " + super.getName() + "=" + super.getValue() + "\n" + 
				"Property description: " + description + "\n" + 
				"Property unit: " + unit + "\n" + 
				"Property type: " + type + "\n"; 
				if(metrics!=null) {
					toString = toString + "Property metrics count: " + metrics.size() + "\n\n";
				}
				else {
					toString = toString + "Property metrics count: " + 0 + "\n\n";
				}
				return toString;
				
	}
	
	
}
