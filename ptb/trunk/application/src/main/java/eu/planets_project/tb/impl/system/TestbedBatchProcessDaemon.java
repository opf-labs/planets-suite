/**
 * 
 */
package eu.planets_project.tb.impl.system;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
public class TestbedBatchProcessDaemon extends Thread {
    private static Log log = LogFactory.getLog(TestbedBatchProcessDaemon.class);
    
    TestbedBatchProcessor testbedBatchProcessor;
    
    long secs = 0;

    /**
     * @param testbedBatchProcessor
     */
    public TestbedBatchProcessDaemon(TestbedBatchProcessor testbedBatchProcessor) {
        this.testbedBatchProcessor = testbedBatchProcessor;
    }

    public void run() {
        while (true) {
            TestbedBatchJob job = testbedBatchProcessor.pollForNextJob();
            if( job != null ) {
                this.executeWorkflow(job);
            } else {
                try {
                    Thread.sleep(1000);
                    secs++;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public long getSecs() {
        return secs;
    }
    
    public void executeWorkflow( TestbedBatchJob job ) {
        job.setStatus(TestbedBatchJob.RUNNING);
        job.setPercentComplete(0);
        
        try {
            // Set up the basics:
            DataHandler dh = new DataHandlerImpl();
            int total = job.getDigitalObjects().size();
            int i = 0;
            // Process each in turn:
            for( String filename : job.getDigitalObjects() ) {
                log.info("Running job: "+(i+1)+"/"+total);
                File file = dh.getFile(filename);
                DigitalObject dob = new DigitalObject.Builder( Content.byValue(ByteArrayHelper.read(file)) ).build();
                WorkflowResult wfr = null;
                if( job.getWorkflow() == null ) {
                    throw new Exception("Cannot run this workflow, as it is null!");
                }
                try {
                    wfr = job.getWorkflow().execute(dob);
                    job.setWorkflowResult(filename, wfr);
                } catch( Exception e ) {
                    log.error("Workflow execution failed: "+e);
                    e.printStackTrace();
                }
                log.info("Workflow Execution Complete.");
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
                log.info("Ran job: "+(i+1)+"/"+total);
                i++;
                job.setPercentComplete((int)(100.0*i/total));
            }
        } catch( Exception e ) {
            job.setStatus(TestbedBatchJob.FAILED);
            job.setPercentComplete(100);
            log.error("Job failed, with exception: "+e);
            e.printStackTrace();
            return;
        }
        job.setStatus(TestbedBatchJob.DONE);
        job.setPercentComplete(100);
    }

}