package eu.planets_project.ifr.core.wdt.impl.wf;

import java.util.List;
import java.util.ArrayList;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.impl.registry.Service;


/**
* provides a base class for implementing workflow beans
* partly implements the interface WorkflowBean
* @author Rainer Schmidt
*/ 

public abstract class AbstractWorkflowBean {

	
		private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
		
		//this will be a collection of data model instances
		protected List<String> inputData = null;
    
    public AbstractWorkflowBean(){   
    	inputData = new ArrayList<String>();
    }
    
    public void addInputData(String pdm) {
    	logger.debug("Experiment Input Data - added: " + pdm);
    	inputData.add(pdm);    	
    }
    
    public String[] getInputData() {
    	return inputData.toArray(new String[0]);
    }
    
    protected List<SelectItem> toSelectItem(List<Service> services) {   
    	List<SelectItem> ret = new ArrayList<SelectItem>();    	
    	for(int i=0; i<services.size(); i++ ) {
    		Service service = services.get(i);
    		//value must be a string - that seems to be a bug
    		//ret.add(new SelectItem(service, "label#"+i));
				ret.add(new SelectItem(service.getId(), service.getName(), service.getEndpoint()));
    	}
    	return ret;
    }
    
    protected Service getService(SelectItem item, List<Service> services) {
    	//asdf; remember the services after lookup
    	for( Service service : services) {
    		if(service.getId().equals(item.getValue())) return service;
    	}
    	return null;
    }
    
    /**
		* Planets Service Interface
		*/
		public String invokeService(String pdm) {
			logger.debug("charakterization workflow started with input: "+pdm);
			this.addInputData(pdm);
			this.invokeService();
			return pdm;		
		}
		
		public abstract String invokeService();
		
}