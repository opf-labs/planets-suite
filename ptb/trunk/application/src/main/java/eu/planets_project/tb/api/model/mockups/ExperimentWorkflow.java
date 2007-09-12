package eu.planets_project.tb.api.model.mockups;

import java.net.URI;
import java.util.List;

public interface ExperimentWorkflow{
	
	public List<URI> getInputData();
	public void addInputData(URI fileRef);
	public void addInputData(List<URI> fileRefs);
	public void removeInputData(URI fileRef);
	public void removeInputData(List<URI> fileRefs);
	public void setInputData(List<URI> fileRefs);
	
	public Workflow getWorkflow();
	public void setWorkflow(Workflow workflow);
	
	public List<URI> getOutputData();
	public void setOutputData(List<URI> fileRefs);
	public void addOutputData(URI fileRef);
	public void addOutputData(List<URI> fileRefs);
	
	//public String getWorkflowBPEL();

}
