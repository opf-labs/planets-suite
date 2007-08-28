package eu.planets_project.tb.api.model;

import eu.planets_project.tb.api.services.mockups.ComplexWorkflow;

/**
 * The phase ExperimentSetup covers the steps of 1-3 of the Planets Testbed
 * process model.
 * Step 1 define basic properties;
 * Step 2 design the experiment's workflow (= services, data, configuration)
 * Step 3 specify ressources
 * @author alindley
 *
 */
public interface ExperimentSetup extends ExperimentPhase{

	//Step 1:
	public void setBasicProperties(BasicProperties props);
	public BasicProperties getBasicProperties();

	/**
	 * @param iTypeID
	 * @see model.finals.ExperimentTypes
	 */
	public void setExperimentType(int iTypeID);
	public String getExperimentTypeName();
	public int getExperimentTypeID();
	
	//Step 2:
	/**
	 * Sets the experiment's workflow (= services, data, configuration) 
	 * @param workflow
	 */
	public void setWorkflow(ComplexWorkflow workflow);
	public ComplexWorkflow getExperimentWorkflow();
	
	/**
	 * Sets a list of explored objectives
	 * @param exploredObjectives
	 */
	public void setExperimentedObjectives(ExperimentObjectives exploredObjectives);
	public ExperimentObjectives getExperimentedObjectives();
	
	//Step 3: specify resources
	public void setExperimentResources(ExperimentResources experimentResources);
	public ExperimentResources getExperimentResources();
	


}
