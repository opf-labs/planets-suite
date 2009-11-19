package eu.planets_project.ifr.core.wee.impl.workflow;

import java.util.UUID;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;

public class WorkflowInstanceImpl implements WorkflowInstance{

	private static final long serialVersionUID = 1002200923891L;
	private UUID wfID;
	private WorkflowTemplate wfTemplate;
	
	public WorkflowInstanceImpl(WorkflowTemplate wfTemplate){
		this.setWorkflowID(this.generateUUID());
		this.wfTemplate = wfTemplate;
	}
	
	public UUID getWorkflowID() {
		return this.wfID;
	}
	
	private void setWorkflowID(UUID id){
		this.wfID = id;
	}
	
    /**
     * Generate a UUID which is used as ticket for the client side polling
     * @return
     */
    private UUID generateUUID(){
    	return UUID.randomUUID();
    }

	public WorkflowResult execute() throws Exception{
		return this.getWorkflowTemplate().execute();
	}

	public WorkflowTemplate getWorkflowTemplate() {
		return this.wfTemplate;
	}

}
