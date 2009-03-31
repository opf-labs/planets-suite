/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.ontology.OntologyHandlerImpl;
import eu.planets_project.tb.impl.model.ontology.util.OntoPropertyUtil;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public abstract class ExpTypeBackingBean {
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeBackingBean.class);

    /** Allow the workflow to be cached during editing. */
    private ExperimentWorkflow ewfCache = null;
    private HashMap<String, String> ewfCacheParameters = null;

    /** A Service Registry instance for look-ups. */
    protected Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());

    /**
     * @return
     */
    public abstract HashMap<String, List<MeasurementImpl>> getObservables();
    
    public abstract HashMap<String, List<MeasurementImpl>> getManualObservables();
    
    /**
     * @return
     */
    public abstract List<ExperimentStageBean> getStageBeans();
    
    /**
     * @param etype
     * @return
     */
    public static final ExpTypeBackingBean getExpTypeBean( String etype ) {
        ExpTypeBackingBean exptype;
        if( etype.equals( AdminManagerImpl.IDENTIFY ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeIdentify");
        } else if( etype.equals( AdminManagerImpl.MIGRATE ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeMigrate");
        } else {
            // For unrecognised experiment types, set to NULL:
            exptype = null;
        }
        return exptype;
    }
    
    /**
     * @param etype
     * @return
     */
    public final ExperimentWorkflow getWorkflow( String etype ) {
        
        // Workflow, depending on the experiment type:
        if( AdminManagerImpl.IDENTIFY.equals(etype)) {
            log.info("Running an Identify experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof IdentifyWorkflow) ) )
                ewfCache = new IdentifyWorkflow();
            
        } else if( AdminManagerImpl.MIGRATE.equals(etype)) {
            log.info("Running a Migrate experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof MigrateWorkflow) ) )
                ewfCache = new MigrateWorkflow();
            
        } else {
            log.error("Unknown experiment type: "+etype);
            
        }

        // Ensure the parameters are stored/remembered:
        if( ewfCache != null ) {
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            try {
            	if(ewfCache.getParameters().equals(expBean.getExperiment().getExperimentExecutable().getParameters())){
            		//no update - don't call the time consuming ewfCache.setParameters
            	}else{
            		ewfCache.setParameters(expBean.getExperiment().getExperimentExecutable().getParameters());
            	}
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ewfCache;
    }
    
    /**
     * @param type
     * @return
     */
    protected List<ServiceDescription> lookupServicesByType( String type ) {
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, type ).build();
        return registry.query(sdQuery);
    }
    
    /**
     * Merges for every stage the static Measurements defined by the Workflow
     * as well as the Measurements added from the Ontology (and stored in the Experiment Executable)
     * @param staticOps
     * @param ontoOps
     * @return
     */
    protected HashMap<String,List<MeasurementImpl>> mergeManualObservables(HashMap<String,List<MeasurementImpl>> staticOps, HashMap<String,Vector<String>> ontoOps){
    	HashMap<String,List<MeasurementImpl>> ret = new HashMap<String,List<MeasurementImpl>>();
    	
    	//iterate over all known stages and merge into one List per stage
    	for(ExperimentStageBean sb : this.getStageBeans()){
    		
    		String stageName = sb.getName();
    		//for every stage add the static Measurements defined by the Workflow
    		List<MeasurementImpl> l = staticOps.get(stageName);
    		if(l ==null){
    			l = new ArrayList<MeasurementImpl>();
    		}
    		
    		//as well as the Measurements added from the Ontology (and stored in the Experiment Executable)
    		if(ontoOps!=null){
        		OntologyHandlerImpl ontohandler = OntologyHandlerImpl.getInstance();
        		for(String propURI : ontoOps.get(stageName)){
        			//query the authority to get the OntologyProperty by URI
        			OntologyProperty ontoProp = ontohandler.getProperty(propURI);
        			
					try {
						MeasurementImpl m = OntoPropertyUtil.createMeasurementFromOntologyProperty(ontoProp);
						l.add(m);
					} catch (Exception e) {
						log.debug("error building Measurement from OntologyProperty: "+ontoProp,e);
					}
        		}
        		
        	}
    		
    		ret.put(stageName, l);
    	}
    
    	return ret;
    }

}