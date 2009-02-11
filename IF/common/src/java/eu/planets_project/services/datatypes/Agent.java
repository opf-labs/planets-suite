/**
 * 
 */
package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class Agent {

	@XmlAttribute public String id;
    
	@XmlAttribute public String name;
    
	@XmlAttribute public String type;

    /**
     * 
     */
    public Agent() {
        super();
    }
    
    
    
}
