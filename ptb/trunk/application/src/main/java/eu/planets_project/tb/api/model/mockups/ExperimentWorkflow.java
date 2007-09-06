package eu.planets_project.tb.api.model.mockups;

import java.io.File;
import java.util.List;

import eu.planets_project.tb.api.services.mockups.Service;

public interface ExperimentWorkflow{
	
	public List<File> getInputData();
	public void addInputData(File file);
	public void addInputData(List<File> files);
	public void removeInputData(File file);
	public void removeInputData(List<File> files);
	public void setInputData(List<File> files);
	
	public Workflow getWorkflowTemplate();
	public void setWorkflowTemplate(Workflow workflow);
	
	public List<File> getOutputData();
	public void setOutputData(List<File> files);
	public void addOutputData(File file);
	public void addOutputData(List<File> files);
	
	//public String getWorkflowBPEL();

}
