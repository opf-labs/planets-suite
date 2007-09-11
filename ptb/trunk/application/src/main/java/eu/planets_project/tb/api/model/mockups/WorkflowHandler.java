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
	
	/**
	 * Gets all available Workflow names of a certain given Experiment type e.g. ExperimentTypes.EXPERIMENT_TYPE_EMULATION
	 * @param experimentType
	 * @return
	 */
	public List<String> getAllWorkflowNames(int experimentType);
	/**
	 * Gets all available Workflows of a certain given Experiment type e.g. ExperimentTypes.EXPERIMENT_TYPE_EMULATION
	 * @param experimentType
	 * @return
	 */
	public Collection<Workflow> getAllWorkflows(int experimentType);
	
	public ExperimentWorkflow getExperimentWorkflow(long lWorkflowEntityID);
	
	public Workflow getWorkflow(long workflowID);

}
