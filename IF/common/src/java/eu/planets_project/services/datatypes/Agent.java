/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Planets agent representation.
 * <p>
 * Instances of this class are immutable and so can be shared freely.
 * </p>
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Agent {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;

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

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Agent other = (Agent) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() 
    {
        return String.format(
        		"Agent: id '%s', name '%s', type '%s';"
        		, id, name, type);
    }    
}
