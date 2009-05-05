/**
 * 
 */
package eu.planets_project.services.view;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.datatypes.Property;

/**
 * This is returned when a viewing session is polled for it's current status.
 * 
 * Any properties determined during the viewing session can be returned here,
 * or queried directly using the doAction interface.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ViewStatus {

    // Give it a suitable different name from the other Status class.
    @XmlType( name = "view-status" )
    public enum Status { ACTIVE, INACTIVE, UNKNOWN };
    
    private Status state;
    private List<Property> properties;
    
    /* for JAXB */
    protected ViewStatus() { }
    
    public ViewStatus( Status state, List<Property> properties ) {
        this.state = state;
        this.properties = properties;
    }

    /**
     * @return the state
     */
    public Status getState() {
        return state;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }
    
}
