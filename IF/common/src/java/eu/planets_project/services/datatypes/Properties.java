/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Properties {
    
    // The properties
    List<Property> properties = null;

    /**
     * @return the parameters
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setProperties(List<Property> parameters) {
        this.properties = parameters;
    }

    /**
     * 
     * @param name
     * @param value
     */
    public void add(URI uri, String name, String value) {
        if( this.properties == null ) this.properties = new ArrayList<Property>();
        
        Property p = new Property(uri, name, value);
        this.properties.add(p);
    }

    /**
     * 
     * @param name
     */
    public void add( URI uri, String name ) {
        this.add(uri, name, null);
    }
    
    /**
     * 
     * @param index
     * @return The Property at the given index.
     */
    public Property get( int index ) {
        return this.properties.get(index);
    }
    
    /**
     * 
     * @return The number of properties:
     */
    public int size() {
        return this.properties.size();
    }


    /**
     * Look up a specific property.
     * 
     * @param key The key of the property to look up.
     * @return The value associated with that key.
     */
    public String get(String key) {
        for( int i = 0; i < this.properties.size(); i++ ) {
            if( this.properties.get(i).getName().equals(key) )
                    return this.properties.get(i).getValue();
        }
        return null;
    }
}
