/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * 
 * Note that as we move to TB 1.1, we are enforcing one batch execution.
 * Therefore, this class can safely assume that it should return batch 0, run 0, or none at all.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ResultsForDigitalObjectBean extends DigitalObjectBean {
    private Log log = LogFactory.getLog(ResultsForDigitalObjectBean.class);

    /**
     * @param input
     */
    public ResultsForDigitalObjectBean( String input ) {
    	super(input);
    }
    
    private BatchExecutionRecordImpl getBatch(){
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Set<BatchExecutionRecordImpl> batchExecutionRecords = expBean.getExperiment().getExperimentExecutable().getBatchExecutionRecords();
        if( batchExecutionRecords != null && batchExecutionRecords.size() > 0 )
            return batchExecutionRecords.iterator().next();
        return null;
    }
    

    /**
     * returns the ExecutionRecords for a given input digital object reference
     * @return the executionRecords
     */
    public List<ExecutionRecordImpl> getExecutionRecords() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<ExecutionRecordImpl> executionRecords = new ArrayList<ExecutionRecordImpl>();
        // Loop over results and patch them in:
        if( this.getBatch() != null  && this.getBatch().getRuns() != null ) {
            for( ExecutionRecordImpl run : this.getBatch().getRuns() ) {
                if( this.getDigitalObject().equals( run.getDigitalObjectReferenceCopy() ) ) {
                    executionRecords.add(run);
                }
            }
        }
        return executionRecords;
    }
   

    public boolean getHasExecuted() {
        BatchExecutionRecordImpl batch = this.getBatch();
        if( batch == null ) return false;
        if( batch.getStartDate() == null ) return false;
        return true;
    }
    
    /**
     * @return
     */
    public boolean getHasExecutionRecord() {
        if( this.getExecutionRecords() == null ) return false;
        if( this.getExecutionRecords().size() == 0 ) return false;
        return true;
    }

    /**
     * @return
     */
    public ExecutionRecordImpl getExecutionRecord() {
        if( this.getHasExecutionRecord() == false ) return null;
        return this.getExecutionRecords().get(0);
    }

    /**
     * @return
     */
    public boolean getHasResult() {
        if( this.getHasExecutionRecord() == false ) return false;
        if( this.getExecutionRecord().getResult() == null ) return false;
        if( "".equals( this.getExecutionRecord().getResult() ) ) return false;
        return true;
    }
    
    public boolean getHasExecutionSucceededOK(){
    	//currently the only criteria we're checking for measuring if a record has 
    	//successfully been migrated is if there's an output file - no workflow_result_log information taken into account
    	return this.getHasResult();
    }
    
    /**
     * How long did this record take to execute it's workflow upon the batch processor
     * e.g. how long did this migration take for the given record
     * @return the duration in milli seconds
     */
    public Long getExecutionDuration() {
    	if((this.getExecutionRecord()!=null)&&(this.getExecutionRecord().getEndDate()!=null)&&
    		(this.getExecutionRecord().getStartDate()!=null)){
    		
    		if(this.getExecutionRecord().getEndDate().getTimeInMillis() >= this.getExecutionRecord().getStartDate().getTimeInMillis()){
    			return this.getExecutionRecord().getEndDate().getTimeInMillis() - this.getExecutionRecord().getStartDate().getTimeInMillis();
    		}
    	}
    	return null;
    }

    /**
     * @return The list of results, stage by stage.
     */
    public List<StageMeasurementBean> getResultsByStage() {
        List<StageMeasurementBean> ms = new ArrayList<StageMeasurementBean>();
        // Peel through results:
        ExecutionRecordImpl executionRecord = this.getExecutionRecord();
        if( executionRecord == null ) return ms;
        for( ExecutionStageRecordImpl stagei : executionRecord.getStages() ) {
            for( MeasurementImpl res : stagei.getMeasuredObservables() ) {
                ms.add( new StageMeasurementBean( stagei, res ));
            }
            
        }
        return ms;
    }
    
    /**
     * @return the report from the batch processor level:
     */
    public String getBatchReport() {
        BatchExecutionRecordImpl batch = this.getBatch();
        if( batch == null || batch.getWorkflowExecutionLog() == null ) return "No batch report logged.";
        return batch.getWorkflowExecutionLog().getSerializedWorkflowResult();
    }
    
    /**
     * @return
     */
    public String getReport() {
        if( this.getExecutionRecord() == null || this.getExecutionRecord().getReportLog() == null ) return "No report logged.";
        String reportLog = "";
        for( String report : this.getExecutionRecord().getReportLog() ) {
            reportLog += report;
        }
        return reportLog;
    }

    /**
     * @return
     */
    public List<String> getReportLog() {
        if( this.getExecutionRecord() == null || this.getExecutionRecord().getReportLog() == null ) return null;
        return this.getExecutionRecord().getReportLog();
    }
    
    /**
     * checks if the is any result type of 'propertiesListResult' available
     * @return
     */
    public boolean isPropertiesListResultType(){
    	try {
			if((this.getExecutionRecord()!=null)&&(this.getExecutionRecord().getPropertiesListResult()!=null)){
				return true;
			}
		} catch (IOException e) {
		}
		return false;
    }
    
    /**
     * Returns all information when the experiment's result type is: 'PropertiesListResult' i.e.
     * all wee workflows use this result type to pass on information.
     * @return
     */
    public List<String> getResultPropertiesList(){
    	List<String> ret = new ArrayList<String>();
    	if( this.getExecutionRecord() == null || this.getExecutionRecord().getReportLog() == null )
    		return null;
    	try {
			Properties ps = this.getExecutionRecord().getPropertiesListResult();
			if(ps!=null){
				Enumeration enumeration = ps.keys();
				while(enumeration.hasMoreElements()){
					String key = (String)enumeration.nextElement();
					String value = ps.getProperty(key);
					if(!key.startsWith(ExecutionRecordImpl.RESULT_PROPERTY_URI)){
						ret.add("["+key+"= "+value+"]");
					}
				}
				// Sort list in Case-insensitive sort
	            Collections.sort(ret, String.CASE_INSENSITIVE_ORDER);
				return ret;	
			}
		} catch (IOException e) {
			log.debug("unable to fetch the resultPropertiesList in ResultsForDigitalObjectBean "+e);
			return null;
		}
    	return null;
    }
    

    /**
     * 
     * @author AnJackson
     *
     */
    public class StageMeasurementBean {
        
        private ExecutionStageRecordImpl stage;
        private MeasurementImpl measurement;

        /**
         * @param stage2
         * @param res
         */
        public StageMeasurementBean(ExecutionStageRecordImpl stage,
                MeasurementImpl res) {
            this.stage = stage;
            this.measurement = res;
        }

        /**
         * @return the stage
         */
        public ExecutionStageRecordImpl getStage() {
            return stage;
        }

        /**
         * @return the measurement
         */
        public MeasurementImpl getMeasurement() {
            return measurement;
        }

    }
}
