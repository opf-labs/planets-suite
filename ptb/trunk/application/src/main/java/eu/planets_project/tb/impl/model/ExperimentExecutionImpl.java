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
public class ExperimentExecutionImpl 
	implements ExperimentExecution, java.io.Serializable {

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
	public long getScheduledExecutionDate() {
		// TODO Auto-generated method stub
		return 0;
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

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getDurationInMillis()
	 */
	public long getDurationInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDate()
	 */
	public Calendar getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDateInMillis()
	 */
	public long getEndDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEntityID()
	 */
	public long getEntityID() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getResult()
	 */
	public int getResult() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDate()
	 */
	public Calendar getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDateInMillis()
	 */
	public long getStartDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getState()
	 */
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getSummary()
	 */
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isCompleted()
	 */
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isInProgress()
	 */
	public boolean isInProgress() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isNotStarted()
	 */
	public boolean isNotStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setEndDate(java.util.Calendar)
	 */
	public void setEndDate(Calendar endDate) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setResult(int)
	 */
	public void setResult(int result) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setStartDate(java.util.Calendar)
	 */
	public void setStartDate(Calendar startDate) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setState(int)
	 */
	public void setState(int state) {
		// TODO Auto-generated method stub

	}

}
