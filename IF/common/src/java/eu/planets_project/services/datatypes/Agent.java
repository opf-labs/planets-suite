/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
    @XmlType(namespace = PlanetsServices.OBJECTS_NS)
public class Agent {

	@XmlAttribute private String id;
    
	@XmlAttribute private String name;
    
	@XmlAttribute private String type;

    /**
     * 
     */
    public Agent() {
        super();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Agent(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
