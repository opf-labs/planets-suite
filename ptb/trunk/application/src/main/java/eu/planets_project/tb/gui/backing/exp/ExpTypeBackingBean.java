/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.ontology.OntologyHandlerImpl;
import eu.planets_project.tb.impl.model.ontology.util.OntoPropertyUtil;
import eu.planets_project.tb.impl.services.mockups.workflow.ExecutablePPWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.ViewerWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public abstract class ExpTypeBackingBean {
    private static Log log = LogFactory.getLog(ExpTypeBackingBean.class);

    /** Allow the workflow to be cached during editing. */
    private ExperimentWorkflow ewfCache = null;
    private HashMap<String, String> ewfCacheParameters = null;

    /** A Service Registry instance for look-ups. */
    protected ServiceRegistry registry = ServiceRegistryFactory.getServiceRegistry();

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
     * TODO Note that this is also a point that requires expansions when adding types.
     * @param etype
     * @return
     */
    public static final ExpTypeBackingBean getExpTypeBean( String etype ) {
        ExpTypeBackingBean exptype;
        if( etype.equals( AdminManagerImpl.IDENTIFY ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeIdentify");
        } else if( etype.equals( AdminManagerImpl.MIGRATE ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeMigrate");
        } else if( etype.equals( AdminManagerImpl.EMULATE ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeViewer");
        } else if( etype.equals( AdminManagerImpl.EXECUTABLEPP ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeExecutablePP");
  
        } else {
            // For unrecognised experiment types, set to NULL:
            exptype = null;
        }
        return exptype;
    }
    /**
     * TODO Note that this is also a point that requires expansions when adding types.
     * Reset all avialable expType beans within the Session Map
     * @param etype
     * @return
     */
    public static final void resetExpTypeBean() {
    	FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("ExpTypeIdentify", new ExpTypeIdentify());
        ctx.getExternalContext().getSessionMap().put("ExpTypeMigrate", new ExpTypeMigrate());
        ctx.getExternalContext().getSessionMap().put("ExpTypeViewer", new ExpTypeViewer());
        ctx.getExternalContext().getSessionMap().put("ExpTypeExecutablePP", new ExpTypeExecutablePP());
    }
    
    /**
     * TODO Record that this is one of the bits to change when adding experiment types.
     * 
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
            
        } else if( AdminManagerImpl.EMULATE.equals(etype)) {
            log.info("Running a Emulate experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof ViewerWorkflow) ) )
                ewfCache = new ViewerWorkflow();
            
        } else if( AdminManagerImpl.EXECUTABLEPP.equals(etype)) {
            log.info("Running an Executable PP experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof ExecutablePPWorkflow) ) )
                ewfCache = new ExecutablePPWorkflow();
            
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
                //Version v1.0 - the ExperimentExecutable().getParameters() aren't any longer used! All Information is encoded in a WFConf object
            	//TODO AL remove the Parameters from the ExperimentExecutable AND the ExpTypeBackingBean
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
    		List<MeasurementImpl> l;
    		if((staticOps==null)||(staticOps.get(stageName)==null)){
    			l = new ArrayList<MeasurementImpl>();
    		}else{
    			l = staticOps.get(stageName);
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
						log.error("error building Measurement from OntologyProperty: "+ontoProp + " :: " + e);
					}
        		}
        		
        	}
    		
    		ret.put(stageName, l);
    	}
    
    	return ret;
    }
    
    /**
     * This method is used to init the expTye backing beans for a given experiment.
     * i.e. init the beans that are related to a specific experiment type. Most types as
     * expTypeIdentify, expTypeMigrate, etc. don't require this 'fill' method.
     * Others as expTypeExecutablePP need to override this method.
     */
    public void initExpTypeBeanForExistingExperiment(){
    	
    }
    
    //TODO AL version 1.0: this method was introduced with the WEE backend and should used by all experiments that update to the WEE
    //backend for persisting  wizard step2
    /**
     * since version 1.0
     * This method is used to persist the ExpTypeBean specific information within step2 'configure workflow'
     * of an experiment to the DB model.
     */
    public void saveExpTypeBean_Step2_WorkflowConfiguration_ToDBModel(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        //store information in the db entities
		ExperimentExecutable expExecutable = expBean.getExperiment().getExperimentExecutable();
        //specify which batch processing system WEE or TB/Local we want to use for this experiment - default it's the old TB one.
        expExecutable.setBatchSystemIdentifier(BatchProcessor.BATCH_IDENTIFIER_TESTBED_LOCAL);
    }

    /**
     * since version 1.0
     * This method is used to check the ExpTypeBean specific workflow configuration is valid
     * @throws Exception use the Exception to pass a meaningfull statement to the user
     */
    public abstract void checkExpTypeBean_Step2_WorkflowConfigurationOK() throws Exception;
    
    /**
     * @return true if the ExperimentBean is of this type.
     */
    public abstract boolean isExperimentBeanType();
    
    /**
     * since version 1.0
     * This method is used to specify the ExpTypeBean's parameter configuration for the underlying WEE workflow
     * @param sServiceID
     * @param lParas
     */
    public abstract void setWorkflowParameters(Map<String,List<Parameter>> lParams);
    
    /**
     * since version 1.0
     * This method is used to get the ExpTypeBean's parameter that have been configured for the underlying WEE workflow
     * @return
     */
    public abstract Map<String,List<Parameter>> getWorkflowParameters();

}