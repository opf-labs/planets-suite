/**
 * 
 */
package eu.planets_project.tb.impl.model.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;

/**
 * Responsible for loading the underlying ontology(s)
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 09.03.2009
 *
 */
public class OntologyHandlerImpl {
	
	private Log log = LogFactory.getLog(OntologyHandlerImpl.class);
	private final String owlresource = "eu/planets_project/tb/impl/TestbedOntology.owl";
	private OWLModel owlModel;
	private ProtegeReasoner reasoner;
	private static OntologyHandlerImpl instance;
	public static final String OWLMODEL_ROOT_CLASS = "XCLOntology:specificationPropertyNames";
	
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
            classifyTaxonomy(createPelletJenaReasoner(this.owlModel));
            
        } catch (Exception ex) {
            String err = "An error occurred while trying to load the ontology:";
            log.fatal(err,ex);
        }
    }
    
    /**
     * Register a ProtegePelletJena Reasoner for the given owl model
     * @param owlModel
     * @return
     */
    private ProtegeReasoner createPelletJenaReasoner(OWLModel owlModel) {

		// Get the reasoner manager and obtain a reasoner for the OWL model. 
		@SuppressWarnings("unused")
		ReasonerManager reasonerManager = ReasonerManager.getInstance();

		//Get an instance of the Protege Pellet reasoner
		ProtegeReasoner preasoner = null; 
		// FIXME Switching this out, as slf4j compatability bug means it is not usable at present. 
        //preasoner = reasonerManager.createProtegeReasoner(owlModel, ProtegePelletJenaReasoner.class);
		reasoner = preasoner;
		return preasoner;
	}
    
    /**
     * @param reasoner
     * @throws ProtegeReasonerException
     */
    private  void classifyTaxonomy(ProtegeReasoner reasoner) throws ProtegeReasonerException{
    	//classify the whole ontology, which will put the
		// inferred class hierarchy information directly into the Protege-OWL model. 
	
		reasoner.initialize();
		reasoner.computeInferredHierarchy();
		reasoner.computeInferredIndividualTypes();
	
		reasoner.classifyTaxonomy(); 
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
    	RDFIndividual individual = this.getOWLModel().getRDFIndividual(uri);
    	
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
