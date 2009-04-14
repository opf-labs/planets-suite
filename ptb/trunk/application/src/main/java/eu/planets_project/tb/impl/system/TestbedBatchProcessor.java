/**
 * 
 */
package eu.planets_project.tb.impl.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class TestbedBatchProcessor {
    
    private static Log log = LogFactory.getLog(TestbedBatchProcessor.class);
    
    private TestbedBatchProcessDaemon daemon;
    private HashMap<String,TestbedBatchJob> jobs = new HashMap<String,TestbedBatchJob>();
    private Queue<String> jobsQueued = new PriorityBlockingQueue<String>();
    private int job_id = 0;
    
    public TestbedBatchProcessor() {
       daemon = new TestbedBatchProcessDaemon(this);
       daemon.start();
    }

    /**
     * @return the daemon
     */
    public TestbedBatchProcessDaemon getDaemon() {
        return daemon;
    }

    /**
     * 
     * @param workflow
     * @param digitalObjects
     * @return
     */
    public synchronized String submitBatch( long expID, ExperimentWorkflow workflow, Collection<String> digitalObjects ) {
        TestbedBatchJob testbedBatchJob = new TestbedBatchJob( expID, workflow, digitalObjects );
        job_id++;
        String job_key ="TBK:"+job_id;
        jobs.put(job_key, testbedBatchJob);
        jobsQueued.add(job_key);
        log.info("Job has been queued : "+job_key);
        return job_key;
    }

    /**
     * 
     * @param job_key
     * @return
     */
    public TestbedBatchJob getJob( String job_key ) {
        return jobs.get(job_key);
    }
    
    /**
     * 
     * @param job_key
     * @return
     */
    public String getJobStatus( String job_key ) {
        if( this.getJob(job_key) == null ) return TestbedBatchJob.NO_SUCH_JOB;
        return this.getJob(job_key).getStatus();
    }
    
    /**
     * 
     * @param job_key
     * @return
     */
    public int getJobPercentComplete( String job_key ) {
        if( this.getJob(job_key) == null ) return 0;
        return this.getJob(job_key).getPercentComplete();
    }
    
    /**
     * 
     * @param job_key
     * @return
     */
    protected synchronized TestbedBatchJob pollForNextJob() {
        if( this.jobsQueued.peek() == null ) {
            return null;
        } else {
            return jobs.get( this.jobsQueued.poll() );
        }
    }
    
}
