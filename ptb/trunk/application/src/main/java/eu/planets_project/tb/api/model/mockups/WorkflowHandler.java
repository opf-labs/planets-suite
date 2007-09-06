package eu.planets_project.tb.api.model.mockups;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @author alindley
 *
 */
public interface WorkflowHandler {
	
	public Collection<Workflow> getAllWorkflows();
	public List<Long> getAllWorkflowIDs();
	public Map<Long,String> getAllWorkflowIDAndNames();
	public List<String> getAllWorkflowNames();
	
	public ExperimentWorkflow getExperimentWorkflow(long lWorkflowEntityID);

}
