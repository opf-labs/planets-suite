package eu.planets_project.ifr.core.wdt.gui.faces; 

import java.util.logging.Logger;

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
	
	private Logger logger = Logger.getLogger(this.getClass().getName());	

	public CurrentWorkflowBean() {
	}

	/*
	* @return the currently selected workflowBean implementation
	*/
	protected WorkflowBean getCurrentWorkflowBean() {
		TemplateContainer templateContainer = (TemplateContainer) JSFUtil.getManagedObject("templateContainer");
		WFTemplate currentTemplate = templateContainer.getCurrentTemplate();
		WorkflowBean wfBean = (WorkflowBean) JSFUtil.getManagedObject(currentTemplate.getBeanInstance());
		return wfBean;
	}
	
	/*
	* @return true if a workflowBean is selected
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
	* Remove input data
	*/	
	public void resetInputData() {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.resetInputData();
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