/**
 * 
 */
package eu.planets_project.tb.impl.system;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class TestbedBatchJob {
    private static Log log = LogFactory.getLog(TestbedBatchJob.class);

    public static final String NOT_STARTED = "not-started";
    public static final String RUNNING = "running";
    public static final String DONE = "done";
    public static final String FAILED = "failed";
    public static final String NO_SUCH_JOB = "no-such-job";
    
    private ExperimentWorkflow workflow;
    private Collection<String> digitalObjects;
    private String status;
    private int percentComplete;
    private HashMap<String,WorkflowResult> results = new HashMap<String,WorkflowResult>();

    /**
     * @param workflow
     * @param digitalObjects
     */
    public TestbedBatchJob(ExperimentWorkflow workflow,
            Collection<String> digitalObjects) {
        this.workflow = workflow;
        this.digitalObjects = digitalObjects;
        this.status = NOT_STARTED;
        this.percentComplete = 0;
    }

    /**
     * @return the workflow
     */
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
    
}
