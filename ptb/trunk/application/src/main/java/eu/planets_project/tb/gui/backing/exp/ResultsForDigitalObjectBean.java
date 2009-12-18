/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;
import java.util.List;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
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

    private List<ExecutionRecordImpl> executionRecords = new ArrayList<ExecutionRecordImpl>();

    private BatchExecutionRecordImpl batch = null;
    
    
    /**
     * @param input
     */
    public ResultsForDigitalObjectBean( String input ) {
    	super(input);
    	this.init(input);
    }
    
    private void init(String file){
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // Loop over results and patch them in:
        for( BatchExecutionRecordImpl batch : expBean.getExperiment().getExperimentExecutable().getBatchExecutionRecords() ) {
            for( ExecutionRecordImpl run : batch.getRuns() ) {
                if( file.equals( run.getDigitalObjectReferenceCopy() ) ) {
                    getExecutionRecords().add(run);
                    this.batch = batch;
                }
            }
        }
        log.info("Result object initialised.");
    }
    

    /**
     * @return the executionRecords
     */
    public List<ExecutionRecordImpl> getExecutionRecords() {
        return executionRecords;
    }

    public boolean getHasExecuted() {
        if( this.batch == null ) return false;
        if( this.batch.getStartDate() == null ) return false;
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
        if( this.batch == null || this.batch.getWorkflowExecutionLog() == null ) return "No batch report logged.";
        return this.batch.getWorkflowExecutionLog().getSerializedWorkflowResult();
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
