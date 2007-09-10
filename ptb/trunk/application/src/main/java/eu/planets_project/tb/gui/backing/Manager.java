package eu.planets_project.tb.gui.backing;


import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;


public class Manager {
    
   
    
    public Manager() {
    }
    
    public String initExperimentAction() {
		ExperimentBean expBean = new ExperimentBean();
		// Put Bean into Session; accessible later as #{ExperimentBean}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	    return "success";
    }

    public String updateBasicPropsAction(){
   	    TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
    	Experiment exp = null;
		BasicProperties props = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // if not yet created, create new Experiment object and new Bean
        if ((expBean.getID() == 0)) { 
	        // Create new Experiment
	        exp = new ExperimentImpl();
	        props = new BasicPropertiesImpl();
	        // Get userid info from managed bean
	        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
	        // set current User as experimenter
	        props.setExperimenter(currentUser.getUserid());
	        ExperimentSetup expSetup = new ExperimentSetupImpl();
	        expSetup.setBasicProperties(props);       
	        exp.setExperimentSetup(expSetup);
            long expId = testbedMan.registerExperiment(exp);            
            expBean.setID(expId);
        }
    	exp = testbedMan.getExperiment(expBean.getID());
    	props = exp.getExperimentSetup().getBasicProperties();          
        props.setExperimentName(expBean.getEname());        
        //set the experiment information
        props.setSummary(expBean.getEsummary());
        props.setConsiderations(expBean.getEconsiderations());
        props.setPurpose(expBean.getEpurpose());
        props.setFocus(expBean.getEfocus());
        props.setScope(expBean.getEscope());
        props.setContact(expBean.getEcontactname(),expBean.getEcontactemail(),expBean.getEcontacttel(),expBean.getEcontactaddress());       
        props.setExperimentFormal(expBean.getFormality());
              
        // Workaround
        // update in cached lists
        ListExp listExp_Backing = (ListExp)JSFUtil.getManagedObject("ListExp_Backing");
        listExp_Backing.getExperimentsOfUser();
        listExp_Backing.getAllExperiments();
        
        // Put updated Bean into Session; accessible later as #{ExperimentBean}
        //FacesContext ctx = FacesContext.getCurrentInstance();
	    //ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
        testbedMan.updateExperiment(exp);
        return "success";
    }
    
    public String updateExpTypeAction() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = testbedMan.getExperiment(expBean.getID());
        // add/change attributes
        exp.getExperimentSetup().setExperimentType(Integer.parseInt(expBean.getEtype()));
        
        testbedMan.updateExperiment(exp);
    	return "success";
    }
      
    public String updateWorkflowTypeAction() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Workflow workflow = (Workflow)JSFUtil.getManagedObject("Workflow");
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = testbedMan.getExperiment(expBean.getID());
        // remove old workflow and add newadd/change workflow of this experiment
        ExperimentWorkflow ewf = exp.getExperimentSetup().getExperimentWorkflow();
        if (ewf == null) {
        	ewf = new ExperimentWorkflowImpl(workflow);
        }
        //expBean.getEworkflow()
        //exp.getExperimentSetup().setExperimentType(Integer.parseInt(expBean.getEtype()));
        
        testbedMan.updateExperiment(exp);
    	return "success";
    }
    
    public void changedExpWorkflowEvent(ValueChangeEvent ce) {
    	int id = Integer.parseInt(ce.getNewValue().toString());
    	Workflow wf = null;
    	Iterator iter = WorkflowHandlerImpl.getInstance().getAllWorkflows().iterator();
        while (iter.hasNext()) {
        	wf = (Workflow)iter.next();
        	if (wf.getEntityID()==id)
        		break;
        }
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("Workflow", wf);
    }

    public Map<String,String> getAvailableExperimentTypes() {
        ExperimentTypesImpl types = new ExperimentTypesImpl();    
        
        List<Integer> passtypes = types.getAlLAvailableExperimentTypeIDs();
        TreeMap<String,String> typeMap = new TreeMap<String,String>();
        for (int i=0;i<passtypes.size();i++) {
        	typeMap.put(types.getExperimentTypeName(passtypes.get(i)),String.valueOf(passtypes.get(i)) );
        }
        return typeMap;
    }
    
    public Map<String,String> getAvailableWorkflows() {
    	WorkflowHandlerImpl wfh = WorkflowHandlerImpl.getInstance();
    	TreeMap<String,String> wfMap = new TreeMap<String,String>();
    	Iterator iter = wfh.getAllWorkflows().iterator();
    	while(iter.hasNext()) {
    		Workflow wf = (Workflow)iter.next();
    		wfMap.put(wf.getName(),String.valueOf(wf.getEntityID()));
    	}
    	return wfMap;
    }

    
    
}
