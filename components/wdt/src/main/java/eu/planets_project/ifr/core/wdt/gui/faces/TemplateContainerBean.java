package eu.planets_project.ifr.core.wdt.gui.faces;

import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceRef;

import javax.faces.component.*;
import javax.faces.context.FacesContext;
//import javax.faces.context.ExternalContext;
import javax.faces.application.ViewHandler;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.common.services.ServiceRegistryManager_Service;
import eu.planets_project.ifr.core.wdt.common.services.ServiceRegistryManager;
import eu.planets_project.ifr.core.wdt.common.services.JAXRException_Exception;
	
/**
 *    container for workflow templates 
 *
 * @author Rainer Schmidt, ARC
 */
public class TemplateContainerBean 
	// implements ValueChangeListener
{
	//@WebServiceRef(wsdlLocation="http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl")
	//ServiceRegistryManager_Service service;
	//does not inject...

	private Log log = PlanetsLogger.getLogger(this.getClass(), "resources/log/sample-log4j.xml");	
	private List<WFTemplate> templates = null;


	public TemplateContainerBean() {
	}
	
	public List<WFTemplate> getTemplates() {
		return templates;
	}
	
	public void setTemplates(List templateList) {
		this.templates = templates;
	}
	
		/**
	* loads workflow templates from data storage
	*/
	public String loadTemplates() {
		templates = new ArrayList<WFTemplate>();
		templates.add(new WFTemplate("Charicterization", "/views/wf.characterization.jsp"));
		templates.add(new WFTemplate("Tiff2jpg", "/tiff2jpg.view"));
		templates.add(new WFTemplate("ImageMagic", "/imageMagic.view"));
		return "success-loadTemplates";
	}
	
	/**
 	* returns view page of a template
 	* 
 	* @param event
 	*/
	public void selectView(ActionEvent event) {
		WFTemplate template = null;
		try {
			UICommand link = (UICommand) event.getComponent();
			template = (WFTemplate) link.getValue(); 
			//not working internallly
			//FacesContext context = FacesContext.getCurrentInstance();
			//ExternalContext extContext = context.getExternalContext();
			//extContext.dispatch(template.getView()); 
			String viewId = template.getView();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String currentViewId = facesContext.getViewRoot().getViewId();
			
			if (viewId != null && (!viewId.equals(currentViewId))) {
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
				UIViewRoot viewRoot = viewHandler.createView(facesContext, viewId);
				facesContext.setViewRoot(viewRoot);
				facesContext.renderResponse();
			} 
		} catch(Exception e) {
			log.error("Error selecting WFTemplate View ", e);
		}
	}
	
	/**
	* tests the services registry
	*/
	public String testRegistry() {
		templates = new ArrayList<WFTemplate>();
		try {
			ServiceRegistryManager_Service service = new ServiceRegistryManager_Service(new URL("http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"), new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
			ServiceRegistryManager registry = service.getServiceRegistryManagerPort();
			registry.configure("admin", "admin");
			//registry.saveService("cService", "http://www.myCharacterizationService.org/?wsdl");			
			//String sLocation = registry.findServices("cService");
			//log.debug("Found Service at:"+sLocation);
		} catch(Exception e) {
			log.error("Error testing registry: ", e);
		}

		return "success-testRegistry";
	}

	
	
	public class WFTemplate {
		
		private String name = null;
		private String view = null;
		
		public WFTemplate() {
		}
		
		public WFTemplate(String name, String view) {
			this.name = name;
			this.view = view;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setView(String view) {
			this.view = view;
		}
		
		public String getView() {
			return view;
		}
		
		public String toString() {
			return "wf:"+name;
		}		
	}

}
