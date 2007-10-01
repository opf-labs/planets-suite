package eu.planets_project.tb.gui.backing;


import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.ExperimentReportImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputTextarea;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Manager {
    
	private Log log = LogFactory.getLog(Manager.class);
        private HtmlInputTextarea ereport;
    
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
        // remove workflow object from session, if still available from previously viewed experiment
    	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow"); 
    	
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
    	Experiment exp = null;
		BasicProperties props = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // create message for duplicate name error message
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Experiment name already in use! - Please specify a unique name.");
        fmsg.setSummary("Duplicate name: Experiment names must be unique!");
        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        // if not yet created, create new Experiment object and new Bean
        if ((expBean.getID() == 0)) { 
	        // Create new Experiment
	        exp = new ExperimentImpl();
	        props = new BasicPropertiesImpl();
	        // Get userid info from managed bean
	        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
	        // set current User as experimenter
	        props.setExperimenter(currentUser.getUserid());
		    try {
		        props.setExperimentName(expBean.getEname());        
		    } catch (InvalidInputException e) {
	    		// add message-tag for duplicate name
		        FacesContext ctx = FacesContext.getCurrentInstance();
		        ctx.addMessage("ename",fmsg);
	    		return "failure";
	    	}
	        ExperimentSetup expSetup = new ExperimentSetupImpl();
	        expSetup.setBasicProperties(props);       
	        exp.setExperimentSetup(expSetup);
            long expId = testbedMan.registerExperiment(exp);            
            expBean.setID(expId);
        }
	    exp = testbedMan.getExperiment(expBean.getID());
        props = exp.getExperimentSetup().getBasicProperties();          
	    try {
	        props.setExperimentName(expBean.getEname());        
	    } catch (InvalidInputException e) {
    		// add message-tag for duplicate name
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.addMessage("ename",fmsg);
    		return "failure";
    	}
        //set the experiment information
        props.setSummary(expBean.getEsummary());
        props.setConsiderations(expBean.getEconsiderations());
        props.setPurpose(expBean.getEpurpose());
        props.setFocus(expBean.getEfocus());
        props.setScope(expBean.getEscope());
        props.setContact(expBean.getEcontactname(),expBean.getEcontactemail(),expBean.getEcontacttel(),expBean.getEcontactaddress());       
        props.setExperimentFormal(expBean.getFormality());

        props.setExternalReferenceID(expBean.getExid());
                
        String litRefDesc = expBean.getLitRefDesc();
        String litRefURI = expBean.getLitRefURI();     
	    List<String[]> refList = new ArrayList<String[]>();
        if (litRefDesc != null && !litRefDesc.equals("")) {
	    	refList.add(new String[]{litRefDesc,litRefURI});
        }
        try {
        	props.setLiteratureReference(refList);
        } catch (InvalidInputException e) {
        	log.error("Problems setting literature references: "+e.toString());
        }
        List<Long> refs = new ArrayList<Long>();
        if (expBean.getEref() != null && !expBean.getEref().equals(""))
        	refs.add(expBean.getEref());
        props.setExperimentReferences(refs);
	    /*List<String[]> lit = props.getAllLiteratureReferences();
        if (lit != null && !lit.isEmpty()) {
        	String[] l = lit.get(0);
        	props.removeLiteratureReference(l[0],l[1]);            	
        } 
        if (litRefDesc != null && !litRefDesc.equals("") && litRefURI != null && !litRefURI.equals(""))
        	props.addLiteratureReference(litRefDesc, litRefURI);
        
        if (expBean.getEref() != null)
        	props.addExperimentReference(expBean.getEref());
        else {
            List<Long> refs = props.getExperimentReferences();
            if (refs != null && !refs.isEmpty()) 
            	props.removeExperimentReference(refs.get(0));
    	}*/
        
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
        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_2);
        return "success";
	}
    
    public String updateBenchmarksAndSubmitAction() {
    	if (this.updateBenchmarksAction() == "success") {
        	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        	Experiment exp = testbedMan.getExperiment(expBean.getID());
            exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
            exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
            testbedMan.updateExperiment(exp);
            expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTAPPROVAL);  
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("BenchmarkBeans"); 
    		return "goToStage4";
    	} else
    		return null;    	
    }
    
    public String updateBenchmarksAction(){
	  try {
    	// create bm-goals    	
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID());
    	// create Goal objects from Beans
    	List<BenchmarkGoal> bmgoals = new ArrayList<BenchmarkGoal>();
    	List<BenchmarkBean> bmbeans = (List<BenchmarkBean>)JSFUtil.getManagedObject("BenchmarkBeans");
    	Iterator iter = bmbeans.iterator();
    	//Iterator iter = expBean.getBenchmarks().values().iterator();    	
    	while (iter.hasNext()) {
    		BenchmarkBean bmb = (BenchmarkBean)iter.next();
    		if (bmb.getSelected()) {
	    		// method to get a new instance of BenchmarkGoal
    			BenchmarkGoal bmg = BenchmarkGoalsHandlerImpl.getInstance().getBenchmarkGoal(bmb.getID());
	    		if (bmb.getSourceValue()!=null && (!(bmb.getSourceValue().equals(""))))    			
	    			bmg.setSourceValue(bmb.getSourceValue());
	    		if (bmb.getTargetValue()!=null && (!(bmb.getTargetValue().equals(""))))	    		
	    			bmg.setTargetValue(bmb.getTargetValue());
	    		if (bmb.getEvaluationValue()!=null && (!(bmb.getEvaluationValue().equals(""))))
	    			bmg.setEvaluationValue(bmb.getEvaluationValue());
	    		if (bmb.getWeight()!=null && (!(bmb.getWeight().equals("-1"))))
	    			bmg.setWeight(Integer.parseInt(bmb.getWeight()));
	    		bmgoals.add(bmg);
	    		// add to experimentbean benchmarks
	    		expBean.addBenchmarkBean(bmb);
    		} else {
    			expBean.deleteBenchmarkBean(bmb);
    		}
    	}
    	exp.getExperimentSetup().setBenchmarkGoals(bmgoals);
    	ExperimentResources expRes = exp.getExperimentSetup().getExperimentResources();
    	if (expRes == null)
    		expRes = new ExperimentResourcesImpl();
    	expRes.setIntensity(Integer.parseInt(expBean.getIntensity()));
    	expRes.setNumberOfOutputFiles(Integer.parseInt(expBean.getNumberOfOutputFiles()));
    	exp.getExperimentSetup().setExperimentResources(expRes);
    	testbedMan.updateExperiment(exp); 
    	// if successful, set a message at top of page
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Your data have been saved successfully!");
        fmsg.setSummary("Your data have been saved successfully!");
        fmsg.setSeverity(FacesMessage.SEVERITY_INFO);
		// add message-tag for duplicate name
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage("stage3form",fmsg);
        return "success";
	  } catch (InvalidInputException e) {
		log.error(e.toString());
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Problem saving data!");
        fmsg.setSummary("Problem saving data!");
        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage("stage3form",fmsg);
		return "failure";
	  }
   	}
    
    
    public String updateEvaluationAction() {
        try {
	    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");    	
	    	Experiment exp = testbedMan.getExperiment(expBean.getID());
                
                //ExperimentReportImpl rep = new ExperimentReportImpl();
                //rep.setBodyText(ereport.getValue().toString());
                //rep.setHeader("Report Header");
                
                //save the evaluation report
                //exp.getExperimentEvaluation().setExperimentReport(rep);
                
	    	// create Goal objects from Beans
	    	List<BenchmarkGoal> bmgoals = new ArrayList<BenchmarkGoal>();
	    	Iterator iter = expBean.getBenchmarkBeans().iterator();
	    	while (iter.hasNext()) {
	    		BenchmarkBean bmb = (BenchmarkBean)iter.next();
	    		if (bmb.getSelected()) {
		    		// method to get a new instance of BenchmarkGoal
	    			BenchmarkGoal bmg = BenchmarkGoalsHandlerImpl.getInstance().getBenchmarkGoal(bmb.getID());
		    	try {
	    			String srcVal = bmb.getSourceValue();
	    			if (srcVal!=null && !srcVal.equals(""))
	    				bmg.setSourceValue(srcVal);
	    			String trgVal = bmb.getTargetValue();
	    			if (trgVal!=null && !trgVal.equals(""))
	    				bmg.setTargetValue(trgVal);
	    			String evalVal = bmb.getEvaluationValue();
	    			if (evalVal!=null && !evalVal.equals(""))
	    				bmg.setEvaluationValue(evalVal);
		    		if (bmb.getWeight()!=null && !bmb.getWeight().equals("-1"))
		    			bmg.setWeight(Integer.parseInt(bmb.getWeight()));
		    		bmgoals.add(bmg);
	    		} catch (InvalidInputException e) {
	    	        FacesMessage fmsg = new FacesMessage();
	    	        fmsg.setDetail("Values for Benchmarkgoal are not valid!"+e.toString());
	    	        fmsg.setSummary("Values for Benchmarkgoal are not valid!");
	    	        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			        FacesContext ctx = FacesContext.getCurrentInstance();
			        ctx.addMessage("bmTable",fmsg);
	    			log.error(e.toString());
		    		return "failure";
	    		}
	    		}
	    	}
	    	Map<URI,List<BenchmarkGoal>> bhm = new HashMap<URI,List<BenchmarkGoal>>();
	    	bhm.put(new URI(expBean.getEworkflowInputData()),bmgoals);
	    	Experiment e = testbedMan.getExperiment(exp.getEntityID());
	    	System.out.println("Exp ID: "+ exp.getEntityID() + " e: " + e);   	
	    	exp.getExperimentEvaluation().setEvaluatedFileBenchmarkGoals(bhm);
	    	testbedMan.updateExperiment(exp);
	        FacesMessage fmsg = new FacesMessage();
	        fmsg.setDetail("Evaluation Data saved successfully!");
	        fmsg.setSummary("Evaluation Data saved successfully!");
	        fmsg.setSeverity(FacesMessage.SEVERITY_INFO);
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.addMessage("bmTable",fmsg);	    	
	    	return "success";
        } catch (Exception e) {
        	log.error("Exception when trying to create/update Evaluations: "+e.toString());
        	return "failure";
        }
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
	        exp.setState(Experiment.STATE_IN_PROGRESS);
	        testbedMan.updateExperiment(exp);
	        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow");   
	        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_3);	        	        
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
    
    public Map<String,String> getAvailableEvaluationValues() {
       	TreeMap<String,String> map = new TreeMap<String,String>();
    	ExperimentEvaluation ev = new ExperimentEvaluationImpl();
    	Iterator iter = ev.getAllAcceptedEvaluationValues().iterator();
    	while (iter.hasNext()) {
    		String v = (String)iter.next();
    		map.put(v, v);
    	}       	
    	return map;
    }
    
    public String getRetrieveBenchmarkBeans() {
       	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");   	
    	Map<String,BenchmarkBean> availBenchmarks = new HashMap<String,BenchmarkBean>(expBean.getBenchmarks());
    	Iterator iter = BenchmarkGoalsHandlerImpl.getInstance().getAllBenchmarkGoals().iterator();
    	while (iter.hasNext()) {
    		BenchmarkGoal bmg = (BenchmarkGoal)iter.next();
    		if (availBenchmarks.containsKey(bmg.getID())) {
    			BenchmarkBean bmb = availBenchmarks.get(bmg.getID());
    			bmb.setSelected(true);
    		} else {
    			BenchmarkBean bmb = new BenchmarkBean(bmg);  			
    			bmb.setSelected(false);
        		availBenchmarks.put(bmg.getID(), bmb);
    		}
    	}
        // create and put BenchmarkBeans into session 
        List<BenchmarkBean> bmbeans = new ArrayList<BenchmarkBean>(availBenchmarks.values());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("BenchmarkBeans",bmbeans);
        return "";
    }
    
       
    public String approveExperiment(){
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID());
    	exp.getExperimentApproval().setState(Experiment.STATE_COMPLETED);
    	exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
        testbedMan.updateExperiment(exp);
        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEXECUTION);        
        expBean.setApproved(true);
        return "goToStage5";
    }
    public String executeExperiment(){
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID()); 
    	// running experiment: dummy invoker should be called here
    	try {
    		testbedMan.executeExperiment(exp);
        	String inputData = expBean.getEworkflowInputData();
        	URI outputURI = exp.getExperimentExecution().getExecutionOutputData(new URI(inputData));
        	expBean.setEworkflowOutputData(outputURI.toString());    		
	  	  	//testbedMan.updateExperiment(exp);
	  	  	if (exp.getExperimentExecution().isExecuted()) {
	  	    	exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
	  	    	exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);  	  	
	  	  		expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEVALUATION);
	  	  	}
	        testbedMan.updateExperiment(exp);
	  	  	return null;
    	} catch (Exception e) {
    		log.error("Error when executing Experiment: " + e.toString());
    		System.out.println("Error when executing Experiment: " + e.toString());
    		return null;
    	}   	
    }
    
    public String proceedToEvaluation() {
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID()); 
  	  	if (exp.getExperimentExecution().isExecuted()) {
  	    	exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
  	    	exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);  	  	
  	  		expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEVALUATION);
  	        testbedMan.updateExperiment(exp);
    		return "goToStage6";
  	  	} else
    		return null;
    }
    
    public boolean getExperimentExecutionRunning() {
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID()); 
    	if (exp.getExperimentExecution().isExecutionInProgress())
    		return true;
    	else {
      	  	//if (exp.getCurrentPhase().getPhaseName().equals(ExperimentPhase.PHASENAME_EXPERIMENTEVALUATION))
      	  	//	expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEVALUATION);
    		return false;
    	}
    }

    public String saveEvaluation(){
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID());    	
    	//exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
    	//exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);
    	exp.getExperimentEvaluation().setState(Experiment.STATE_COMPLETED);
        testbedMan.updateExperiment(exp);
    	return "completeExperiment";
    }
    
    public String loadReaderStage2() {
        return "goToReaderStage2";
    }

    public String loadReaderStage3() {
        return "goToReaderStage3";
    }
    
    public String loadReaderStage4() {
        return "goToReaderStage4";
    }
    
    public String loadReaderStage5() {
        return "goToReaderStage5";
    }
    
    public String loadReaderStage6() {
        return "goToReaderStage6";
    }
    
    public String finishReaderStage6() {
        return "goToBrowseExperiments";
    }

    public HtmlInputTextarea getEreport() {
        return ereport;
    }
    
    public void setEreport(HtmlInputTextarea ereport) {
        this.ereport = ereport;
    }
}
