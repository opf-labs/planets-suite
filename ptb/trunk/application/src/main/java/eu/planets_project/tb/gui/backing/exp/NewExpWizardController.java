package eu.planets_project.tb.gui.backing.exp;


import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.BenchmarkBean;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.FileUploadBean;
import eu.planets_project.tb.gui.backing.ListExp;
import eu.planets_project.tb.gui.backing.admin.RegisterTBServices;
import eu.planets_project.tb.gui.backing.admin.ManagerTBServices;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.ExperimentReportImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;
import eu.planets_project.tb.impl.services.tags.DefaultServiceTagHandlerImpl;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NewExpWizardController {
    
    private static Log log = PlanetsLogger.getLogger(NewExpWizardController.class, "testbed-log4j.xml");
    public NewExpWizardController() {
    }
    
/*
 * -------------------------------------------
 * START methods for new_experiment wizard page
 */

    public String updateBasicPropsAction(){
        // Flag to indicate validity of submission:
        boolean validForm = true;
        
        // remove workflow object from session, if still available from previously viewed experiment
    	//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow"); 
    	
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
    	Experiment exp = null;
		BasicProperties props = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // create message for duplicate name error message
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Experiment name already in use! - Please specify a unique name.");
        fmsg.setSummary("Duplicate name: Experiment names must be unique!");
        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        // Flag to catch new/existing state:
        boolean existingExp = true;
        log.debug("Checking if this is a new experiment.");
        // if not yet created, create new Experiment object and new Bean
        if ((expBean.getID() == 0)) { 
            existingExp = false;
	        // Create new Experiment
	        exp = new ExperimentImpl();
	        props = new BasicPropertiesImpl();
	        // Get userid info from managed bean
	        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
	        // set current User as experimenter
	        props.setExperimenter(currentUser.getUserid());
		    try {
		        log.debug("New experiment, setting name: " + expBean.getEname() );
		        props.setExperimentName(expBean.getEname());        
		    } catch (InvalidInputException e) {
	    		// add message-tag for duplicate name
		        FacesContext ctx = FacesContext.getCurrentInstance();
		        ctx.addMessage("ename",fmsg);
	    		validForm = false;
	    	}
	        ExperimentSetup expSetup = new ExperimentSetupImpl();
	        expSetup.setBasicProperties(props);       
	        exp.setExperimentSetup(expSetup);
            long expId = testbedMan.registerExperiment(exp);            
            expBean.setID(expId);
        }
        // Get the Experiment description objects
	    exp = testbedMan.getExperiment(expBean.getID());
        props = exp.getExperimentSetup().getBasicProperties();
        log.debug("TEST: exec: "+exp.getExperimentExecutable());
        // If the experiment already existed, check for valid name changes:
        if( existingExp ) {
  	      try {
            log.debug("Existing experiment, setting name: " + expBean.getEname() );
	        props.setExperimentName(expBean.getEname());        
	      } catch (InvalidInputException e) {
    		// add message-tag for duplicate name
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.addMessage("ename",fmsg);
    		validForm = false;
    	 }
        }
        //set the experiment information
        log.debug("Setting the experimental properties.");
        props.setSummary(expBean.getEsummary());
        props.setConsiderations(expBean.getEconsiderations());
        props.setPurpose(expBean.getEpurpose());
        props.setFocus(expBean.getEfocus());
        props.setScope(expBean.getEscope());
        props.setContact(expBean.getEcontactname(),expBean.getEcontactemail(),expBean.getEcontacttel(),expBean.getEcontactaddress());       
        props.setExperimentFormal(expBean.getFormality());

        String partpnts = expBean.getEparticipants();
        String[] partpntlist = partpnts.split(",");
        for(int i=0;i<partpntlist.length;i++){
            partpntlist[i] = partpntlist[i].trim();
            if( partpntlist[i] != "" ) {
                props.addInvolvedUser(partpntlist[i]);
            }
        }
        props.setExternalReferenceID(expBean.getExid());
                
        ArrayList<String> litRefDesc = expBean.getLitRefDesc();
        ArrayList<String> litRefURI = expBean.getLitRefURI();     
	    List<String[]> refList = new ArrayList<String[]>();
        if (litRefDesc != null && !litRefDesc.equals("")) {
            for( int i = 0; i < litRefDesc.size(); i++ ) {
                if( ! "".equals(litRefDesc.get(i).trim()) && 
                        ! "".equals(litRefURI.get(i).trim()) )
                    refList.add(new String[]{litRefDesc.get(i).trim(), litRefURI.get(i).trim()});
            }
        }
        try {
        	props.setLiteratureReferences(refList);
        } catch (InvalidInputException e) {
        	log.error("Problems setting literature references: "+e.toString());
        }
        List<Long> refs = new ArrayList<Long>();
        if (expBean.getEref() != null && !expBean.getEref().equals("")) {
            for( int i = 0; i < expBean.getEref().size(); i++)
                refs.add(Long.parseLong( (expBean.getEref().get(i)) ));
        }
        props.setExperimentReferences(refs);
        
        props.setDigiTypes(expBean.getDtype());
        
        // Set the experiment type:
        try {
            exp.getExperimentSetup().setExperimentType(expBean.getEtype());
        } catch (InvalidInputException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage tfmsg = new FacesMessage();
            tfmsg.setSummary("No experiment type specified!");
            tfmsg.setDetail("You must select and experiment type.");
            tfmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            ctx.addMessage("etype",tfmsg);
            validForm = false;
        }
        
        log.debug("Updating...");
        // Workaround
        // update in cached lists
        ListExp listExp_Backing = (ListExp)JSFUtil.getManagedObject("ListExp_Backing");
        listExp_Backing.getExperimentsOfUser();
        listExp_Backing.getAllExperiments();
        
        // Exit with failure condition if the form submission was not valid.
        if( ! validForm ) {
            log.debug("Exiting with failure.");
            return "failure";
        }
        
        // Put updated Bean into Session; accessible later as #{ExperimentBean}
        //FacesContext ctx = FacesContext.getCurrentInstance();
	    //ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
        testbedMan.updateExperiment(exp);
        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_2);
        log.debug("Exiting in success.");
        log.debug("TEST: exec: "+exp.getExperimentExecutable());
        return "success";
	}
    
    public String addAnotherLitRefAction() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.addLitRefSpot();
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
            // TODO The next line automatically submits and approves the request.  Should be temporary.
            return approveExperiment();
    		//return "goToStage4";
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
    	expRes.setNumberOfOutputFiles(Integer.parseInt(expBean.getNumberOfOutput()));
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

	    	// Store the updated report:
            exp.getExperimentEvaluation().getExperimentReport().setHeader(expBean.getReportHeader());
            exp.getExperimentEvaluation().getExperimentReport().setBodyText(expBean.getReportBody());
            log.debug("updateEvaluation Report Header: "+exp.getExperimentEvaluation().getExperimentReport().getHeader());
            
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
	    	//fill the benchmark goals for every input file - used for evaluation
	    	Map<URI,List<BenchmarkGoal>> bhm = new HashMap<URI,List<BenchmarkGoal>>();
	    	Iterator<String> itLocalInputFileRefs = expBean.getExperimentInputData().values().iterator();
	    	DataHandler dh = new DataHandlerImpl();
	    	//iterate over every input file and add it's BM goals
	    	while(itLocalInputFileRefs.hasNext()){
	    		String localInputFileRef = itLocalInputFileRefs.next();
	    		URI inputURI = dh.getHttpFileRef(new File(localInputFileRef), true);
		    	bhm.put(inputURI,bmgoals);
	    	}
	    	
	    	//now write these changes back to the experiment
	    	Experiment e = testbedMan.getExperiment(exp.getEntityID());
	    	log.debug("Exp ID: "+ exp.getEntityID() + " exp: " + e);
	    	
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

    public String finalizeEvaluationAction() {
        // Finalise the experiment:
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");        
        Experiment exp = testbedMan.getExperiment(expBean.getID());
        // First, catch any updates.
        updateEvaluationAction();
        exp.getExperimentEvaluation().setState(Experiment.STATE_COMPLETED);
        log.debug("attempting to save finalized evaluation. "+ exp.getExperimentEvaluation().getState());
        testbedMan.updateExperiment(exp);
        log.debug("saved finalized evaluation. "+ exp.getExperimentEvaluation().getState());
        // And report:
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Evaluation Data finalised!");
        fmsg.setSummary("Evaluation Data finalised!");
        fmsg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage("bmTable",fmsg);         
        return "success";
    }
    

    
    

    /**
     * This action completes stage2. i.e. create an experiment's executable, store the
     * added files within, hand over the selected ServiceTemplate, etc.
     * @return
     */
    public String commandSaveStep2Substep2Action()  {
        try {
	    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	        Experiment exp = testbedMan.getExperiment(expBean.getID());
	        
	        // create the experiment's executable - the constructor takes the ServiceTemplate
	        ExperimentExecutable executable = exp.getExperimentExecutable();
	        if (executable == null) {
	        	executable = new ExperimentExecutableImpl(expBean.getSelectedServiceTemplate());
	        	exp.setExperimentExecutable(executable);
	        	log.debug("save: Created a new executable: "+executable);
	        }
	        // store the provided input data
	        executable.setInputData(expBean.getExperimentInputData().values());	        
	        // store all other metadata
	        executable.setSelectedServiceOperationName(expBean.getSelectedServiceOperationName());
	        
	        //modify the experiment's stage information
	        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
	        exp.setState(Experiment.STATE_IN_PROGRESS);
	        testbedMan.updateExperiment(exp);
	        
	        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow");   
	        
	        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_3);	        	        
	    	return "goToStage3";
        } catch (Exception e) {
        	log.error("Exception when trying to create/update ExperimentWorkflow: "+e.toString());
        	e.printStackTrace();
        	return "failure";
        }
    }
    
    /**
     * In the process of selecting the proper TBServiceTemplate to work with
     * Reacts to changes within the selectOneMenu
     * @param ce
     */
    public void changedSelTBServiceTemplateEvent(ValueChangeEvent ce) {
        log.debug("changedSelTBServiceTemplateEvent: setting to "+ce.getNewValue());
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	//also sets the beans selectedServiceTemplate (from the registry)
    	expBean.setSelServiceTemplateID(ce.getNewValue().toString());
    	reloadOperations();
    }
    
    /**
     * In the process of selecting the proper TBServiceTemplate to work with
     * Reacts to changes within the selectOneMenu
     * @param ce
     */
    public void changedSelServiceOperationEvent(ValueChangeEvent ce) {
        if( ce.getNewValue() == null ) return;
        log.debug("changedSelServiceOperationEvent: setting to "+ce.getNewValue().toString());
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	expBean.setSelectedServiceOperationName(ce.getNewValue().toString());
    }
    
    /**
     * When the selected ServiceTemplate has changed - reload it's available
     * ServiceOperations and select the first one in the list
     */
    private void reloadOperations(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	List<ServiceOperation> lOperations = new Vector<ServiceOperation>();
    	TestbedServiceTemplate template = expBean.getSelectedServiceTemplate();
    	
    	if(expBean.getEtype().equals("experimentType.simpleMigration")){
    		//mapping between operationTypeID and experiment type ID
    		lOperations = template.getAllServiceOperationsByType(
    				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_MIGRATION
    				);
    	}
    	 //simple characterisation experiment
    	if(expBean.getEtype().equals("experimentType.simpleCharacterisation")){
    		lOperations = template.getAllServiceOperationsByType(
    				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_CHARACTERISATION
    				);
    	}

    	if((lOperations!=null)&&(lOperations.size()>0)){
    		//sets the first operationname selected so that it gets displayed
    		expBean.setSelectedServiceOperationName(lOperations.iterator().next().getName());
    	}
    	else{
    		expBean.setSelectedServiceOperationName("");
    	}
    }
    
    
    /**
     * A file has been slected for being uploaded and the add icon was pressed to add a reference
     * for this within the experiment bean
     * @return
     */
    public String commandAddInputDataItem(){
    	//0) upload the specified data to the Testbed's file repository
        log.debug("commandAddInputDataItem: Uploading file.");
		FileUploadBean uploadBean = this.uploadFile();
		if( uploadBean == null ) return "goToStage2";
		String fileRef = uploadBean.getLocalFileRef();
		if(!(new File(fileRef).canRead())){
			log.debug("Added file reference not correct or reachable by the VM "+fileRef);
		}
    	
    	//1) Add the file reference to the expBean
		log.debug("Adding file to Experiment Bean.");
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	String position = expBean.addExperimentInputData(fileRef);
    	
    	//2) For the current fileRef create an GUI outputfield + a RemoveIcon+Link
    	//UIComponent panel = this.getComponent("configureExpWorkflowForm:panelAddedFiles");
    	//UIComponent panel = expBean.getPanelAddedFiles();
    	//expBean.helperCreateRemoveFileElement(panel, fileRef, position);
    	// FIXME Remove the above now addExperimentInputData does it automatically.
    	
    	//reload stage2 and displaying the added data items
        log.debug("commandAddInputDataItem DONE");
    	return "goToStage2";
    }
    
    
    /**
     * checks for the max supported and min required number of input files and triggers
     * the file upload button rendered or not.
     * @return
     */
    public boolean isAddFileButtonRendered(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if((expBean.getSelectedServiceTemplate()!=null)&&(expBean.getSelectedServiceOperationName()!="")){
    		ServiceOperation operation = expBean.getSelectedServiceTemplate().getServiceOperation(
    				expBean.getSelectedServiceOperationName()
    				);
    		int maxsupp = operation.getMaxSupportedInputFiles();
    		int current = expBean.getNumberOfInputFiles();
    		if((current<maxsupp)){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * May only proceed to the next step if the min. number of required input files
     * was provided
     * @return
     */
    public boolean isMinReqNrOfFilesSelected(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if((expBean.getSelectedServiceTemplate()!=null)&&(expBean.getSelectedServiceOperationName()!="")){
    		try{
    			ServiceOperation operation = expBean.getSelectedServiceTemplate().getServiceOperation(
    				expBean.getSelectedServiceOperationName()
    				);
    			int minrequ = operation.getMinRequiredInputFiles();
    			int current = expBean.getNumberOfInputFiles();
    			if(current>=minrequ){
    				return true;
    			}
    		}catch(Exception e){
    			//exception when re-initing wizard: then expBean.selectedOpName could not be contained 
    			//in the template as the first one is selected when filling the screen
    			return false;
    		}
    	}
    	return false;
    }
    
    
    public boolean isExecutionSuccess(){
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");   
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID());   	
    	if(exp.getExperimentExecutable()!=null){
    		return exp.getExperimentExecutable().isExecutionSuccess();
    	} 
    	return false;
    }
    

    /**
	 * Removes one selected file reference from the list of added file refs for Step3.
	 * The specified file ref(s) are used as input data to invoke a given service operation. 
	 * @return
	 */
	public String commandRemoveAddedFileRef(ActionEvent event){
		
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			
			//1)get the passed Attribute "IDint", which is the counting number of the component
			String IDnr = event.getComponent().getAttributes().get("IDint").toString();
			
			
			//2) Remove the data from the bean's variable
			ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
			expBean.removeExperimentInputData(IDnr+"");
			
			//3) Remove the GUI elements from the panel
			
			UIComponent comp_link_remove = this.getComponent(expBean.getPanelAddedFiles(),"removeLink"+IDnr);
			UIComponent comp_link_src = this.getComponent(expBean.getPanelAddedFiles(),"fileRef"+IDnr);

			//UIComponent comp_link_src = this.getComponent("panelAddedFiles:fileRef"+IDnr);
			expBean.getPanelAddedFiles().getChildren().remove(comp_link_remove);
			//this.getComponentPanelStep3Add().getChildren().remove(comp_text);
			expBean.getPanelAddedFiles().getChildren().remove(comp_link_src);

			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "goToStage2";
	}
	
	/**
	 * Undo the process of selecting service and operation to pick another one.
	 * Note: this leads to losing already uploaded input data - warn the user
	 * @return
	 */
	public String changeAlreadySelectedSerOps(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		expBean.removeAllExperimentInputData();
		expBean.setOpartionSelectionCompleted(false);
		return "goToStage2";
	}
	
	/**
	 * Set the selected service and operation to final and continue to select input data
	 * @return
	 */
	public String completeSerOpsSelection(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		expBean.setOpartionSelectionCompleted(true);
		return "goToStage2";
	}
    
	/**
	 * Helper to fetch the FileUploadBean from the request
	 * @return
	 */
	private FileUploadBean getCurrentFileUploadBean(){
		return (FileUploadBean)JSFUtil.getManagedObject("FileUploadBean");
	}
	
	/**
	 * Triggers the page's file upload element, takes the selected data and 
	 * transfers it into the Testbed's file repository. The reference to this file
	 * is layed into the system's application map.
	 * @return: Returns an instance of the FileUploadBean (e.g. for additional operations as .getEntryName, etc.)
	 * if the operation was successful or null if an error occured
	 */
	private FileUploadBean uploadFile(){
		FileUploadBean file_upload = this.getCurrentFileUploadBean();
		try{
			//trigger the upload command
			String result = file_upload.upload();
			
			if(!result.equals("success-upload")){
				return null;
			}
		}
		catch(Exception e){
			//In this case an error occured ("error-upload"): just reload the page without adding any information
			log.error("error uploading file to Testbed's input folder: "+e.toString());
			return null;
		}
		
		return file_upload;
	}
    
    /**
     * Reacting to the "use" button, to make ServiceTemplate Selection final
     */
    /*public void commandUseSelTBServiceTemplate(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	//TODO: continue with implementation
    }*/
    
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
    
    public String getExperimentTypeName(String ID) {
        String name = AdminManagerImpl.getInstance().getExperimentTypeName(ID);
        
        return name;
    }
    
    
    /**
     * Queries the ServiceTemplateRegistry and returns a Map of all available Templates
     * with service name and it's UUID as key, which is then displayed in the GUI
     * Restriction: The list is restricted by the already chosen experiment type:
     * e.g. only fetch Migration/Characterisation templates
     * @return
     */
    public Map<String,String> getAllAvailableTBServiceTemplates(){
    	TreeMap<String,String> ret = new TreeMap<String,String>();
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
    	Collection<TestbedServiceTemplate> templates = new Vector<TestbedServiceTemplate>();
    	
    	//determine which typeID has been selected
    	  //simple migration experiment
    	if(expBean.getEtype().equals("experimentType.simpleMigration")){
    		//mapping between service type ID and experiment type ID
    		templates = registry.getAllServicesWithType(
    				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_MIGRATION
    				);
    	}
    	 //simple characterisation experiment
    	if(expBean.getEtype().equals("experimentType.simpleCharacterisation")){
    		templates = registry.getAllServicesWithType(
    				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_CHARACTERISATION
    				);
    	}
    	//add data for rendering
		Iterator<TestbedServiceTemplate> itTemplates = templates.iterator();
		while(itTemplates.hasNext()){
			TestbedServiceTemplate template = itTemplates.next();
			ret.put(template.getName(),String.valueOf(template.getUUID()));
		}
		
		//only triggered for the first time
		if(expBean.getSelectedServiceTemplate()==null){
			expBean.setSelServiceTemplateID(ret.values().iterator().next());
			reloadOperations();
		}
    	return ret;
    }
    
    /**
     * Returns a Map of all available serviceOperations for an already selected
     * ServiceTemplate
     * Restriction: The list is restricted by the already chosen experiment type:
     * e.g. only fetch Migration/Characterisation templates
     * @return
     */
    public Map<String,String> getAllAvailableServiceOperations(){
    	TreeMap<String,String> ret = new TreeMap<String,String>();
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	TestbedServiceTemplate template = expBean.getSelectedServiceTemplate();
    	//log.debug("Looking up service operations for template: "+template);
    	if(template!=null){
            log.debug("Looking for services of type: "+expBean.getEtype());
    		List<ServiceOperation> lOperations = null;
    		  //simple migration experiment
        	if(expBean.getEtype().equals("experimentType.simpleMigration")){
        		//mapping between operationTypeID and experiment type ID
        		lOperations = template.getAllServiceOperationsByType(
        				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_MIGRATION
        				);
        	}
        	 //simple characterisation experiment
        	if(expBean.getEtype().equals("experimentType.simpleCharacterisation")){
        		lOperations = template.getAllServiceOperationsByType(
        				TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_CHARACTERISATION
        				);
        	}
        	
        	if(lOperations!=null){
        		//add data for rendering
        		Iterator<ServiceOperation> itOperations = lOperations.iterator();
        		while(itOperations.hasNext()){
        			ServiceOperation operation = itOperations.next();
        			//Treevalue, key
        			ret.put(operation.getName(),operation.getName());
        		}
        	}
    	}
    	return ret;
    }
    

    /**
     * Gets a list of all default Service Annotation Tags using the default tag reader class
     * Service Annotation Tags are used to restrict the list of rendered services
     * @return
     */
    public Collection<ServiceTag> getAllDefaultServiceAnnotationTags(){
    	DefaultServiceTagHandler dth = DefaultServiceTagHandlerImpl.getInstance();
    	return dth.getAllTags();
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
    	try {
    		//call invocation on the experiment's executable
    		testbedMan.executeExperiment(exp);
        	
    		//fill the expBean with the execution's results
        	expBean.setOutputData(exp.getExperimentExecutable().getOutputDataEntries());

	  	  	if (exp.getExperimentExecution().isExecutionInvoked()) {
	  	    	exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
	  	    	exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);  	  	
	  	  		expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEVALUATION);
	  	  	}
	        testbedMan.updateExperiment(exp);
	  	  	return null;
    	} catch (Exception e) {
    		log.error("Error when executing Experiment: " + e.toString());
    		if( log.isDebugEnabled() ) e.printStackTrace();
    		return null;
    	}   	
    }
    
    
    public String proceedToEvaluation() {
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = testbedMan.getExperiment(expBean.getID()); 
  	  	if (exp.getExperimentExecution().isExecutionCompleted()) {
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
    	if ((exp.getExperimentExecution().isExecutionInvoked())&&(!exp.getExperimentExecution().isCompleted()))
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
    
    public String finishReader() {
        return "goToBrowseExperiments";
    }

/*
 * END methods for new_experiment wizard page
 * -------------------------------------------
 */
    

	/**
	 * Get the component from the JSF view model - it's id is registered withinin the page
	 * @param panel if null then from the root
	 * @param sID
	 * @return
	 */
	private UIComponent getComponent(UIComponent panel, String sID){

			FacesContext facesContext = FacesContext.getCurrentInstance();
			
			if(panel==null){
				//the ViewRoot contains all children starting from sub-level 0
				panel = facesContext.getViewRoot();
			}
			
			Iterator<UIComponentBase> it = panel.getChildren().iterator();
			UIComponent returnComp = null;
			
			while(it.hasNext()){
				UIComponent guiComponent = it.next().findComponent(sID);
				if(guiComponent!=null){
					returnComp = guiComponent;
				}
			}
			
			//changes on the object are directly reflected within the GUI
			return returnComp;
	  }
    

}
