/**
 * 
 */
package eu.planets_project.ifr.core.wee.api;

import java.util.UUID;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.datatypes.DigitalObject;


/**
 * It allows to submit a job to the WorkflowExecution Engine's queue and 
 * also maintains a list of jobs currently being executed and their status, etc.
 * 
 * A polling architecture for clients is implemented by using tickets for job identification.
 * 
 * The list of jobs is a nice thread-safe FIFO Queue which is processed by the 
 * messageDriven WorkflowExecutionEngine.
 * 
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 12.11.2008
 * 
 */
public interface WeeManager {

	/**
	 * Submits a workflow for execution. This will
	 *  - generate a UUID ticket
	 *  - send a message containing all information (workflow, ticket) to the queue
	 * @param workflow
	 * @return
	 */
	public UUID submitWorkflow(WorkflowInstance workflow);
	
	/**
	 * Returns the Workflow Execution Status for a given ticket.
	 * @param ticket
	 * @return
	 * @throws PlanetsException if ticket is unknown
	 */
	public WorkflowExecutionStatus getStatus(UUID ticket) throws PlanetsException;
	
	/**
	 * Returns the current position in the queue.
	 * @param ticket
	 * @return
	 * @throws PlanetsException if ticket is unknown
	 */
	public int getPositionInQueue(UUID ticket) throws PlanetsException;
	
	/**
	 * The callback method which is used by the execution engine to report back on 
	 * a wf execution and its status
	 * @param wfResult
	 * @param executionStatus
	 */
	public void notify(UUID ticket, WorkflowResult wfResult, WorkflowExecutionStatus executionStatus);
	/**
	 * The callback method which is used by the execution engine to report back on 
	 * a wf execution's status
	 * @param wfResult
	 * @param executionStatus
	 */
	public void notify(UUID ticket, WorkflowExecutionStatus status);
	

	/**
	 * Method for polling the wf execution results for a given ticket.
	 * @param ticket
	 * @return WorkflowResult
	 * @throws PlanetsException if status is != completed
	 */
	public WorkflowResult getExecutionResult(UUID ticket) throws PlanetsException;
	
	
	public boolean isExecutionRunning(UUID ticket);
	public boolean isExecutionCompleted(UUID ticket);
	public boolean isExecutionFailed(UUID ticket);
    
}
