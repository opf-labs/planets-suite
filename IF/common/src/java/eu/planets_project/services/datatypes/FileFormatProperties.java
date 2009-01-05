package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A collection of @see FileFormatProperty objects
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FileFormatProperties {
	
	/**
	 * no arg default
	 */
	public FileFormatProperties() {
		this.fileFormatProperties = new ArrayList<FileFormatProperty>();
	}
	
	List <FileFormatProperty> fileFormatProperties;
	
	/**
	 * @return list of file format props
	 */
	public List<FileFormatProperty> getProperties() {
        return fileFormatProperties;
    }

    /**
     * @param propertyList the parameters to set
     */
    public void setProperties(List<FileFormatProperty> propertyList) {
        this.fileFormatProperties = propertyList;
    }
    
    /**
     * @param fileFormatProperty
     */
    public void add(FileFormatProperty fileFormatProperty) {
    	if(this.fileFormatProperties != null) {
    		this.fileFormatProperties.add(fileFormatProperty);
    	}
    	else {
    		this.fileFormatProperties = new ArrayList<FileFormatProperty>();
    		this.fileFormatProperties.add(fileFormatProperty);
    	}
    }

    /**
     * 
     * @param name
     * @param value
     */
    public void add(String name, String value) {
        if( this.fileFormatProperties == null ) this.fileFormatProperties = new ArrayList<FileFormatProperty>();
        
        FileFormatProperty p = new FileFormatProperty(name, value);
        this.fileFormatProperties.add(p);
    }

    /**
     * 
     * @param name
     */
    public void add( String name ) {
        this.add(name, null);
    }
    
    /**
     * 
     * @param index
     * @return The FileFormatProperty at the given index.
     */
    public FileFormatProperty get( int index ) {
        return this.fileFormatProperties.get(index);
    }
    
    /**
     * 
     * @return The number of fileFormatProperties:
     */
    public int size() {
        return this.fileFormatProperties.size();
    }


    /**
     * Look up the value of a specific FileFormatProperty (e.g. name, id, description, unit, type.
     * 
     * @param propertyKey The key of the FileFormatProperty to look up.
     * @return The value associated with that key.
     */
    public String getFileFormatPropertyByName(String propertyKey) {
    	
    	for( int i = 0; i < this.fileFormatProperties.size(); i++ ) {
            if( this.fileFormatProperties.get(i).getName().equals(propertyKey) )
                    return this.fileFormatProperties.get(i).getValue();
        }
        return null;
    }
}
