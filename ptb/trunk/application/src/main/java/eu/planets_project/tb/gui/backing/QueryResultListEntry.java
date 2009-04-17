package eu.planets_project.tb.gui.backing;

import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Simple wrapper POJO that associates a DigitalObject with a (display)name.
 * 
 * @author SimonR
 *
 */
public class QueryResultListEntry {
	
	private DigitalObject dob;
	
	private String name;
	
	private String url;
	
	private String sizeInKB;
	
	private String format;
	
	public boolean selected = false;
	
	public QueryResultListEntry(DigitalObject dob, String name, String url, String sizeInKB, String format) {
		this.dob = dob;
		this.name = name;
		this.url = url;
		this.sizeInKB = sizeInKB;
		this.format = format;
	}
	
	public DigitalObject getDigitalObject() {
		return dob;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSize() {
		return sizeInKB;
	}
	
	public String getFormat() {
		return format;
	}
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
