/**
 * 
 */
package eu.planets_project.tb.impl.system.batch.backends.tbown;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;
import eu.planets_project.tb.impl.system.batch.TestbedBatchProcessorManager;

/**
 * This bean is managed by JSF and is given application scope.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class TestbedBatchProcessor implements BatchProcessor{
    
    private static Log log = LogFactory.getLog(TestbedBatchProcessor.class);
    
    private TestbedBatchProcessDaemon daemon;
    private HashMap<String,TestbedBatchJob> jobs = new HashMap<String,TestbedBatchJob>();
    private Queue<String> jobsQueued = new LinkedBlockingQueue<String>();
    private int job_id = 0;
    
    public TestbedBatchProcessor() {
        log.info("Constructing BG process.");
        daemon = new TestbedBatchProcessDaemon(this);
        daemon.start();
        log.info("Daemon started.");
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
    public synchronized String submitBatch( long expID, ExperimentWorkflow workflow, Collection<String> digitalObjects) {
        TestbedBatchJob testbedBatchJob = new TestbedBatchJob( expID, workflow, digitalObjects );
        job_id++;
        String job_key ="TBK:"+job_id;
        log.info("Queuing job : "+job_key);
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

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getBatchProcessorSystemIdentifier()
	 */
	public String getBatchProcessorSystemIdentifier() {
		return this.BATCH_IDENTIFIER_TESTBED_LOCAL;
	}


	/* 
	 * since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getPositionInQueue(java.lang.String)
	 */
	public String getPositionInQueue(String job_key) {
		return TestbedBatchJob.POSITION_NOT_SUPPORTED;
	}

	
	// --------------- the following information was introduced with version 1.0 ----------------->
	// if updated to the new structure this TB Batch Processor could use the new MDB listeners
	
	/* 
	 * since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#sumitBatch(long, java.util.List, eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf)
	 */
	public String sumitBatch(long expID, List<DigitalObject> digObjs,
			WorkflowConf workflowConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyComplete(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public void notifyComplete(String job_key, TestbedBatchJob job) {
		// TODO Auto-generated method stub
		
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyUpdate(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public void notifyUpdate(String job_key, TestbedBatchJob job) {
		// TODO Auto-generated method stub
		
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getWorkflowEngineResult(java.lang.String)
	 */
	public Object getWorkflowEngineResult(String job_key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isCompleted(java.lang.String)
	 */
	public boolean isCompleted(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isFailed(java.lang.String)
	 */
	public boolean isFailed(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isRunning(java.lang.String)
	 */
	public boolean isRunning(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isStarted(java.lang.String)
	 */
	public boolean isStarted(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isUpdated(java.lang.String)
	 */
	public boolean isUpdated(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyFailed(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public void notifyFailed(String job_key, TestbedBatchJob job) {
		// TODO Auto-generated method stub
		
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyRunning(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public void notifyRunning(String job_key, TestbedBatchJob job) {
		// TODO Auto-generated method stub
		
	}

	/* since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyStart(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public void notifyStart(String job_key, TestbedBatchJob job) {
		// TODO Auto-generated method stub
		
	}

	/* 
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#submitTicketForPollingToQueue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void submitTicketForPollingToQueue(String ticket, String queueName,
			String batchProcessorSystemID) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isQueued(java.lang.String)
	 */
	public boolean isQueued(String job_key) {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	public String sumitBatchByReference(long expID, List<URI> digObjRef,
			WorkflowConf workflowConfig) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
