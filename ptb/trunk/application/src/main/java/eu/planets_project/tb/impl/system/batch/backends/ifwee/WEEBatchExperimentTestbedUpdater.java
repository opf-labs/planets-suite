package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.BatchWorkflowResultLogImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.serialization.JaxbUtil;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;


/**
 * workflow processor specific actions for storing/extracting results when notified by the MDB.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 22.10.2009
 *
 */
public class WEEBatchExperimentTestbedUpdater {
	
	private static final Log log = LogFactory.getLog(WEEBatchExperimentTestbedUpdater.class);
	
	private TestbedWEEBatchProcessor tbWEEBatch;
	private ExperimentPersistencyRemote edao;
	private TestbedManager testbedMan;
	
	public WEEBatchExperimentTestbedUpdater(){
		tbWEEBatch = TestbedWEEBatchProcessor.getInstance();
		edao = ExperimentPersistencyImpl.getInstance();
		testbedMan = TestbedManagerImpl.getInstance();
	}
	 
	
	//TODO AL: check if these methods are needed and at the right place here
	/*public void processNotify_WorkflowQueued(){
		
	}
	
	public void processNotify_WorkflowRunning(){
		
	}*/
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution. In here all the TB specific mapping is done.
	 * 	1. extract digital objects created
	 *	2. store the WorkflowResult 
	 *	3. set all the stage information
	 * @param expID
	 * @param weeWFResult
	 */
	public void processNotify_WorkflowCompleted(long expID, WorkflowResult weeWFResult){
		//TODO AL: implement
		if(weeWFResult==null){
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not available");
			return;
		}
		//create a BatchExecutionRecord
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl();
		//startTime
		Calendar c1 = new GregorianCalendar();
		c1.setTimeInMillis(weeWFResult.getStartTime());
		batchRecord.setStartDate(c1);
		
		//endTime
		Calendar c2 = new GregorianCalendar();
		c2.setTimeInMillis(weeWFResult.getEndTime());
		batchRecord.setStartDate(c2);
	
		BatchWorkflowResultLogImpl wfResultLog = new BatchWorkflowResultLogImpl();
		try {
			//try serializing the workflow result log- as this is the way it needs to be stored
			String wfResultxml = JaxbUtil.marshallObjectwithJAXB(WorkflowResult.class, weeWFResult);
			log.debug("Successfully serialized the workflowResult Log via Jaxb" );
			//store the wfResultLog in the db model bean
			wfResultLog.setSerializedWorkflowResult(wfResultxml);
		} catch (Exception e) {
			log.debug("Problems serializing wfResultLog object",e);
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not serializable");
			return;
		}
		
		batchRecord.setWorkflowExecutionLog(wfResultLog);
		batchRecord.setBatchRunSucceeded(true);
		
		//now add the Records
		ExecutionRecordImpl execRecord = new ExecutionRecordImpl();
		//TODO AL CONTINUE HERE extract digos, set executable end time, etc.
		
		this.helperUpdateExpWithBatchRecord(expID, batchRecord);
	}
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution
	 * @param expID
	 * @param failureReason
	 */
	public void processNotify_WorkflowFailed(long expID,String failureReason){
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl();
		batchRecord.setBatchRunSucceeded(false);
		
		this.helperUpdateExpWithBatchRecord(expID, batchRecord);
		//TODO AL: any more fields to set?
	}
	
	/**
	 * All actions of setting an experiment into state 'processing has started'
	 * @param expID
	 */
	public void processNotify_WorkflowStarted(long expID){
		Experiment exp = testbedMan.getExperiment(expID);
    	exp.getExperimentExecutable().setExecutableInvoked(true);
    	//testbedMan.updateExperiment(exp);
    	edao.updateExperiment(exp);
	}
	
	private void helperUpdateExpWithBatchRecord(long expID,BatchExecutionRecordImpl record){
    	Experiment exp = testbedMan.getExperiment(expID);
    	exp.getExperimentExecutable().getBatchExecutionRecords().add(record);
		exp.getExperimentExecutable().setExecutionCompleted(true);
		//testbedMan.updateExperiment(exp);
		edao.updateExperiment(exp);
	}
	
	//CHOSE HOW TO DO THIS - define Arguments
	//update Experiment information with the WEE execution results
	private void storeWorkflowResultForDigo( ){
		
	}
	
