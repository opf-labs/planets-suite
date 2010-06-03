/**
 * 
 */
package eu.planets_project.tb.impl.system.batch.backends.tbown;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
public class TestbedBatchProcessDaemon extends Thread {
    private static Log log = LogFactory.getLog(TestbedBatchProcessDaemon.class);
    
    ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
    
    TestbedBatchProcessor testbedBatchProcessor;
    
    long msecs = 0;
    long sleepms = 500;

    /**
     * @param testbedBatchProcessor
     */
    public TestbedBatchProcessDaemon(TestbedBatchProcessor testbedBatchProcessor) {
        this.testbedBatchProcessor = testbedBatchProcessor;
    }

    public void run() {
        TestbedBatchJob job = null;
        while (true) {
            //log.debug("Looking for a job: t="+this.getSecs());
            job = testbedBatchProcessor.pollForNextJob();
            if( job != null ) {
                this.executeWorkflow(job);
                job = null;
            } else {
                try {
                    Thread.sleep( this.sleepms );
                    msecs += this.sleepms;
                    //log.debug("Slept: t="+this.getSecs());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public long getSecs() {
        return msecs/1000;
    }
    
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
                Calendar start = Calendar.getInstance();
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
                    // Patch in the start and end dates:
                    wfr.setStartDate(start);
                    wfr.setEndDate( Calendar.getInstance());
                    // Inspect the report:
                    if( wfr.getReportLog() != null ) {
                        log.info("Got report: " + wfr.getReportLog());
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
        exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);   
        
        // Persist these changes:
        log.info("Attempting to store results...");
        edao.updateExperiment(exp);
        log.info("Results have been stored in the experiment.");
    }
    
    /**
     * @param job
     * @return
     */
    private BatchExecutionRecordImpl createExperimentBatch(TestbedBatchJob job, Experiment exp) {
        BatchExecutionRecordImpl batch = new BatchExecutionRecordImpl((ExperimentExecutableImpl) exp.getExperimentExecutable());
        batch.setStartDate(job.getStartDate());
        log.info("Adding results of job "+job.getExpID()+" ("+job.getStatus()+") to experiment "+exp.getEntityID()+".");
        exp.getExperimentExecutable().getBatchExecutionRecords().add(batch);
        //FIXME edao.updateExperiment(exp);
        
        return batch;
    }

    /**
     * This is the low-level workflow execution for one Digital Object, and is the component that will behave differently when 
     * the IF WEE is used.
     * 
     * @param job The Testbed Job to run.
     * @param dob The Digital Object to be processed.
     * @return
     * @throws Exception
     */
    private WorkflowResult executeWorkflowOn( TestbedBatchJob job, DigitalObject dob ) throws Exception {
        WorkflowResult wfr = null;
        if( job.getWorkflow() == null ) {
            throw new Exception("Cannot run this workflow, as it is null!");
        }
        try {
            wfr = job.getWorkflow().execute(dob);
        } catch( Exception e ) {
            log.error("Workflow execution failed: "+e);
            e.printStackTrace();
        }
        log.info("Workflow Execution Complete.");
        return wfr;
    }

    /**
     * @param job
     * @param wfr
     * @param dob
     * @param filename
     */
    private void storeWorkflowResults(TestbedBatchJob job, WorkflowResult wfr,
            DigitalObject dob, String filename, BatchExecutionRecordImpl batch, Experiment exp ) {
        // Update the experiment from the job:
        WorkflowResult.recordWorkflowResultToExperiment( exp.getEntityID(), wfr, filename, batch, exp );
        //FIXME edao.updateExperiment(exp);
    }

}