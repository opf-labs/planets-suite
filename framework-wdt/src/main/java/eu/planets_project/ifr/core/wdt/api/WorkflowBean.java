package eu.planets_project.ifr.core.wdt.api;


/**
 * this interface provides a contract for the exectution framework
 * and needs to be implemented by every workflow bean
 * @author Rainer Schmidt 
 */
public interface WorkflowBean {

	//public void setView(String view);	
	//public String getView();
	
	
	/**
	* adds a file to a workflow instance
	* this is general and can be handled by an abstract parent
	* @ param file reference
	*/
	public void addInputData(String localFileRef);
	
	/**
	* resets input files within a workflow instance
	* this is general and can be handled by an abstract parent
	*/	
	public void resetInputData();	
	
	/**
	* triggers a registry lookup by the workflow instance
	* this needs to be called via gui components
	*/		
	public void lookupServices();

	/**
	* resets the list of services available for the workflow instance
	* this needs to be called via gui components
	*/			
	public void resetServices();
	
	/**
	* non-argument workflow exectuion interface
	* triggers a workflow execution
	* invoked via by an execution thread
	*/
	public String invokeService();
	
}