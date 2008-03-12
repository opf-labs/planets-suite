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
		
		protected String[] inputData = null;
    
    public AbstractWorkflowBean(){   
    }
    
    public void addInputData(String localFileRef) {
    	logger.debug("Experiment Input Data - added: " + localFileRef);
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
}