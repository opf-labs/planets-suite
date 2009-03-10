/**
 * 
 */
package eu.planets_project.tb.impl.model.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;

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

}
