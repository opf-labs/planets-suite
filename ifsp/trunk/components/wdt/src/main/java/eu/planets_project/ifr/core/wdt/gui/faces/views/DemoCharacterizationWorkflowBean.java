package eu.planets_project.ifr.core.wdt.gui.faces.views;

import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

//import javax.xml.namespace.QName;

import javax.faces.component.*;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
	
/**
 *    characterization workflow bean 
 *
 *    demonstrates a workflow comprising a characterization followed by a migration
 *
 * @author Rainer Schmidt, ARC
 */
public class DemoCharacterizationWorkflowBean {

	private Log log = PlanetsLogger.getLogger(this.getClass(), "resources/log/sample-log4j.xml");	
	
	private List<SelectItem> charServices = null;
	private List<SelectItem> migServices = null;
	private SelectItem currentCharService = null;
	private SelectItem currentMigService = null;

	public DemoCharacterizationWorkflowBean() {
		loadServices();
	}

	/**
	* loads workflow templates from data storage
	*/
	public String loadServices() {
		charServices = new ArrayList<SelectItem>();
		charServices.add( new SelectItem("please choose a service") );
		charServices.add( new SelectItem("http://myCharacterization.com/service1") );
		charServices.add( new SelectItem("http://myCharacterization.com/service2") );
		charServices.add( new SelectItem("http://myCharacterization.com/service3") );
		currentCharService = charServices.get(0);
		return "success-loadServices";
	}
		
	public List<SelectItem> getCharServices() {
		return charServices;
	}
	
	public void setCharServies(List services) {
		this.charServices = services;
	}	
	
	public List<SelectItem> getMigServices() {
		return migServices;
	}
	
	public void setLinkList(List services) {
		this.migServices = services;
	}
	
	public String getCurrentCharService() {
		String service = (String) currentCharService.getValue();
		return service;
	}
	
	//public void setCurrentCharService(SelectItem charService) {
	//	this.currentCharService = charService;
	//}
	
}
