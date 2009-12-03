/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.net.URI;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpTypeIdentify extends ExpTypeBackingBean {
    private Log log = LogFactory.getLog(ExpTypeIdentify.class);
    
    /**
     * @return the identifyService
     */
    public String getIdentifyService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        log.info("Got params: "+expBean.getExperiment().getExperimentExecutable().getParameters() );
        log.info("Got param: "+expBean.getExperiment().getExperimentExecutable().getParameters().get(IdentifyWorkflow.PARAM_SERVICE) );
        return expBean.getExperiment().getExperimentExecutable().getParameters().get(IdentifyWorkflow.PARAM_SERVICE);
    }

    /**
     * @param identifyService the identifyService to set
     */
    public void setIdentifyService(String identifyService) {
        log.info("Setting the Identify service to: "+identifyService);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(IdentifyWorkflow.PARAM_SERVICE, identifyService);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getStageBeans()
     */
    @Override
    public List<ExperimentStageBean> getStageBeans() {
        return getWorkflow(AdminManagerImpl.IDENTIFY).getStages();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return getWorkflow(AdminManagerImpl.IDENTIFY).getObservables();
    }
    
    HashMap<String,List<MeasurementImpl>> manualObsCache;
    long cacheExperimentID;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getManualObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getManualObservables() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if(manualObsCache==null||(cacheExperimentID != expBean.getExperiment().getEntityID())){
    		cacheExperimentID = expBean.getExperiment().getEntityID();
    		
        	//query for properties that have been added from the Ontology
        	HashMap<String,Vector<String>> ontoPropIDs = new HashMap<String, Vector<String>>();
        	for(ExperimentStageBean stage : expBean.getStages()){
        		ontoPropIDs.put(stage.getName(),expBean.getExperiment().getExperimentExecutable().getManualProperties(stage.getName()));
        	}
        	
        	//this is the static list of manual properties - normally empty
        	HashMap<String,List<MeasurementImpl>> staticWFobs = getWorkflow(AdminManagerImpl.IDENTIFY).getManualObservables();
        	
        	//FIXME AL: staticWFobs returns wrong items - where are they added - exclude staticWFobs for now
        	//manualObsCache = mergeManualObservables(staticWFobs, ontoPropIDs);
        	manualObsCache = mergeManualObservables(null, ontoPropIDs);
    	}
    	return manualObsCache;
    }
    

    /**
     * A Bean to hold the results on each digital object.
     */
    public class FormatResultsForDO  extends ResultsForDigitalObjectBean {

        /**
         * @param input
         */
        public FormatResultsForDO(String input) {
            super(input);
        }
        
        /**
         * 
         * @return
         */
        public List<FormatResultBean> getFormats() {
            List<FormatResultBean> frb = new Vector<FormatResultBean>();
            for( ExecutionRecordImpl exec : this.getExecutionRecords() ) {
                for( ExecutionStageRecordImpl stage : exec.getStages() ) {
                    if( stage.getStage().equals( IdentifyWorkflow.STAGE_IDENTIFY )) {
                        for( MeasurementRecordImpl m : stage.getMeasurements() ) {
                            if( m.getIdentifier().equals(TecRegMockup.URIDigitalObjectPropertyRoot+"basic/format")) {
                                frb.add(new FormatResultBean(m.getValue()));
                            }
                        }
                    }
                }
            }
            return frb;
        }
        
        
    }
    
    /**
     * @return
     */
    public List<FormatResultsForDO> getFormatResults() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<FormatResultsForDO> results = new Vector<FormatResultsForDO>();
        // Populate using the results:
        for( String file : expBean.getExperimentInputData().values() ) {
            FormatResultsForDO res = new FormatResultsForDO(file);
            results.add(res);
        }

        // Now return the results:
        return results;
        
    }
    


    /**
     * 
     * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
     */
    public class FormatResultBean {
        
        private String format;

        /**
         * @param stage
         */
        public FormatResultBean( String format ) {
            this.format = format;
        }
        
        /**
         * @return
         */
        public String getSummary() {
            try {
                Format f = ServiceBrowser.fr.getFormatForUri(new URI(format));
                if( f.getExtensions() != null && f.getExtensions().size() > 0 ) {
                    String fs = ""; // Use the (1st) longest extension:
                    for( String ext : f.getExtensions() ) {
                        if( ext.length() > fs.length() ) fs = ext;
                    }
                    fs = fs.toUpperCase();
                    if( f.getVersion() != null ) fs += " v."+f.getVersion();
                    if( f.getSummary() != null )
                        fs += " - "+f.getSummary();
                    fs += " ("+f.getUri()+")";
                    return fs;
                } else {
                    return f.getSummary() + " " + f.getVersion();
                }
            } catch ( Exception e) {
                log.error("Could not understand format URI: "+format);
                return "unknown";
            }
        }
        
    }

    /* 
	 * TODO AL: version 1.0 uses this structure to check for a valid workflow (exp-type specific) configuration.
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#checkExpTypeBean_Step2_WorkflowConfigurationOK()
	 */
	@Override
	public void checkExpTypeBean_Step2_WorkflowConfigurationOK() throws Exception{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#isExperimentBeanType()
	 */
	@Override
	public boolean isExperimentBeanType() {
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		if( expBean == null ) return false;
		log.info("Checking "+expBean.getEtype()+" eq "+AdminManagerImpl.IDENTIFY+" : "+AdminManagerImpl.IDENTIFY.equals(expBean.getEtype()));
		if( AdminManagerImpl.IDENTIFY.equals(expBean.getEtype()) ) return true;
		return false;
	}
	
    
}
