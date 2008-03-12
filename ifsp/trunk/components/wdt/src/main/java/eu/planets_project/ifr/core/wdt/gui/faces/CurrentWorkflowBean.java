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
 	*/
public class CurrentWorkflowBean implements WorkflowBean {
	
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	

	public CurrentWorkflowBean() {
	}

	public WorkflowBean getCurrentWorkflowBean() {
		TemplateContainer templateContainer = (TemplateContainer) JSFUtil.getManagedObject("templateContainer");
		WFTemplate currentTemplate = templateContainer.getCurrentTemplate();
		WorkflowBean wfBean = (WorkflowBean) JSFUtil.getManagedObject(currentTemplate.getBeanInstance());
		return wfBean;
	}
	
	/*
	* Inject services
	*/
	public void lookupServices() {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.lookupServices();
	}	
	
	/*
	* Inject input data
	*/	
	public void addInputData(String localFileRef) {
		WorkflowBean wfBean = getCurrentWorkflowBean();
		wfBean.addInputData(localFileRef);
	}
	
}