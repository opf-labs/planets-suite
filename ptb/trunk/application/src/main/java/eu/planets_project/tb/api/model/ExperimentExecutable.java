package eu.planets_project.tb.api.model;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * @author Andrew Lindley, ARC
 * An object implementing this interface contains all bits of information that are
 * required for service execution. This object is handed over to the service execution
 * and results are written back to it. 
 * i.e. corresponds to the idea of an executable part of a preservatio plan
 */
public interface ExperimentExecutable{
	
	/**
	 * Returns a List of local file refs e.g. C:/DATA/text1.dco
	 * @return
	 */
	public Collection<String> getInputData();
	public void addInputData(String localFileRef);
	public void addInputData(Collection<String> localFileRefs);
	public void removeInputData(String localFileRef);
	public void removeInputData(Collection<String> localFileRefs);
	public void setInputData(Collection<String> localFileRefs);
	
	/**
	 * Takes a local file ref and hands over its exposed http reference
	 * @param localFileRef
	 * @return
	 */
	public URI getInputHttpFileRef(String localFileRef);
	public Collection<URI> getAllMigrationOutputHttpData();
	public Collection<String> getAllCharacterisationOutputHttpData();
	public URI getOutputHttpFileRef(String localFileRef);
	public Collection<URI> getAllInputHttpDataEntries();
	
	/**
	 * A TBServiceTemplate is produced by the Testbed administrator role and
	 * mainly contains 
	 * @return
	 */
	public TestbedServiceTemplate getServiceTemplate();
	public void setServiceTemplate(TestbedServiceTemplate template);
	
	/**
	 * As a ServiceTemplate may contain 1..n ServoceOperations it is necessary
	 * to specify which ServiceOperation to use. It's name is a unique identifier within a ServiceTemplate
	 * @param sOperationName
	 */
	public void setSelectedServiceOperationName(String sOperationName);
	public String getSelectedServiceOperationName();
	
	/**
	 * Returns a Collection of OutputURIs
	 * @return does not contain null values
	 */
	public Collection<String> getOutputData();
	/**
	 * Creates a mapping of output data for it's corresponding input data
	 * @param fileRefs List<Map.Entry<existingInputFileRef, OutputFileRef>>
	 */
	public void setOutputData(Collection<Map.Entry<String,String>> ioFileRefs);
	/**
	 * Sets output data file ref for a (previously added) input file ref
	 * @param inputFileRef
	 * @param outputFileRef
	 */
	public void setOutputData(String inputFileRef, String outputFileRef);
	/**
	 * Sets output data for a (previously added) input file ref
	 * @param ioFileRef Map.Entry<InputData, OutputData>
	 */
	public void setOutputData(Map.Entry<String, String> ioFileRef);
	
	//getMapping input,output data
	/**
	 * Gets all available Entries in the form of key=InputLocalRef, value=OutputLocalRef
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<String,String>> getMigrationDataEntries();
	/**
	 * Gets all available Entries in the form of key=InputURI, value=OutputURI
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI, URI>> getMigrationHttpDataEntries();
	/**
	 * Gets a certain inputURI,outputURI result for a given localFileRef
	 * @see getDataEntry in form of URI, URI as return value
	 * @return
	 */
	public Map.Entry<URI, URI> getMigrationHttpDataEntry(String localFileRef);
	/**
	 * Gets all available Entries in the form of key=InputLocalRef, value=OutputLocalRef
	 * Note: Characterisation local input file ref maps to output string (not file!) 
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<String,String>> getCharacterisationDataEntries();
	/**
	 * Gets all available Entries in the form of key=InputURI, value=OutputURI
	 * Note: Characterisation local input maps to output string (not file!) 
	 * @see getDataEntry for return values.
	 * @return
	 */
	public Collection<Map.Entry<URI, String>> getCharacterisationHttpDataEntries();
	
	/**
	 * Gets a certain inputURI,outputURI result for a given localFileRef
	 * Note: Characterisation local input maps to output string (not file!) 
	 * @see getDataEntry in form of URI, URI as return value
	 * @return
	 */
	public Map.Entry<URI, String> getCharacterisationHttpDataEntry(String localFileRef);
	
	/**
	 * An indicator if the service invocation has been triggered
	 */
	public boolean isExecutableInvoked();
	public void setExecutableInvoked(boolean b);
	
	/**
	 * An indicator if the service invocation has terminated
	 * @return
	 */
	public boolean isExecutionCompleted();
	public void setExecutionCompleted(boolean b);
	
	/**
	 * An indicator if the service invocation was completed successfully 
	 */
	public boolean isExecutionSuccess();
	public void setExecutionSuccess(boolean b);
	

}
