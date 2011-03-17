package eu.planets_project.ifr.core.wdt.gui.faces;

import java.util.logging.Logger;

import eu.planets_project.ifr.core.wdt.gui.faces.CurrentWorkflowBean;
import eu.planets_project.ifr.core.wdt.common.faces.JSFUtil;

/**
	* Executes a workflow bean if initiated via the web application.
	* @author Rainer Schmidt
	*/
	public class BeanExecutor {
		
		private Logger logger = Logger.getLogger(this.getClass().getName());	
		
		private CurrentWorkflowBean wfBean = (CurrentWorkflowBean) JSFUtil.getManagedObject("currentWorkflowBean");	
		
		//start a new runnable to detach from browser session
		//startup listener
		public String invokeService() {
			logger.fine("beanExecutor: invokeService called");
			String ret = wfBean.invokeService();
			return ret;
		}
		
		//remove input files from bean
		public String resetInputData() {
			logger.fine("beanExecutor: resetInputData called");
			if(wfBean != null) wfBean.resetInputData();			
			return "resetInputData";
		}
		
		//remove input files from bean
		public String resetWorkflow() {
			logger.fine("beanExecutor: resetWorkflow called");
			JSFUtil.invalidateSession();
			return "invalidated";
		}
		
		public String getStatus() {
			return null;
		}
		
		public String getReport() {
			return null;
		}
				
	}