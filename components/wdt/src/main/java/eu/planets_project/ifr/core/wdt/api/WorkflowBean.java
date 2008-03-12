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
	//this needs to be called by via a gui component
	public void lookupServices();
}