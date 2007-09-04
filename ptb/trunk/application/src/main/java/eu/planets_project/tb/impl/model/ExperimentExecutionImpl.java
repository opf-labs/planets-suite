/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Calendar;

import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentResults;
import eu.planets_project.tb.api.services.mockups.WorkflowExecution;
import eu.planets_project.tb.api.system.SystemMonitoring;

/**
 * @author alindley
 *
 */
//@Entity
public class ExperimentExecutionImpl extends eu.planets_project.tb.impl.model.ExperimentPhaseImpl
	implements ExperimentExecution, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExperimentResults()
	 */
	public ExperimentResults getExperimentResults() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getScheduledExecutionDate()
	 */
	public Calendar getScheduledExecutionDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getSystemMonitoringData()
	 */
	public SystemMonitoring getSystemMonitoringData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getWorkflowExecutionData()
	 */
	public WorkflowExecution getWorkflowExecutionData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#setExperimentResults(eu.planets_project.tb.api.model.ExperimentResults)
	 */
	public void setExperimentResults(ExperimentResults expResults) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#setScheduledExecutionDate(long)
	 */
	public void setScheduledExecutionDate(long millis) {
		// TODO Auto-generated method stub

	}
	
	public void setScheduledExecutionDate(Calendar date){
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#setSystemMonitoringData(eu.planets_project.tb.api.system.SystemMonitoring)
	 */
	public void setSystemMonitoringData(SystemMonitoring systemState) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#setWorkflowExecutionData(eu.planets_project.tb.api.services.mockups.WorkflowExecution)
	 */
	public void setWorkflowExecutionData(WorkflowExecution wee_Information) {
		// TODO Auto-generated method stub

	}

}
