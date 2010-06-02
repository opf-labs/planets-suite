package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 30.10.2008
 * 
 * The representation of a WEE workflow 
 * i.e. a workflow template (=structure) + Planets Digital Objects (=payload)
 *
 */
public interface WorkflowInstance extends Serializable{

	/**
	 * A unique processing number of this instance which is used by the workflow execution engine
	 * Different WorkflowInstances of e.g. the same WorkflowTemplate created by the WorkflowFactory
	 * have different UUIDs assigned.
	 * @return
	 */
	public UUID getWorkflowID();
	
	public WorkflowTemplate getWorkflowTemplate();
	
	public WorkflowResult execute() throws Exception;
}
