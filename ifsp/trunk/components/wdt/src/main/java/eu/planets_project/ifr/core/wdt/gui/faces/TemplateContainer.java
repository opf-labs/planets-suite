package eu.planets_project.ifr.core.wdt.gui.faces;
				
import java.util.List;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceRef;

import javax.faces.component.*;
import javax.faces.context.FacesContext;
//import javax.faces.context.ExternalContext;
import javax.faces.event.ActionEvent;

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

	private Logger log = Logger.getLogger(this.getClass().getName());	
	private List<WFTemplate> templates = null;
	private WFTemplate currentTemplate = null;


	public TemplateContainer() {
	}
	
	/**
	* @retrun a list of all registered workflo templates
	*/
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
//		templates.add(new WFTemplate("NOP Template", "views/wf.characterization.xhtml", "characterizationWorkflowBean"));
		templates.add(new WFTemplate("Simple Conversion", "views/wf.simpleconvertdemo.xhtml", "demoSimpleConvertBean"));
		templates.add(new WFTemplate("Level-1 Conversion", "views/wf.level1demo.xhtml", "level1ConvertBean"));
		templates.add(new WFTemplate("Droid Identification", "views/wf.droiddemo.xhtml", "droidBean"));
		templates.add(new WFTemplate("Dual Migration", "views/wf.review1.xhtml", "review1Bean"));		
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
			log.fine("current view: "+viewId);
			/*BUG: move this to faces-config*/
			JSFUtil.redirectToView("/displayView.xhtml");
			//-- not shure if I need that
			//WorkflowBean wfBean = (WorkflowBean) JSFUtil.getManagedObject(template.getBeanInstance());
			//wfBean.setView(viewId);
			
		} catch(Exception e) {
			log.severe("Error selecting WFTemplate View "+e);
		}
		return "displayView";
	}
	
	/**
	* @return view page for currently selected workflow 
	*/
	public String getCurrentView() {
		if(currentTemplate == null) return null;
		return currentTemplate.getView();
	}
	
	/**
	* @return for currently selected workflow template
	*/	
	public WFTemplate getCurrentTemplate() {
		return currentTemplate;
	}
	
}
