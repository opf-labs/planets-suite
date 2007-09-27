/**
 * 
 */
package eu.planets_project.tb.api.system.mockup;

import eu.planets_project.tb.api.model.Experiment;

/**
 * @author alindley
 *
 */
public interface WorkflowInvoker {
	
	public void executeExperimentWorkflow(Experiment exp) throws Exception;
}
