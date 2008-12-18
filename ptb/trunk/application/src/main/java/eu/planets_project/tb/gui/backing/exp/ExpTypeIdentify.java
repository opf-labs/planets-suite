/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpTypeIdentify {
    private PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeIdentify.class, "testbed-log4j.xml");
    
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

    /**
     * 
     * @return
     */
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return this.getIdentifyWorkflow().getObservables();
    }

    /**
     * 
     * @return
     */
    public ExperimentWorkflow getIdentifyWorkflow() {
        return new IdentifyWorkflow();
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
                Format f = ServiceBrowser.fr.getFormatForURI(new URI(format));
                if( f.getExtensions() != null && f.getExtensions().size() > 0 ) {
                    String fs = ""; // Use the (1st) longest extension:
                    for( String ext : f.getExtensions() ) {
                        if( ext.length() > fs.length() ) fs = ext;
                    }
                    fs = fs.toUpperCase();
                    if( f.getVersion() != null ) fs += " v."+f.getVersion();
                    if( f.getSummary() != null )
                        fs += " - "+f.getSummary();
                    fs += " ("+f.getTypeURI()+")";
                    return fs;
                } else {
                    return f.getSummaryAndVersion();
                }
            } catch ( Exception e) {
                log.error("Could not understand format URI: "+format);
                return "unknown";
            }
        }
        
    }
    
}
