/**
 * 
 */
package eu.planets_project.services.view;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;

/**
 * This is returned when a viewing session is polled for it's current status.
 * 
 * Any properties determined during the viewing session can be returned here.
 * 
 * FIXME Needs to distinguish between live sessions, live sessions that are still measuring properties, and dead sessions.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ViewStatus {
    
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    
    private int state = -1;
    private List<Property> properties;
    
    /* for JAXB */
    protected ViewStatus() { }
    
    public ViewStatus( int state, List<Property> properties ) {
        this.state = state;
        this.properties = properties;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }
    
}
