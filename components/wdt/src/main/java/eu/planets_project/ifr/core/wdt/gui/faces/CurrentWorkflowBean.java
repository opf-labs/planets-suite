package eu.planets_project.ifr.core.wdt.gui.faces; 

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.common.faces.JSFUtil;
import eu.planets_project.ifr.core.wdt.gui.faces.TemplateContainer;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.impl.wf.WFTemplate;

/**
 	* @author Rainer Schmidt
 	* wrapper class providing access to the currently loaded workflow
 	* don't put any other logic here!
 	*/
public class CurrentWorkflowBean implements WorkflowBean {
	
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	

	public CurrentWorkflowBean() {
	}

	/*
	* returns the currently selected workflowBean implementation
	*/
	protected WorkflowBean getCurrentWorkflowBean() {
		TemplateContainer templateContainer = (TemplateContainer) JSFUtil.getManagedObject("templateContainer");
		WFTemplate currentTemplate = templateContainer.getCurrentTemplate();
		WorkflowBean wfBean = (WorkflowBean) JSFUtil.getManagedObject(currentTemplate.getBeanInstance());
		return wfBean;
	}
	
	/*
	* returns true if a workflowBean is selected
	*/
	public boolean isWorkflowBeanSelected() {
		TemplateContainer templateContainer = (TemplateContainer) JSFUtil.getManagedObject("templateContainer");
		if(templateContainer.getCurrentTemplate() == null) return false;
		return true;
	}
		
	/*
	* Inject input data
	*/	
	public void addInputData(String localFileRef) {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.addInputData(localFileRef);
	}
	
	/*
	* Inject services
	*/
	public void lookupServices() {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.lookupServices();
	}	

	/*
	* Reset services
	*/
	public void resetServices() {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.resetServices();
	}
	
	/*
	* Execute service method
	*/
	public String invokeService() {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		return wfBean.invokeService();
	}
}