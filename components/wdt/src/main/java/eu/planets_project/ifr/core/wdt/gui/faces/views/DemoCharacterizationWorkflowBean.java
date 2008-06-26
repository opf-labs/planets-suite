package eu.planets_project.ifr.core.wdt.gui.faces.views;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.net.MalformedURLException;
import java.net.URL;

//import javax.xml.namespace.QName;
import javax.faces.component.*;
import javax.faces.model.SelectItem;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.api.L2PlanetsService;
//import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.impl.wf.AbstractWorkflowBean;
import eu.planets_project.ifr.core.wdt.impl.registry.Service;
import eu.planets_project.ifr.core.wdt.impl.registry.ServiceRegistry;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
	
/**
 *    characterization workflow bean 
 *
 *    demonstrates a workflow comprising a characterization followed by a migration
 *
 * @author Rainer Schmidt, ARC
 */
public class DemoCharacterizationWorkflowBean extends AbstractWorkflowBean implements L2PlanetsService, WorkflowBean {
	
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	
	private String viewId = null;
	
	private List<SelectItem> charServices = null;
	private List<SelectItem> migServices = null;
	private SelectItem currentCharService = null;
	private SelectItem currentMigService = null;
	
	private ServiceRegistry registry = null;
	
	public DemoCharacterizationWorkflowBean() {
		super();
		//get a registry url from a .properties file 
		registry = new ServiceRegistry();
		this.resetServices();
	}

	/**
	* loads services from a service registry 
	*/
	public void lookupServices() {
		
		this.resetServices();
		//registry lookup...
		charServices.addAll( (Collection)toSelectItem(registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/characterisation",null))) );
		migServices.addAll( (Collection)toSelectItem(registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/migration",null))) );		
	}
	
	/**
	* removes service urls
	*/
	public void resetServices() {
		charServices = new ArrayList<SelectItem>();
		charServices.add( new SelectItem("please choose a service") );
		currentCharService = charServices.get(0);
		migServices = new ArrayList<SelectItem>();
		migServices.add( new SelectItem("please choose a service") );
		currentMigService = charServices.get(0);		
	}
	
	/**
	* adds a service for a user interface component
	* @param uiID HTML id of a workflow view component
	* @param endpoint Web service endpoint that should be added 
	*/
	//public void addService(String uiId, String endpoint) {
	//	if(uiID.equals(CHAR_SERVICE))
	//}
	//...or maybe something more generic
	//public void addService(String endpoint) 
	//...or something like addMigService(url) that can be directly included in view
	
	//public void setCharServies(List services) {
	//	this.charServices = services;
	//}
		
	public List<SelectItem> getCharServices() {
		return charServices;
	}
	
	public String getCurrentCharService() {
		String service = (String) currentCharService.getValue();
		return service;
	}	
	
	public void toggleCharServices(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : charServices ) {
    	if( indexService.getValue().toString().equals(selectedService)) currentCharService = indexService;
    }
    logger.debug("currentCharService: " + currentCharService.getValue().toString() );
	}
	
	//public void setMigServies(List services) {
	//	this.migServices = services;
	//}
		
	public List<SelectItem> getMigServices() {
		return migServices;
	}
	
	public String getCurrentMigService() {
		String service = (String) currentMigService.getValue();
		return service;
	}	
	
	public void toggleMigServices(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : migServices ) {
    	if( indexService.getValue().toString().equals(selectedService)) currentMigService = indexService;
    }
    logger.debug("currentMigService: " + currentMigService.getValue().toString() );
	}
	
	/*public void addInputData(String localFileRef) {
		super.addInputData(localFileRef);
	}*/
	
	/* Interface: WorkflowBean
	public void setView(String view) {
		this.viewId = view;
	}
	
	public String getView() {
		return this.viewId;
	}
	*/

	
	/**
	* non-argument service interface
	*/
	public String invokeService() {
		return null;
	}
}
