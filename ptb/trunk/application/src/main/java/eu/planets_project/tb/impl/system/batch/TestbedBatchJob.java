/**
 * 
 */
package eu.planets_project.tb.impl.system.batch;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;

/**
 * A class for temporarily parking details that are related to a batch execution job
 * i.e. execution status, output objects, start/end date, etc., workflowResults
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andy Lindley</a>
 */
public class TestbedBatchJob {
    private static Log log = LogFactory.getLog(TestbedBatchJob.class);

    public static final String NOT_STARTED = "not-started";
    public static final String RUNNING = "running";
    public static final String DONE = "done";
    public static final String FAILED = "failed";
    public static final String NO_SUCH_JOB = "no-such-job";
    
    public static final String POSITION_NOT_SUPPORTED = "query position not supported";
    public static final String POSITION_IN_PROGRESS = "execution in progress";
    public static final String POSITION_COMPLETED = "execution completed";
    
    private long expID = -1;
    private ExperimentWorkflow workflow;
    //the digital Objects to execute upon
    private Collection<String> digitalObjects;
    private String status;
    private int percentComplete;
    private HashMap<String,WorkflowResult> results = new HashMap<String,WorkflowResult>();
    private Calendar startDate;
    private Calendar endDate;
    private String positionInQueue = "-1";
    //a workflow result (log) object depending on the engine we're using 
    private Object workflowResultEngineReport;
    //a workflow failure object depending on the engine we're using
    private Object overallWorkflowFailureReport;
    
    
    /**
     * constructor used for WEE experiments
     * @param expID
     */
    public TestbedBatchJob(long expID){
    	this(expID,null,null);
    }

    /**
     * @param workflow
     * @param digitalObjects
     */
    public TestbedBatchJob(long expID, ExperimentWorkflow workflow,
            Collection<String> digitalObjects) {
        this.expID = expID;
        this.workflow = workflow;
        this.digitalObjects = digitalObjects;
        this.status = NOT_STARTED;
        this.percentComplete = 0;
    }
    
    /**
     * @return the expID
     */
    public long getExpID() {
        return expID;
    }

    /**
     * @return the workflow
     */
    @Deprecated
    public ExperimentWorkflow getWorkflow() {
        return workflow;
    }

    /**
     * @return the digitalObjects
     */
    public Collection<String> getDigitalObjects() {
        return digitalObjects;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        log.info("Getting job status: "+status);
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        log.info("Setting job status to "+status);
        this.status = status;
    }

    /**
     * @return the percentComplete
     */
    public int getPercentComplete() {
        return percentComplete;
    }

    /**
     * @param percentComplete the percentComplete to set
     */
    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }
    
    /**
     * @return the percentComplete
     */
    public String getPositionInQueue() {
        return this.positionInQueue;
    }

    /**
     * @param percentComplete the percentComplete to set
     */
    public void setPositionInQueue(int percentComplete) {
       this.positionInQueue = percentComplete+"";
    }

    /**
     * @param filename
     * @param wfr
     */
    public void setWorkflowResult(String filename, WorkflowResult wfr) {
        this.results.put(filename, wfr);
    }
    
    /**
     * 
     * @param filename
     * @return
     */
    public WorkflowResult getWorkflowResult(String filename ) {
        return this.results.get(filename);
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
	 * The result object as returned by the workflow engine (e.g. WorkflowResult for WEE)
	 * @return
	 */
	public Object getWorkflowResultEngineReport() {
		return workflowResultEngineReport;
	}

	/**
	 * 	 
	 * The result object as returned by the workflow engine (e.g. WorkflowResult for WEE)
	 * @param workflowResultEngineReport
	 */
	public void setWorkflowResultEngineReport(Object workflowResultEngineReport) {
		this.workflowResultEngineReport = workflowResultEngineReport;
	}
	
	/**
	 * A failure object as returned by either the workflow engine or the BatchProcessExecutionListener
	 * @return
	 */
	public Object getWorkflowFailureReport() {
		return overallWorkflowFailureReport;
	}

	/**
	 * A failure object as returned by either the workflow engine or the BatchProcessExecutionListener
	 * @param workflowFailureReport
	 */
	public void setWorkflowFailureReport(Object workflowFailureReport) {
		this.overallWorkflowFailureReport = workflowFailureReport;
	}
    
}
