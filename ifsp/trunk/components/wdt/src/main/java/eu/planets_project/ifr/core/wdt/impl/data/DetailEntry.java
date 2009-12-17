package eu.planets_project.ifr.core.wdt.impl.data;

/**
 * 
 * This a reference to a digital object properties. Representation of digital object
 * properties matches to the PREMIS standard.
 * 
 * @author Roman Graf
 *
 */
public class DetailEntry 
{
    // Entry fields
    private String name;
    private String value;
    
    /**
     * Constructor with permanent URI and digital object manager
     * 
     * @param puri
     *        This is a permanent URI of the digital object
     * @param _dom
     *        This is a digital object manager used to retrieve additional 
     *        digital object properities
     */
    public DetailEntry( String _name, String _value ) 
    {
    	name = _name;
    	value = _value;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
