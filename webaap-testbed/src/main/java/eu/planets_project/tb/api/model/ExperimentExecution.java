/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.api.model;


import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.system.mockup.SystemMonitoring;

/**
 * @author alindley
 * The interface for Experiment Execution covers step 5 of the Testbed workflow
 * 
 * Note that this interface takes and delivers URIs and no local file Refs (as the ExperimentExecutable)
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
	
	public List<String> getExecutionMetadata(URI inputFile);

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
	public Collection<URI> getMigrationOutputData();
	
	/**
	 * Returns a Collection of Output Characterisation Strings
	 * @return does not contain null values
	 */
	public Collection<String> getCharacterisationOutputData();
	
	//getMapping input,output data
	/**
	 * Gets all available Migration resultsin the form of Entry key=InputURI, value=OutputURI
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI,URI>> getMigrationOutputDataEntries();
	
	/**
	 * Gets a Map.Entry containing the InputData URI as key and the outputData URI as value
	 * @see getDataEntry in form of URI, URI as return value
	 * @return
	 */
	public Map.Entry<URI, URI> getMigrationOutputDataEntry(URI inputFileURI);
	
	/**
	 * Gets all available Characterisation resultsin the form of Entry key=InputURI, value=String
	 * Note: Characterisation output maps to String (not file!) 
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI, String>> getCharacterisationOutputDataEntries();
	
	/**
	 * Gets a Map.Entry containing the InputData URI as key and the outputData characterisation String as value
	 *  Note: Characterisation output maps to String (not file!) 
	 * @see getDataEntry in form of URI, String as return value
	 * @return
	 */
	public Map.Entry<URI, String> getCharacterisationOutputDataEntry(URI inputFileURI);
	
	/**
	 * Returns the ServiceOperation of the experiment's executable part
	 * This contains data about: operation name, max. supported files, etc.
	 * @return
	 */
	public TestbedServiceTemplate.ServiceOperation getselectedTBServiceTemplateOperation();
	
	/**
	 * Returns the Service of the experiment's executable part
	 * This contains information about: description, all operations, etc.
	 * @return
	 */
	public TestbedServiceTemplate getSelectedTBServiceTemplate();
	
	/**
	 * Returns the executable part of the experiment
	 * @return
	 */
	public ExperimentExecutable getExperimentExecutable();
	
	/**
	 * Building request, invoking service, parsing results, etc. was performed
	 * successfully
	 * @return
	 */
	public boolean isExecutionSuccess() ;
	/**
	 * service invocation was completed - does not tell anything about its success
	 * @return
	 */
	public boolean isExecutionCompleted();
	/**
	 * service invocation has started
	 * @return
	 */
	public boolean isExecutionInvoked();
	
	/**
	 * Returns the experiment execution's enddate
	 * @return
	 */
	public Calendar getExecutionEndedDate();
	
	/**
	 * Returns the experiment execution's startdate
	 * @return
	 */
	public Calendar getExecutionStartedDate();

}
