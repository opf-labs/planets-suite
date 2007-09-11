package eu.planets_project.tb.api.model.mockups;

import java.io.File;
import java.util.List;

import eu.planets_project.tb.api.services.mockups.Service;

public interface Workflow {
	
	public long getEntityID();

	public void setName(String sWorkflowName);
	public String getName();
	
	/**
	 * @return String representing the required mime-type of input information
	 */
	public List<String> getRequiredInputMIMETypes();
	public void addRequiredInputMIMEType(String sMimeType);
	public void removeRequiredInputMIMEType(String sMimeType);
	
	/**
	 * @return String representing the required mime-type of output information
	 */
	public List<String> getRequiredOutputMIMETypes();
	public void addRequiredOutputMIMEType(String sMimeType);
	public void removeRequiredOutputMIMEType(String sMimeType);
	
	public Service getWorkflowService(int iPosition);
	/**
	 * Contains all Services in the order of their occurrence 
	 * i.e. List.getItem(0) represents the starting service
	 * Currently only a flat structure is supported - No forks and junctions. 
	 * @return 
	 */
	public List<Service> getWorkflowServices();
	public void addWorkflowService(int iPosition, Service service);
	public void removeWorkflowService(int iPosition);
	
	/**The Tool Type will specify for example a "jpeg2pdfMigration" experiment – but does not contain
	 * any reference to actual tools instances, which is part of the Design Experiment stage.
	 * @param toolTypes: requires to be in the format which is accepted and known by the service registry
	 **/
	public String getToolType();
	public void setToolType(String sToolType);
	
	public void setExperimentType(String sExperimentTypeID);
	public String getExperimentType();
	public String getExpeirmentTypeName();
	
	public boolean isValidWorkflow();
}
