package eu.planets_project.tb.api.model.mockups;


import java.net.URI;
import java.util.List;


public interface ExperimentWorkflow{
	
	public List<URI> getInputData();
	public void addInputData(URI uri);
	public void addInputData(List<URI> uris);
	public void removeInputData(URI uri);
	public void removeInputData(List<URI> uris);
	public void setInputData(List<URI> uris);
	
	public Workflow getWorkflow();
	public void setWorkflow(Workflow workflow);
	
	public List<URI> getOutputData();
	public void setOutputData(List<URI> uris);
	public void addOutputData(URI uri);
	public void addOutputData(List<URI> uris);
	
	//public String getWorkflowBPEL();

}
