package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;


/**
 * workflow processor specific actions for storing/extracting results when notified by the MDB.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 22.10.2009
 *
 */
public class WEEBatchExperimentTestbedUpdater {
	
	private TestbedWEEBatchProcessor tbWEEBatch;
	private ExperimentPersistencyRemote edao;
	
	public WEEBatchExperimentTestbedUpdater(){
		tbWEEBatch = TestbedWEEBatchProcessor.getInstance();
		edao = ExperimentPersistencyImpl.getInstance();
	}
	 
	
	//TODO AL: check if these methods are needed and at the right place here
	/*public void processNotify_WorkflowQueued(){
		
	}
	
	public void processNotify_WorkflowRunning(){
		
	}*/
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution
	 * @param expID
	 * @param result
	 */
	public void processNotify_WorkflowCompleted(long expID, WorkflowResult result){
		//TODO AL: implement
		//1. extract digital objects created
		//2. store the WorkflowResult 
		//3. set all the stage information
	}
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution
	 * @param expID
	 * @param failureReason
	 */
	public void processNotify_WorkflowFailed(long expID,String failureReason){
		//TODO AL: implement
		//3. set all the stage information
	}
	
	//CHOSE HOW TO DO THIS - define Arguments
	//update Experiment information with the WEE execution results
	private void storeWorkflowResultForDigo( ){
		
	}
	
	/**
	 * Takes a workflow execution engine specific result object and populates the
	 * Testbed's batchjob result object with information.
	 * In here all the TB specific mapping is done.
	 * @param job - an existing TestbedBatchJob to update
	 * @param weeWFResult
	 * @return
	 */
	private TestbedBatchJob updateTBBatchJob(TestbedBatchJob job, WorkflowResult weeWFResult){
		
		Experiment exp = edao.findExperiment(job.getExpID());
        // Set up the DB:
        //TODO AL: check: BatchExecutionRecordImpl batch = this.createExperimentBatch(job, exp);
        
		if(!weeWFResult.isPartialResults()){
			//we're having a full WorkflowResult
			
		  //a) set workflow results
			//startTime
			Calendar c1 = new GregorianCalendar();
			c1.setTimeInMillis(weeWFResult.getStartTime());
			job.setStartDate(c1);
			
			//endTime
			Calendar c2 = new GregorianCalendar();
			c2.setTimeInMillis(weeWFResult.getEndTime());
			job.setEndDate(c2);
			
			//percentComplete
			job.setPercentComplete(weeWFResult.getProgress());
			
			//status
			job.setStatus(TestbedBatchJob.DONE);
			
		  //b) set workflow_item results
            for( WorkflowResultItem item : weeWFResult.getWorkflowResultItems() ) {
               
            	eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult tbWFR = new eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult();
            	tbWFR.setReport(item.getServiceReport());
            	//...
            	
            	//TODO AL: for now just extract the DigitalObject
            	//...
            }
                
			
		}
		else{
			//TODO AL: take care of incremental updates when supported by WEE...
		}
		return job;
	}
	
	 /*public static void recordWorkflowResultToExperiment(long eid, WorkflowResult wfr, String filename,
	            BatchExecutionRecordImpl batch, Experiment exp ) {
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
