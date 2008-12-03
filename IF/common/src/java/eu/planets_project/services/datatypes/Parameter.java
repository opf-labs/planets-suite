/**
 * 
 */
package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This wraps the concept of a service parameter.  When retrieved from a service, the default values should be set.
 * 
 * This form does not allow optional v. required parameters, as ALL parameters should be explicitly specified.
 * An 'optional' parameter implies an implicit default that would end up not being recorded in the audit trail.
 * 
 * @author AnJackson
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
    
    /**
     * A name for the parameter.  Must be uniquely meaningful to the service, but is not expected to carry any meaning outwith the service.
     */
    public String name;

    /**
     * The value for this parameter.  Should be set to the default by the service when parameter discovery is happening.
     */
    public String value;
    
    /**
     * This is a String to hold the type, which should map to the xsd types and should be assumed to be a String if empty or null.
     * In the future, we might add limits/validation?  XSD-style?
     */
    public String type;
    
    /**
     * the description of this parameter/value pair. Might be used to give further
     * information on the possible values and their meaning.
     */
    public String description;

    /* ------------------------------------------------------------------------- */
    
    

	/**
     * 
     */
    public Parameter() {
    }
    

    /**
     * @param name
     * @param value
     * @param type
     * @param description the description of this parameter/value pair. Might be used to give further
     *                    information on the possible values and their meaning.
     */
    
    /**
     * @param name
     * @param value
     * @param type
     */
    public Parameter(String name, String value, String type) {
        super();
        this.name = name;
        this.value = value;
        this.type = type;
    }

    /**
     * @param name
     * @param value
     */
    public Parameter(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /* ------------------------------------------------------------------------- */
    
    /**
     * @return the value
     */
    protected String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    protected void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the name
     */
    protected String getName() {
        return name;
    }

    /**
     * @return the type
     */
    protected String getType() {
        return type;
    }
    
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		String toPrint = this.name + " = " + this.value;
		return toPrint;
	}
    
    
    
}