	/*private ExecutionRecordImpl createExecutionRecordToExperiment(long eid, WorkflowResult wfr, String filename) {
        DataHandler dh = new DataHandlerImpl();
        try {
            ExecutionRecordImpl rec = new ExecutionRecordImpl();
            rec.setDigitalObjectReferenceCopy(filename);
            try {
                rec.setDigitalObjectSource(dh.get(filename).getName());
            } catch (FileNotFoundException e) {
                rec.setDigitalObjectSource(filename);
            }
            // FIXME Set this in the job somewhere:
            rec.setDate(Calendar.getInstance());
            List<ExecutionStageRecordImpl> stages = rec.getStages();
            
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
        
    }*/

	
	/**
	 * @see TestbedBatchProcessDaemon
	 * @param job
	 * @param exp
	 * @return
	 */
	/*private BatchExecutionRecordImpl createExperimentBatch(TestbedBatchJob job, Experiment exp) {
        BatchExecutionRecordImpl batch = new BatchExecutionRecordImpl();
        exp.getExperimentExecutable().getBatchExecutionRecords().add(batch);
        batch.setStartDate(job.getStartDate());
        edao.updateExperiment(exp);
        
        return batch;
    }*/
	
	
	
	/*
	 * DELTE
	public void executeWorkflow( TestbedBatchJob job ) {
        job.setStatus(TestbedBatchJob.RUNNING);
        job.setPercentComplete(0);
        job.setStartDate(Calendar.getInstance());
        Experiment exp = edao.findExperiment(job.getExpID());
        // Set up the DB:
        BatchExecutionRecordImpl batch = this.createExperimentBatch(job, exp);
        
        try {
            // FIXME, Some experiment types may take all DOBs into one workflow?  Emulation?
            
            // Set up the basics:
            DataHandler dh = new DataHandlerImpl();
            int total = job.getDigitalObjects().size();
            int i = 0;
            
            
            // Process each in turn:
            for( String filename : job.getDigitalObjects() ) {
                log.info("Running job: "+(i+1)+"/"+total);
                DigitalObject dob = dh.get(filename).getDigitalObject();
                WorkflowResult wfr = null;
                
                // Actually run the workflow:
                try {
                    wfr = this.executeWorkflowOn(job, dob);
                    job.setWorkflowResult(filename, wfr);
                } catch( Exception e ) {
                    e.printStackTrace();
                }
                
                // Report:
                if( wfr != null ) {
                    if( wfr.getReport() != null ) {
                        log.info("Got report: " + wfr.getReport().toString());
                    }
                    // Is there a result?
                    if( wfr.getResult() != null ) {
                        log.info("Got result: "+wfr.getResult().toString());
                    }
                }

                
                // Store results in the database:
                this.storeWorkflowResults(job, wfr, dob, filename, batch, exp );
                
                log.info("Ran job: "+(i+1)+"/"+total);
                // Update counter:
                i++;
                job.setPercentComplete((int)(100.0*i/total));
            }
            
            // Record that all went well:
            log.info("Status: DONE - All went well.");
            // Set the job status:
            job.setStatus(TestbedBatchJob.DONE);
            job.setPercentComplete(100);
            job.setEndDate(Calendar.getInstance());
            // Record batch info:
            exp.getExperimentExecutable().setExecutionSuccess(true);
            batch.setBatchRunSucceeded(true);
        } catch( Exception e ) {
            job.setStatus(TestbedBatchJob.FAILED);
            job.setPercentComplete(100);
            job.setEndDate(Calendar.getInstance());
            log.error("Job failed, with exception: "+e);
            batch.setBatchRunSucceeded(false);
            exp.getExperimentExecutable().setExecutionSuccess(false);
            e.printStackTrace();
        }

        // Record general information:
        batch.setEndDate(job.getEndDate());
        exp.getExperimentExecutable().setExecutableInvoked(true);
        exp.getExperimentExecutable().setExecutionCompleted(true);
        exp.getExperimentExecutable().setExecutionEndDate(Calendar.getInstance().getTimeInMillis());
        exp.getExperimentExecution().setEndDate(Calendar.getInstance());
        
        // Persist these changes:
        log.info("Attempting to store results...");
        edao.updateExperiment(exp);
        log.info("Results have been stored in the experiment.");
        */

}
