/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.persistency.ExecutionRecordPersistency;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;

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
    public static final String RESULT_CREATEVIEW_RESULT = "createview_result";
    
    Object result;
    
    List<String> reportLog = new Vector<String>();
    
    URL mainEndpoint;
    
    Calendar startDate;
    
    Calendar endDate;
    
    /** */
    public WorkflowResult() {}


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
    public List<String> getReportLog() {
        return reportLog;
    }

    /**
     * @param report
     *            the report to set
     */
    public void logReport(ServiceReport report) {
        if (report == null)
            return;
        // Log the issue:
        reportLog.add(String.format("%s: %s (%s)", report.getType(), report
                .getMessage(), report.getStatus()));
        // Append properties if any are set:
        for (Property p : report.getProperties()) {
            reportLog.add(" - " + p.toString());
        }
    }

    /**
     * @return the mainEndpoint
     */
    public URL getMainEndpoint() {
        return mainEndpoint;
    }


    /**
     * @param mainEndpoint the mainEndpoint to set
     */
    public void setMainEndpoint(URL mainEndpoint) {
        this.mainEndpoint = mainEndpoint;
    }
    
    
    /**
     * @return the startDate
     */
    public Calendar getStartDate() {
        return startDate;
    }


    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }


    /**
     * @return the endDate
     */
    public Calendar getEndDate() {
        return endDate;
    }


    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }


    /**
     * @param wfr
     * @param exp
     */
    public static void recordWorkflowResultToExperiment(long eid, WorkflowResult wfr, String filename,
            BatchExecutionRecordImpl batch, Experiment exp ) {
        DataHandler dh = new DataHandlerImpl();
        try {
            ExecutionRecordImpl rec = new ExecutionRecordImpl(batch);
            rec.setDigitalObjectReferenceCopy(filename);
            try {
                rec.setDigitalObjectSource(dh.get(filename).getName());
            } catch (FileNotFoundException e) {
                rec.setDigitalObjectSource(filename);
            }
            // Populate the exec record:
            rec.setStartDate(wfr.getStartDate());
            rec.setEndDate(wfr.getEndDate());
            rec.setReportLog(wfr.getReportLog());
            // And the stages.
            Vector<ExecutionStageRecordImpl> stages = rec.getStages();
            
            if( wfr != null && wfr.getStages() != null ) {
                // Examine the result:
                if( WorkflowResult.RESULT_DIGITAL_OBJECT.equals(wfr.getResultType())) {
                    rec.setDigitalObjectResult( (DigitalObject) wfr.getResult(), exp );
                    
                } else if(WorkflowResult.RESULT_CREATEVIEW_RESULT.equals(wfr.getResultType()) ) {
                    CreateViewResult cvr = (CreateViewResult) wfr.getResult( );
                    Properties vp = new Properties();
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_SESSION_ID, cvr.getSessionIdentifier());
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_VIEW_URL, cvr.getViewURL().toString());
                    vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_ENDPOINT_URL, wfr.getMainEndpoint().toString() );
                    rec.setPropertiesListResult(vp);
                    
                } else {
                    rec.setResultType(ExecutionRecordImpl.RESULT_MEASUREMENTS_ONLY);
                }
                
                // Now pull out the stages, which include the measurements etc:
                for( ExecutionStageRecordImpl stage : wfr.getStages() ) {
                    // FIXME Can this be done from the session's Service Registry instead, please!?
                    if( stage.getEndpoint() != null ) {
                        log.info("Recording info about endpoint: "+stage.getEndpoint());
                        stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( eid, stage.getEndpoint(), Calendar.getInstance() ) );
                    }
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
