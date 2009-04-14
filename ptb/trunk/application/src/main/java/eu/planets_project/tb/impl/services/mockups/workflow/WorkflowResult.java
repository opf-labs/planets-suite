/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.persistency.ExecutionRecordPersistency;
import eu.planets_project.tb.impl.system.TestbedBatchJob;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class WorkflowResult {
    private static Log log = LogFactory.getLog(WorkflowResult.class);
    
    List<ExecutionStageRecordImpl> stages = new Vector<ExecutionStageRecordImpl>();
    
    String resultType;
    public static final String RESULT_URI = "uri";
    public static final String RESULT_DIGITAL_OBJECT = "digital_object";
    
    Object result;
    
    ServiceReport report;
    
    /** */
    protected WorkflowResult() {}


    /**
     * @return the stage
     */
    public ExecutionStageRecordImpl getStage( int stage ) {
        return stages.get(stage);
    }
    
    /**
     * @return
     */
    public List<ExecutionStageRecordImpl> getStages() {
        return stages;
    }


    /**
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }


    /**
     * @param resultType the resultType to set
     */
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }


    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }


    /**
     * @param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }


    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }


    /**
     * @param report the report to set
     */
    public void setReport(ServiceReport report) {
        this.report = report;
    }

    /**
     * @param wfr
     * @param exp
     */
    public static void recordWorkflowResultToExperiment(WorkflowResult wfr, String filename,
            BatchExecutionRecordImpl batch ) {
        DataHandler dh = new DataHandlerImpl();
        try {
            ExecutionRecordImpl rec = new ExecutionRecordImpl();
            rec.setDigitalObjectReferenceCopy(filename);
            try {
                rec.setDigitalObjectSource(dh.getName(filename));
            } catch (FileNotFoundException e) {
                rec.setDigitalObjectSource(filename);
            }
            // FIXME Set this in the job somewhere:
            rec.setDate(Calendar.getInstance());
            List<ExecutionStageRecordImpl> stages = rec.getStages();
            
            if( wfr != null && wfr.getStages() != null ) {
                // Examine the result:
                if( WorkflowResult.RESULT_DIGITAL_OBJECT.equals(wfr.getResultType())) {
                    DigitalObject dob = (DigitalObject) wfr.getResult();
                    try {
                        // FIXME Check dob.getContent().read() != null?
                        String storeKey = dh.addBytestream(dob.getContent().read(), dob.getTitle());
                        rec.setResult(storeKey);
                        rec.setResultType(ExecutionRecordImpl.RESULT_DATAHANDLER_REF);
                        /* FIXME In the future, store the whole DO in the TB DR.
                Add dob.gatherBinaries/embedBinaries method?
                         */
                    } catch (IOException e) {
                        log.error("Could not store result DigitalObject - "+dob);
                        e.printStackTrace();
                    }
                } else {
                    rec.setResultType(ExecutionRecordImpl.RESULT_MEASUREMENTS_ONLY);
                }
                // Now pull out the stages, which include the measurements etc:
                for( ExecutionStageRecordImpl stage : wfr.getStages() ) {
                    // FIXME Can this be done from the session's Service Registry instead, please!
                    log.info("Recording info about endpoint: "+stage.getEndpoint());
                    stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( stage.getEndpoint()) );
                    // Re-reference this stage object from the Experiment:
                    stages.add(stage);
                }
            }

            batch.getRuns().add(rec);
            log.info("Added records ("+batch.getRuns().size()+") for "+rec.getDigitalObjectSource());
        } catch( Exception e ) {
            log.error("Exception while parsing Execution Record.");
            e.printStackTrace();
        }
        
    }
}
