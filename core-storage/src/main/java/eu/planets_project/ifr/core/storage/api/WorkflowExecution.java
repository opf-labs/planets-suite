/*
 * WorkflowExecution.java
 *
 * The class stores information on a particular workflow execution
 */

package eu.planets_project.ifr.core.storage.api;

import java.util.List;

/**
 * 
 * @author CFwilson
 * @author Rainer Schmidt
 *
 */
public class WorkflowExecution {

	//id of workflow execution
	private String id = null;
	//id of workflow definition
	private String workflowId = null;
	private String userName = null;
	//protected because adding/removing events is not implemented as part of the bean
	protected List<WorkflowEvent> events = null;

	/**
	 * 
	 */
	public WorkflowExecution() {
	}

	/**
	 * 
	 * @param workflowId
	 * @param userName
	 */
	public WorkflowExecution(String workflowId, String userName) {
		this.workflowId = workflowId;
		this.userName = userName;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setWorkflowId(String id) {
		this.workflowId = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * 
	 * @param name
	 */
	public void setUser(String name) {
		this.userName = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getUser() {
		return userName;
	}
}
