package eu.planets_project.ifr.core.storage.gui;

import java.util.List;

/**
 * This is the interface of an registry property.
 *
 */
public interface RegistryProperty {
	
	
	 	/**
	 	 * @return the properties identifier
	 	 */
	 	public String getURI();
	 	
	 	/**
	 	 * @return a human readable label for the name
	 	 */
	 	public String getHumanReadableName();

	 	
	    /**
	     * @return the Property's name 
	     */
	    public String getName();


	    /**
	     * @return a human readable label for the parent-type
	     */
	    public String getParentType();
	    
	    /**
	     * @return a human readable label for the datatype
	     * e.g. string
	     */
	    public String getDataType();
	    
	    /**
	     * @return a human readable label for the comment
	     */
	    public String getComment();
	    
	    
	    /**
	     * Which type of layer does this property address
	     * does it measure information on e.g. a digital object, the service layer, etc.
	     * @return 
	     */
	    public String getType();


	    /**
	     * @return the unit
	     */
	    public String getUnit();
	    
	    /**
	     * @return a human readable list of 'is_same_as' relationship of this individual
	     */
	    public List<String> getIsSameAsNames();


	    
	    public boolean isUnitDefined();
	    
	    public boolean isDataTypeDefined();

}
