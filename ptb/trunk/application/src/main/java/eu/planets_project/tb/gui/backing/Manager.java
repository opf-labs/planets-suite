package eu.planets_project.tb.gui.backing;


import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Manager {
    
	private Log log = LogFactory.getLog(Manager.class);
    
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
        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
        testbedMan.updateExperiment(exp);
        return "success";
    }
    
    public String updateBenchmarksAndSubmitAction() {
    	if (this.updateBenchmarksAction() == "success") {
        	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        	Experiment exp = testbedMan.getExperiment(expBean.getID());
        	
        	testbedMan.updateExperiment(exp);        	
    		return "goToStage4";
    	} else
    		return "goToStage4";    	
    }
    
    
    public String updateBenchmarksAction(){
   	    // create bm-goals
    	
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID());
    	// create Goal objects from Beans
    	List<BenchmarkGoal> bmgoals = new ArrayList<BenchmarkGoal>();
    	Iterator iter = expBean.getBenchmarks().values().iterator();
    	while (iter.hasNext()) {
    		BenchmarkBean bmb = (BenchmarkBean)iter.next();
    		if (bmb.getSelected()) {
	    		// method to get a new instance of BenchmarkGoal
    			BenchmarkGoal bmg = BenchmarkGoalsHandlerImpl.getInstance().getBenchmarkGoal(bmb.getID());
	    		bmg.setValue(bmb.getValue());
	    		if (bmb.getWeight()!=null)
	    			bmg.setWeight(Integer.parseInt(bmb.getWeight()));
	    		bmgoals.add(bmg);
    		}
    	}
    	exp.getExperimentSetup().setBenchmarkGoals(bmgoals);
    	ExperimentResources expRes = exp.getExperimentSetup().getExperimentResources();
    	if (expRes == null)
    		expRes = new ExperimentResourcesImpl();
    	expRes.setIntensity(Integer.parseInt(expBean.getIntensity()));
    	expRes.setNumberOfOutputFiles(Integer.parseInt(expBean.getNumberOfOutputFiles()));
    	exp.getExperimentSetup().setExperimentResources(expRes);
    	exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
    	exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
    	testbedMan.updateExperiment(exp);
        return "goToStage4";
    }
    
    
   /* public String updateExpTypeAction() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = testbedMan.getExperiment(expBean.getID());
        // add/change attributes
        exp.getExperimentSetup().setExperimentType(Integer.parseInt(expBean.getEtype()));
        
        testbedMan.updateExperiment(exp);
    	return "success";
    }*/
      
    public String saveExpWorkflowAction()  {
        try {
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
	        exp.getExperimentSetup().setExperimentType(expBean.getEtype());
	        FileUploadBean uploadBean = (FileUploadBean)JSFUtil.getManagedObject("FileUploadBean");
	        uploadBean.upload();
	        ewf.addInputData(uploadBean.getURI());
	        ewf.setWorkflow(workflow);
	        exp.getExperimentSetup().setWorkflow(ewf);
	        expBean.setEworkflow(ewf);
	        expBean.setEworkflowInputData(uploadBean.getURI().toString());
	        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
	        testbedMan.updateExperiment(exp);
	        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow");    
	    	return "goToStage3";
        } catch (Exception e) {
        	log.error("Exception when trying to create/update ExperimentWorkflow: "+e.toString());
        	return "failure";
        }
    }
    
    public void changeExpWorkflowDataAction() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.setEworkflowInputData(null);    	
    }
    
    public void changedExpWorkflowEvent(ValueChangeEvent ce) {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	expBean.setWorkflowTypeId(ce.getNewValue().toString());
    	this.changedExpWorkflow();
    }
    
    public void changedExpWorkflow() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Workflow wf = WorkflowHandlerImpl.getInstance().getWorkflow(Long.parseLong(expBean.getWorkflowTypeId()));
    	FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("Workflow", wf);    	
    }
    
    public void changedExpTypeEvent(ValueChangeEvent ce) {
    	String id = ce.getNewValue().toString();  
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	expBean.setEtype(id);
    	//updateExpTypeAction();
    	changedExpType();
    }
    
    // when type is changed, remove workflow etc. from session 
    public void changedExpType() {
    	//reset workflow-display
    	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow");    	
    }
    

    public Map<String,String> getAvailableExperimentTypes() {
        // for display with selectonemenu-component, we have to flip key and value of map
    	Map oriMap = AdminManagerImpl.getInstance().getExperimentTypeIDsandNames();
    	Map<String,String> expTypeMap = new HashMap<String,String>();
    	Iterator iter = oriMap.keySet().iterator();
    	while(iter.hasNext()){
    		String key = (String)iter.next();
    		expTypeMap.put((String)oriMap.get(key), key);
    	}
    	return expTypeMap;
    }
    
    public Map<String,String> getAvailableWorkflows() {
    	WorkflowHandlerImpl wfh = WorkflowHandlerImpl.getInstance();
    	TreeMap<String,String> wfMap = new TreeMap<String,String>();
       	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Iterator iter = wfh.getAllWorkflows(expBean.getEtype()).iterator();
    	while(iter.hasNext()) {
    		Workflow wf = (Workflow)iter.next();
    		wfMap.put(wf.getName(),String.valueOf(wf.getEntityID()));
    	}
    	return wfMap;
    }
    
    public String approveExperiment(){
        return "goToStage5";
    }
    public String proceedToEvaluation(){
        return "goToStage6";
    }
   
    
}
