package eu.planets_project.ifr.core.wdt.gui.faces;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.gui.faces.CurrentWorkflowBean;
import eu.planets_project.ifr.core.wdt.common.faces.JSFUtil;

/**
	* Executes a workflow bean if initiated via the web application.
	* @author Rainer Schmidt
	*/
	public class BeanExecutor {
		
		private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
		
		private CurrentWorkflowBean wfBean = (CurrentWorkflowBean) JSFUtil.getManagedObject("currentWorkflowBean");	
		
		//start a new runnable to detach from browser session
		//startup listener
		public String invokeService() {
			logger.debug("beanExecutor: invokeService called");
			String ret = wfBean.invokeService();
			return ret;
		}
		
		public String getStatus() {
			return null;
		}
		
		public String getReport() {
			return null;
		}
				
	}