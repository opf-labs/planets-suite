package eu.planets_project.ifr.core.storage.api;

import javax.jcr.ItemNotFoundException;

/**
 * The WorkflowManager provides methods frot the storage, querying and retrieval of PLANETS IF Workflow objects and data.
 * 
 * @author CFwilson
 *
 */
public interface WorkflowManager {
	/**
	 * Persists a WorkflowDefinition object to the data registry
	 * 
	 * @param	workflow
	 * 			A WorkflowDefinition object to be persisted to the data registry
	 * @return	The id of the created WorkflowDefinition
	 */
	public String createWorkflow(WorkflowDefinition workflow);

    /**
     * Returns the details of the WorkflowDefinition identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the WorkflowDefinition to be retrieved.
     * @return	The WorkflowDefinition with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     */
	public WorkflowDefinition getWorkflow(String id) throws ItemNotFoundException;
	
	/**
	 * Persists a WorkflowExecution object to the data registry
	 * 
	 * @param	workflow
	 * 			A WorkflowExecution object to be persisted to the data registry
	 * @return	The id of the created WorkflowExecution
	 */
	public String createWorkflowInstance(WorkflowExecution workflow);
	
    /**
     * Returns the details of the WorkflowExecution identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the WorkflowExecution to be retrieved.
     * @return	The WorkflowExecution with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     */
	public WorkflowExecution getWorkflowInstance(String id) throws ItemNotFoundException;

	/**
	 * Persists a InvocationEvent object to the data registry
	 * 
	 * @param	event
	 * 			A InvocationEvent object to be persisted to the data registry
	 * @param	workflowExecutionId
	 * 			The id of the workflow execution that is the parent of the InvocationEvent
	 * @return	The id of the created InvocationEvent
	 */
	public String createInvocationEvent(InvocationEvent event, String workflowExecutionId);
	
    /**
     * Returns the details of the InvocationEvent identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the InvocationEvent to be retrieved.
     * @return	The InvocationEvent with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     */
	public InvocationEvent getInvocationEvent(String id) throws ItemNotFoundException;
}
