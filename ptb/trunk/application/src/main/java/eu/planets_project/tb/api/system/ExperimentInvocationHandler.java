/**
 * 
 */
package eu.planets_project.tb.api.system;

import eu.planets_project.tb.api.model.Experiment;

/**
 * @author alindley
 *
 */
public interface ExperimentInvocationHandler {
	
	public void executeExperiment(Experiment exp);
}
