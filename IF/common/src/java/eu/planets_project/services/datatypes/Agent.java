/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Planets agent representation.
 * <p>Instances of this class are immutable and so can be shared freely.</p>
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Agent {

	@XmlAttribute private String id;
	@XmlAttribute private String name;
	@XmlAttribute private String type;

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Agent() {}

    /**
     * @return The ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param id The agent ID
     * @param name The agent name
     * @param type The agent type
     */
    public Agent(final String id, final String name, final String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
