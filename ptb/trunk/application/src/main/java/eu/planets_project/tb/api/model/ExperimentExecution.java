package eu.planets_project.tb.api.model;


import java.util.Calendar;

import eu.planets_project.tb.api.system.SystemMonitoring;

/**
 * @author alindley
 * The interface for Experiment Execution covers step 5 of the Testbed workflow
 */
public interface ExperimentExecution extends ExperimentPhase{
	
	//TODO: check if experimentExecution Phase has correct properties
	
	public void setScheduledExecutionDate(long millis);
	public void setScheduledExecutionDate(Calendar date);
	public Calendar getScheduledExecutionDate();
	
	/**
	 * @param systemState
	 */
	public void setSystemMonitoringData(SystemMonitoring systemState);
	/**
	 * This SystemMonitoring Object captures and returns the state of the machine the Testbed is running on.
	 * this object contains e.g. CPU usage, memory available, etc.
	 * @return
	 */
	public SystemMonitoring getSystemMonitoringData();
	
	/**
	 * @param wee_Information
	 */
	//TODO Schauen 
	/*public void setWorkflowExecutionData(WorkflowExecution wee_Information);
	public WorkflowExecution getWorkflowExecutionData();*/
	
	//Unclear in which form "experiment results" are packed and from where to retrieve them
	//possibly from the WEE context
	public void setExperimentResults(ExperimentResults expResults);
	public ExperimentResults getExperimentResults();

}
