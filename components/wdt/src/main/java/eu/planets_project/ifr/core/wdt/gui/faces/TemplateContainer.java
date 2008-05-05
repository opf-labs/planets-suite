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
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.common.faces.JSFUtil;
import eu.planets_project.ifr.core.wdt.impl.wf.WFTemplate;
	
/**
 *    container for workflow templates 
 *
 * @author Rainer Schmidt, ARC
 */
public class TemplateContainer 
	// implements ValueChangeListener
{
	//@WebServiceRef(wsdlLocation="http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl")
	//ServiceRegistryManager_Service service;
	//does not inject...

	private Log log = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	private List<WFTemplate> templates = null;
	private WFTemplate currentTemplate = null;


	public TemplateContainer() {
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
		templates.add(new WFTemplate("Charicterization", "views/wf.characterization.xhtml", "characterizationWorkflowBean"));
		templates.add(new WFTemplate("Simple Convert Bean1", "views/wf.simpleconvertdemo1.xhtml", "demoSimpleConvertBean1"));
//		templates.add(new WFTemplate("ImageMagic", "views/imageMagic.xhtml", "imageMagicBean"));
		return "success-loadTemplates";
	}
	
	/**
 	* returns view page of a template
 	* 
 	* @param event
 	*/
	public String selectTemplate(ActionEvent event) {
		//WFTemplate template = null;
		try {
			UICommand link = (UICommand) event.getComponent();
			currentTemplate = (WFTemplate) link.getValue(); 
			String viewId = currentTemplate.getView();
			log.debug("current view: "+viewId);
			/*BUG: move this to faces-config*/
			JSFUtil.redirectToView("/displayView.xhtml");
			//-- not shure if I need that
			//WorkflowBean wfBean = (WorkflowBean) JSFUtil.getManagedObject(template.getBeanInstance());
			//wfBean.setView(viewId);
			
		} catch(Exception e) {
			log.error("Error selecting WFTemplate View ", e);
		}
		return "displayView";
	}
	
	public String getCurrentView() {
		if(currentTemplate == null) return null;
		return currentTemplate.getView();
	}
	
	public WFTemplate getCurrentTemplate() {
		return currentTemplate;
	}
	
}
