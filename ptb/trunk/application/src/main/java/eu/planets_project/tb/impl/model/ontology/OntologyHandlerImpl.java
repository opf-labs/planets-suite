/**
 * 
 */
package eu.planets_project.tb.impl.model.ontology;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;

/**
 * Responsible for loading the underlying ontology(s)
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 09.03.2009
 *
 */
public class OntologyHandlerImpl {
	
	private Log log = LogFactory.getLog(OntologyHandlerImpl.class);
	private final String owlresource = "eu/planets_project/tb/impl/XCLOntology1.5.owl";
	private OWLModel owlModel;
	private static OntologyHandlerImpl instance;
	public static final String OWLMODEL_ROOT_CLASS = "XCLOntology1:specificationPropertyNames";
	
	private OntologyHandlerImpl(){
		this.loadModel();
	}
	
	public static synchronized OntologyHandlerImpl getInstance(){
		if (instance == null){
			instance = new OntologyHandlerImpl();
		}
		return instance;
	}
	
	/**
	 * Returns the ontology representation in form of an OWLModel object
	 * @return
	 */
	public OWLModel getOWLModel(){
		return this.owlModel;
	}

    private void loadModel(){
        try {
        	java.io.InputStream ontoStream = getClass().getClassLoader().getResourceAsStream(owlresource);
            this.owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(ontoStream);
        } catch (Exception ex) {
            String err = "An error occurred while trying to load the ontology:";
            log.fatal(err,ex);
        }
    }
    
    /**
     * 
     * Queries the Ontology with a given uri to fetch the OWLIndividual to build
     * the OntologyProperty from.
     * @param uri
     * @return ontologyproperty or null
     */
    public OntologyProperty getProperty(String uri){
    	//FIXME There may be more models coming
    	OWLNamedClass startClass = owlModel.getOWLNamedClass(OWLMODEL_ROOT_CLASS);
    	
    	//TB property uris correspond to OWLIndividuals
    	OWLIndividual individual = this.getOWLModel().getOWLIndividual(uri);
    	
    	if(individual != null){
        	//create ontology property for this individual
        	OntologyProperty ontoprop = new OntologyPropertyImpl(individual);
        	return ontoprop;
    	}
    	else{
    		//unexpected instance - no individual for this uri RETURN NULL
    		log.debug("Not able to retrieve a stored TB property URI from the OWLModel. uri: "+uri);
    		return null;
    	}
    }

}
