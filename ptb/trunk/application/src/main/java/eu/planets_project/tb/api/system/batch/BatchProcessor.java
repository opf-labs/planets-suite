package eu.planets_project.tb.api.system.batch;

import java.util.Collection;
import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;


/**
 * General interface for submitting BatchExperiments to a specified BatchExecutionProcessor (e.g. TestbedBatchProcessor or IF/WEE)
 * and for querying metadata on the job.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 21.10.2009
 *
 */
public interface BatchProcessor {
	
	//Testbed's own batch processing engine (before TBv1.0)
	public static final String BATCH_IDENTIFIER_TESTBED_LOCAL = "TB#LOCAL";
	//Planets Workflow Execution Engine (local to the TB-CI) (from TBv1.0)
	public static final String BATCH_QUEUE_TESTBED_WEE_LOCAL = "TB#WEE#LOCAL";
	//a property that is used between an BatchProcessor Implementation and the ProcessorListener for exchanging messages
	public static final String QUEUE_PROPERTY_NAME_FOR_SENDING = "queue";
	
	 public static final String JOB_STATUS_NOT_STARTED = "not-started";
	 public static final String JOB_STATUS_RUNNING = "running";
	 public static final String JOB_STATUS_DONE = "done";
	 public static final String JOB_STATUS_FAILED = "failed";
	 public static final String JOB_STATUS_NO_SUCH_JOB = "no-such-job";
	
	@Deprecated
    public String submitBatch( long expID, ExperimentWorkflow workflow, Collection<String> digitalObjects);
	
	/**
	 * A general interface for submitting a Testbed Batch experiment to any known WorkflowExecution backend
	 * @param expID
	 * @param digObjs
	 * @param selectedTemplateQname
	 * @param workflowConfig
	 * @param WorkflowSystemID
	 * @return
	 */
	public String sumitBatch(long expID, List<DigitalObject> digObjs, WorkflowConf workflowConfig);

    public TestbedBatchJob getJob( String job_key );
	    
    public String getJobStatus( String job_key );
	    
    public int getJobPercentComplete( String job_key );
    
    public String getPositionInQueue(String job_key);
    
    /**
     * Returns the identifier of the used batch execution system for a given job_key
     * @param job_key
     * @return
     */
    public String getBatchProcessorSystemIdentifier();

    /**
     * A notify method for the listening job processor (e.g. MDB) to callback on a given job_key to deliver results
     * when the execution action was kicked-off
     * @param job
     */
    public void notifyStart(String job_key, TestbedBatchJob job);
    
    /**
     * A notify method for the listening job processor (e.g. MDB) to callback on a given job_key to deliver results
     * e.g. update experiment operations can be called within
     * @param job
     */
    public void notifyUpdate(String job_key, TestbedBatchJob job);
    
    /**
     * A notify method for the listening job processor (e.g. MDB) to callback on a given job_key to deliver 
     * the final results. e.g. update experiment actions can be called within.
     * @param job
     */
    public void notifyComplete(String job_key, TestbedBatchJob job);
    
    
    public void notifyRunning(String job_key, TestbedBatchJob job);
    

    /**
     * A notify method for the listening job processor (e.g. MDB) for reporting a failure in processing the 
     * submitted workflow
     * @param job_key
     * @param job
     */
    public void notifyFailed(String job_key, TestbedBatchJob job);
    
    /**
	 * Adds the a generated ticket to one of the queues that are used for polling on the state of a submitted batch execution 
	 * @param ticket
	 * @param queueName
	 * @throws Exception
	 */
    public void submitTicketForPollingToQueue(String ticket, String queueName, String batchProcessorSystemID)throws Exception;

    
    /**
     * Return if processing the job on the batch processor has switched to run once.
     * The process may also have already finshed or is currently running
     * @param job_key
     */
    public boolean isStarted(String job_key);
    
    /**
     * Returns if processing the job on the batch processor is currently taking place.
     * @param job_key
     * @return
     */
    public boolean isRunning(String job_key);
    

    /**
     * Return if processing the job on the batch processor has switched to completed.
     * no qualitative statement about failure or not
     * @param job_key
     */
    public boolean isCompleted(String job_key);
    
    /**
     * Return if processing the job on the batch processor has started but failed. 
     * @param job_key
     * @return
     */
    public boolean isFailed(String job_key);
    
    /**
     * Return if processing the job on the batch processor has produced an update
     * @param job_key
     * @return
     */
    public boolean isUpdated(String job_key);
    
    /**
     * Get a WorkflowResult object from the engine
     * @return
     */
    public Object getWorkflowEngineResult(String job_key);
}
