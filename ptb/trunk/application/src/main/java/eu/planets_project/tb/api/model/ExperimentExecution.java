package eu.planets_project.tb.api.model;


import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.model.mockups.Workflow;
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
	 * A temporary helper until the calendar schedule is in place to execute experiments
	 */
	public void executeExperiment() throws Exception;
	
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
	
	public List<String> getExecutionMetadata(URI inputFile);
	public URI getExecutionOutputData(URI inputFile);
	/**
	 * Could capture if the migration/characterisation was performed correctly
	 * @param inputFile
	 * @return
	 */
	public String getExecutionState(URI inputFile);
	
	/**
	 * Returns a Collection of OutputURIs
	 * @return does not contain null values
	 */
	public Collection<URI> getExecutionOutputData();
	//getMapping input,output data
	/**
	 * Gets a Map.Entry containing the InputData URI as key and the outputData URI as value
	 * @param inputFileRef an existing inputFileRef
	 * @return null if the inputFileRef is not found. Map.Entry<Input,Output>: output is null if no URI available
	 */
	public Map.Entry<URI,URI> getExecutionDataEntry(URI inputFileRef);
	/**
	 * Gets all available Entries in the form of key=InputURI, value=OutputURI
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI,URI>> getExecutionDataEntries();
	
	/**
	 * As the ExperimentExecutionPhase may be in progress but the actual execution of workflofs has not been
	 * triggered - this method can be used to verify if the execution has been started.
	 * @return
	 */
	public boolean isExecutionInProgress();
	//public void setExecutionInProgress(boolean bInProgress);
	
	/**
	 * Indicates if an experiment workflow has been executed
	 * @return
	 */
	public boolean isExecuted();
	//public void setExecuted(boolean bExecuted);

}
