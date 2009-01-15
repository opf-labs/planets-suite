/**
 * 
 */
package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Andrew Jackson, Fabian Steeg
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    private String name = "";

    private String value = "";

    protected String unit = "";

    protected String description = "";

    protected String type = "";
    
    /**
     * For JAXB.
     * @deprecated Use the constructor with the two required parameters instead.
     */
    public Property() {}

    /**
     * @param name
     * @param value
     */
    public Property(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
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

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s '%s' = '%s'", this.getClass().getSimpleName(),
                name, value);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        Property that = (Property) obj;
        return this.name.equals(that.name) && this.value.equals(that.value)
                && this.unit.equals(that.unit)
                && this.description.equals(that.description)
                && this.type.equals(that.type);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        int oddPrime = 31;
        result = oddPrime * result + name.hashCode();
        result = oddPrime * result + value.hashCode();
        result = oddPrime * result + unit.hashCode();
        result = oddPrime * result + description.hashCode();
        result = oddPrime * result + type.hashCode();
        return result;
    }

}
