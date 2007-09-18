package eu.planets_project.tb.api.model.mockups;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ExperimentWorkflow{
	
	public Collection<URI> getInputData();
	public void addInputData(URI fileRef);
	public void addInputData(List<URI> fileRefs);
	public void removeInputData(URI fileRef);
	public void removeInputData(List<URI> fileRefs);
	public void setInputData(List<URI> fileRefs);
	
	public Workflow getWorkflow();
	public void setWorkflow(Workflow workflow);
	
	/**
	 * Returns a Collection of OutputURIs
	 * @return does not contain null values
	 */
	public Collection<URI> getOutputData();
	/**
	 * Sets a List of output data for it's corresponding input data
	 * @param fileRefs List<Map.Entry<existingInputURI, OutputURI>>
	 */
	public void setOutputData(Collection<Map.Entry<URI,URI>> ioFileRefs);
	/**
	 * Sets output data URI for a (previously added) input URI
	 * @param inputFileRef
	 * @param outputFileRef
	 */
	public void setOutputData(URI inputFileRef, URI outputFileRef);
	/**
	 * Sets output data for a (previously added) input URI
	 * @param ioFileRef Map.Entry<InputData, OutputData>
	 */
	public void setOutputData(Map.Entry<URI, URI> ioFileRef);
	
	//getMapping input,output data
	/**
	 * Gets a Map.Entry containing the InputData URI as key and the outputData URI as value
	 * @param inputFileRef an existing inputFileRef
	 * @return null if the inputFileRef is not found. Map.Entry<Input,Output>: output is null if no URI available
	 */
	public Map.Entry<URI,URI> getDataEntry(URI inputFileRef);
	/**
	 * Gets all available Entries in the form of key=InputURI, value=OutputURI
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI,URI>> getDataEntries();
	
	//public String getWorkflowBPEL();

}
