package eu.planets_project.ifr.core.storage.gui;

import java.util.List;

/**
 * This is the Testbed's interface of an ontology property.
 * It only contains fields that are used for rendering within the application
 * all others can be requested by using it's identifier and querying the
 * RegistryPropertyHandler
 * 
 * @author <a href="mailto:Andrew.Lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 02.March.2009
 *
 */
public interface RegistryProperty {
	
	
	 	/**
	 	 * @return the properties OWL model identifier
	 	 */
	 	public String getURI();
	 	
	 	/**
	 	 * @return a human readable label for the name
	 	 */
	 	public String getHumanReadableName();

	 	
	    /**
	     * @return the Property's name as defined by the ontology
	     */
	    public String getName();


	    /**
	     * @return a human readable label for the parent-type
	     * e.g. PDF1.3_Properties
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
	     * @return a human readable list of 'is_same_as' relationship of this individual, OWLNamedClasses names 
	     */
	    public List<String> getIsSameAsNames();
	    
	    /**
	     * @return a list of individuals that are connected via 'is_same_as' relationship. Also resolving 
	     * symmetric object property relationships.  
	     */
//	    public List<RDFIndividual> getIsSameAs();
	    
	    
	    /**
	     * Queries the RegistryProperty's individual by the RDFProperty of the String
	     * @param rdfString to build the RDFProperty with.
	     * @return
	     */
//	    public Object getRDFProperty(String rdfString);
	    
	    /**
	     * The OWLIndividual element the implementing property object uses to extract its data from
	     * @return
	     */
//	    public RDFIndividual getRDFIndividual();

	    
	    public boolean isUnitDefined();
	    
	    public boolean isDataTypeDefined();

}
