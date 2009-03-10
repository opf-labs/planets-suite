package eu.planets_project.tb.api.model.ontology;

import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
/**
 * This is the Testbed's interface of an ontology property.
 * It only contains fields that are used for rendering within the application
 * all others can be requested by using it's identifier and querying the
 * OntologyPropertyHandler
 * 
 * @author <a href="mailto:Andrew.Lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 02.March.2009
 *
 */
public interface OntologyProperty {
	
	
	 	/**
	 	 * @return the properties OWL model identifier
	 	 */
	 	public String getURI();

	    /**
	     * @return a human readable label for the name
	     */
	    public String getName();


	    /**
	     * @return a human readable label for the type
	     */
	    public String getType();
	    
	    /**
	     * @return a human readable label for the datatype
	     */
	    public String getDataType();
	    
	    /**
	     * @return a human readable label for the comment
	     */
	    public String getComment();


	    /**
	     * @return the unit
	     */
	    public String getUnit();
	    
	    /**
	     * @return a human readable list of 'is_same_as' relationship of this individual, OWLNamedClasses names 
	     */
	    public List<String> getIsSameAsNames();
	    
	    /**
	     * @return a list of 'is_same_as' relationship of this individual OWLNamedClasses objects 
	     */
	    public List<OWLNamedClass> getIsSameAs();
	    
	    
	    /**
	     * Queries the OntologyProperty's individual by the RDFProperty of the String
	     * @param rdfString to build the RDFProperty with.
	     * @return
	     */
	    public Object getRDFProperty(String rdfString);
	    
	    /**
	     * The OWLIndividual element the implementing property object uses to extract its data from
	     * @return
	     */
	    public OWLIndividual getOWLIndividual();

	    
	    public boolean isUnitDefined();
	    
	    public boolean isDataTypeDefined();

}
