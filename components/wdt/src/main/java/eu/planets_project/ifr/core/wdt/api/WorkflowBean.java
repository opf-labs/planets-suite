package eu.planets_project.ifr.core.wdt.api;


/**
 * this interface provides a contract for the exectution framework
 * and needs to be implemented by every workflow bean
 * @author Rainer Schmidt 
 */
public interface WorkflowBean {

	//public void setView(String view);	
	//public String getView();
	
	//this is general and can be handled by an abstract parent
	public void addInputData(String localFileRef);
	
	//these need to be called via gui components
	public void lookupServices();
	public void resetServices();
	
	//non-argument Planets services interface
	//invoked via by an execution thread
	public String invokeService();
	
}