package eu.planets_project.tb.gui.backing.exp;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.BenchmarkBean;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.FileUploadBean;
import eu.planets_project.tb.gui.backing.Manager;
import eu.planets_project.tb.gui.backing.PropertyDnDTreeBean;
import eu.planets_project.tb.gui.backing.UploadManager;
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
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.finals.DigitalObjectTypesImpl;
import eu.planets_project.tb.impl.model.ontology.util.OntoPropertyUtil;
import eu.planets_project.tb.impl.serialization.ExperimentViaJAXB;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;
import eu.planets_project.tb.impl.services.tags.DefaultServiceTagHandlerImpl;
import eu.planets_project.tb.impl.system.TestbedBatchJob;
import eu.planets_project.tb.impl.system.TestbedBatchProcessor;

import javax.faces.application.FacesMessage;

import java.io.File;
import java.net.URI;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.richfaces.component.html.HtmlDataTable;

public class NewExpWizardController {
    
    private static PlanetsLogger log = PlanetsLogger.getLogger(NewExpWizardController.class, "testbed-log4j.xml");
    public NewExpWizardController() {
    }
    
/*
 * -------------------------------------------
 * START methods for new_experiment wizard page
 */

    public String updateBasicPropsAction(){
        // Flag to indicate validity of submission:
        boolean validForm = true;
        // Flag to indicate that the experiment definition is not valid and cannot be constructed
        boolean validExperiment = true;
        
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
    	Experiment exp = null;
		BasicProperties props = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        log.info("ExpBean: "+expBean.getEname()+" : "+expBean.getEsummary());
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
	    		validExperiment = false;
	    	}
	        ExperimentSetup expSetup = new ExperimentSetupImpl();
	        expSetup.setBasicProperties(props);       
	        exp.setExperimentSetup(expSetup);
	        log.info("Creating a new experiment.");
            long expId = testbedMan.registerExperiment(exp);
            expBean.setID(expId);
            expBean.setExperiment(testbedMan.getExperiment(expId));
        }
        log.debug("Created experiment, now retrieving it.");
        // Get the Experiment description objects
        exp = expBean.getExperiment();
        props = exp.getExperimentSetup().getBasicProperties();
        log.info("Setting the experimental properties, "+props.getExperimentName()+" : "+props.getSummary());
        // If the experiment already existed, check for valid name changes:
        if( existingExp ) {
  	      try {
            log.info("Existing experiment, setting name from expBean: " + expBean.getEname() );
	        props.setExperimentName(expBean.getEname());        
	      } catch (InvalidInputException e) {
    		// add message-tag for duplicate name
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.addMessage("ename",fmsg);
    		validForm = false;
    	 }
        }
        //set the experiment information
        props.setSummary(expBean.getEsummary());
        props.setConsiderations(expBean.getEconsiderations());
        props.setPurpose(expBean.getEpurpose());
        props.setFocus(expBean.getEfocus());
        props.setScope(expBean.getEscope());
        props.setContact(expBean.getEcontactname(),expBean.getEcontactemail(),expBean.getEcontacttel(),expBean.getEcontactaddress());       
        props.setExperimentFormal(expBean.getFormality());
        log.info("Set the experimental properties, "+props.getExperimentName()+" : "+props.getSummary());

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
        ArrayList<String> litRefTitle = expBean.getLitRefTitle();    
        ArrayList<String> litRefAuthor = expBean.getLitRefAuthor(); 
	    List<String[]> refList = new ArrayList<String[]>();
        if (litRefDesc != null && !litRefDesc.equals("")) {
            for( int i = 0; i < litRefDesc.size(); i++ ) {
                if( ! "".equals(litRefDesc.get(i).trim()) && 
                        ! "".equals(litRefURI.get(i).trim()) )//author etc. can be empty
                    refList.add(new String[]{litRefDesc.get(i).trim(), litRefURI.get(i).trim(), litRefTitle.get(i).trim(), litRefAuthor.get(i).trim()});
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
        
        /*
        props.setDigiTypes(expBean.getDtype());
        
        log.debug("Checking the experiment type.");
        // Set the experiment type:
        try {
            //check if the experiment type has changed. If yes we need to remove the already chosen
            log.info("Current type: '"+exp.getExperimentSetup().getExperimentTypeID()+"' versus '"+expBean.getEtype()+"'.");
            //selection from step2, to properly reload all available serivces + operations
            if(!exp.getExperimentSetup().getExperimentTypeID().equals(expBean.getEtype())){
            	//exp. type was reselected - remove all chosen template information
            	expBean.removeSelectedServiceTemplate();
            	//set step2 to substep1
            	changeAlreadySelectedSerOps();
            }
            // This is what throws the error:
            exp.getExperimentSetup().setExperimentType(expBean.getEtype());
        } catch (InvalidInputException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage tfmsg = new FacesMessage();
            tfmsg.setSummary("No experiment type specified!");
            tfmsg.setDetail("You must select an experiment type.");
            tfmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            ctx.addMessage("etype",tfmsg);
            validForm = false;
            log.error("Got error on Etype: "+e);
        }
        */
        
        // Exit with failure condition if no valid experiment could be constructed.
        if( ! validForm && ! validExperiment ) {
            log.debug("Exiting with failure.");
            return "failure";
        }
        
        // Exit with failure condition if the form submission was not valid.
        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
        exp.getExperimentSetup().setSubStage(1);
        //testbedMan.updateExperiment(exp);
        if( validForm ) {
            exp.getExperimentSetup().setSubStage(2);
            expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_2);
            log.debug("Exiting in success.");
            return "success";
        } else {
            log.debug("Exiting in failure - invalid form.");
            return "failure";
        }
        
	}
    
    public String addAnotherLitRefAction() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.addLitRefSpot();
        return "success";
    }
    
    public String updateBenchmarksAndSubmitAction() {
    	if (this.updateBenchmarksAction() == "success") {
    	    return submitForApproval();
    	} else
    		return null;    	
    }
    
    private String submitForApproval() {
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Experiment exp = expBean.getExperiment();
        exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
        testbedMan.updateExperiment(exp);
        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTAPPROVAL);  
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("BenchmarkBeans"); 
        // Attempt to approve the experiment, and forward appropriately
        if( ! AdminManagerImpl.experimentRequiresApproval(exp) ) {
            autoApproveExperiment();
            return "goToStage5";
        }
        // Otherwise, await approval:
        AdminManagerImpl.requestExperimentApproval(exp);
        return "goToStage4";
    }
    
    public String unsubmitAndEdit() {
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Experiment exp = expBean.getExperiment();
        ExperimentImpl.resetToEditingStage(exp);
        testbedMan.updateExperiment(exp);
        return "goToStage1";
    }
    
    public String updateExperimentBeanState() {
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Experiment exp = expBean.getExperiment();
        testbedMan.updateExperiment(exp);
        return "success";
    }
    
	/**
	 * Adds the autoEval data to the benchmark goal and finally returns to step3 of the new experiment.
	 * @return
	 */
    public String saveAutoEvalConfigData(){
    	//set the bmb as selected, as otherwise its auto eval settings won't get stored
    	AutoBMGoalEvalUserConfigBean autoEvalConfigBean = (AutoBMGoalEvalUserConfigBean)JSFUtil.getManagedObject("AutoEvalSerUserConfigBean");
    	this.setBenchmarkBeanSelected(autoEvalConfigBean.getBMGoalID(),true);
    	//update the BenchmarkBean and all model elements
    	updateBenchmarksAction();
    	return "return to exp_stage3";
    }
    
    /**
     * Sets a given benchmark bean's selected state as true/false
     * @param bmGoalID
     * @param b
     */
    @SuppressWarnings("unchecked")
    private void setBenchmarkBeanSelected(String bmGoalID, boolean b){
    	List<BenchmarkBean> bmbeans = (List<BenchmarkBean>)JSFUtil.getManagedObject("BenchmarkBeans");
    	for(BenchmarkBean bmb : bmbeans){
    		if(bmb.getID().equals(bmGoalID)){
    			bmb.setSelected(b);
    		}
    	}
    }
    
    /**
     * Takes the list of selected BenchmarkBeans, and creates BenchmarkGoal objects out if its provided
     * data which is stored as part of the experimentsetup overall BMGoals (not file bmgoals, not experimentevaluation phase). 
     * Already added BenchmarkGoals get updated.
     * @return
     */
    @SuppressWarnings("unchecked")
    public String updateBenchmarksAction(){
    /* FIXME ANJ Clean this up:
	  try {
	  */
    	// create bm-goals    	
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = expBean.getExperiment();
    	// create Goal objects from Beans
    	List<BenchmarkGoal> bmgoals = new ArrayList<BenchmarkGoal>();
    	List<BenchmarkBean> bmbeans = (List<BenchmarkBean>)JSFUtil.getManagedObject("BenchmarkBeans");
    	
    	if( bmbeans != null ) {
    	Iterator<BenchmarkBean> iter = bmbeans.iterator();
    	//Iterator iter = expBean.getBenchmarks().values().iterator();    	
    	while (iter.hasNext()) {
    		BenchmarkBean bmb = iter.next();
    	
    		if (bmb.getSelected()) {

    			//We're having an existing bmGoal object to modify
    			BenchmarkGoal bmg = exp.getExperimentSetup().getBenchmarkGoal(bmb.getID());
    			if(bmg==null){
    				//We're adding a new BMGoal from the list
    				//method to get a new instance of BenchmarkGoal
    				bmg = BenchmarkGoalsHandlerImpl.getInstance().getBenchmarkGoal(bmb.getID());
    			}
    			
    			//update the bmg with the provided bean's data
    			/* FIXME ANJ Clean this up
    			helper_addBMBSettingsToBMGoal(bmb,bmg);
	    		*/
    			
	    		bmgoals.add(bmg);
	    		// add to experimentbean benchmarks
	    		expBean.addBenchmarkBean(bmb);
	    		
    		} else {
    			expBean.deleteBenchmarkBean(bmb);
    		}
    	}
    	}
    	exp.getExperimentSetup().setBenchmarkGoals(bmgoals);
    	ExperimentResources expRes = exp.getExperimentSetup().getExperimentResources();
    	if (expRes == null)
    		expRes = new ExperimentResourcesImpl();
    	expRes.setIntensity(Integer.parseInt(expBean.getIntensity()));
    	expRes.setNumberOfOutputFiles(Integer.parseInt(expBean.getNumberOfOutput()));
    	exp.getExperimentSetup().setExperimentResources(expRes);
    	//testbedMan.updateExperiment(exp);
        //the current stage is 3 as the'save' button is pressed
        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_3);
        exp.getExperimentSetup().setSubStage(ExperimentSetup.SUBSTAGE3);
        return "success";
        /* FIXME ANJ Clean this up
	  } catch (InvalidInputException e) {
		log.error(e.toString());
        FacesMessage fmsg = new FacesMessage();
        fmsg.setDetail("Problem saving data!");
        fmsg.setSummary("Problem saving data!");
        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null,fmsg);
		return "failure";
	  }
        */
   	}
    
    /* FIXME ANJ Clean this up
    private BenchmarkGoal helper_addBMBSettingsToBMGoal(BenchmarkBean bmb, BenchmarkGoal bmg) throws InvalidInputException{
		//get a new BMGoal object: BenchmarkGoal bmg = BenchmarkGoalsHandlerImpl.getInstance().getBenchmarkGoal(bmb.getID());
		if (bmb.getSourceValue()!=null && (!(bmb.getSourceValue().equals("")))) 			
			bmg.setSourceValue(bmb.getSourceValue());
		if (bmb.getTargetValue()!=null && (!(bmb.getTargetValue().equals(""))))	    		
			bmg.setTargetValue(bmb.getTargetValue());
		if (bmb.getEvaluationValue()!=null && (!(bmb.getEvaluationValue().equals(""))))
			bmg.setEvaluationValue(bmb.getEvaluationValue());
		if (bmb.getWeight()!=null && (!(bmb.getWeight().equals("-1"))))
			bmg.setWeight(Integer.parseInt(bmb.getWeight()));
		
		//check and add auto evaluation configuration data
		this.addAutoEvalSettingsToBMGoal(bmb, bmg);
		
		return bmg;
    }
    */

    /**
     * Fetches the provided AutoBMGoalEvalUserConfigBean, extracts its information
     * and stores it within the AutoEvaluationSettings backend object for a given BenchmarkGoal
     * The input BenchmarkGoal object is updated and returned.
     * @param bmb
     * @param bmg
     * @return
     */
    /* FIXME ANJ Clean this up
    private BenchmarkGoal addAutoEvalSettingsToBMGoal(BenchmarkBean bmb, BenchmarkGoal bmg){
    	AutoBMGoalEvalUserConfigBean autoEvalConfigBean = (AutoBMGoalEvalUserConfigBean)JSFUtil.getManagedObject("AutoEvalSerUserConfigBean");
    	
    	//check 1) if the bean was already configured and B) for which bmgoal the configuration was created:
    	if(autoEvalConfigBean.getBMGoalID()==null)
    		return bmg;
    	if(!autoEvalConfigBean.getBMGoalID().equals(bmg.getID())){
    		//config not for this bean - return non-modified object
    		return bmg;
    	}
    	if(bmb.getAutoEvalService()==null){
    		//config not set for this bean - return non-modified object
    		return bmg;
    	}
    	
    	AutoEvaluationSettingsImpl autoEvalConfig = new AutoEvaluationSettingsImpl(bmb.getAutoEvalService());
    	
    	for(TBEvaluationTypes evalType: TBEvaluationTypes.values()){
    		//add the metric evaluation configuration for all types
    		for(MetricBean mb:autoEvalConfigBean.getMetricConfigFor(evalType)){
    			//add all defined configurations for this type from the bean
    			Metric m = new MetricImpl(mb.getName(), mb.getType(), mb.getDescription());
    			Config c = autoEvalConfig.new ConfigImpl(mb.getMathExpr(), mb.getEvalBoundary(), m);
    			
    			autoEvalConfig.addConfiguration(evalType, c);
    		}
    		
    	}
    	bmg.setAutoEvalSettings(autoEvalConfig);
    	return bmg;
    }
*/    

    public String updateBMEvaluationAction() {
        log.debug("In updateEvaluationAction...");
        try {
	    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");    	
	    	Experiment exp = expBean.getExperiment();

	    	// 1. Store the updated report:
            exp.getExperimentEvaluation().getExperimentReport().setHeader(expBean.getReportHeader());
            exp.getExperimentEvaluation().getExperimentReport().setBodyText(expBean.getReportBody());
            log.debug("updateEvaluation Report Header: "+exp.getExperimentEvaluation().getExperimentReport().getHeader());
            
	    	// 2. update Experiment Overall BenchmarkGoals from Bean
	    	List<BenchmarkGoal> expBMgoals = new ArrayList<BenchmarkGoal>();
	    	Iterator<BenchmarkBean> iter = expBean.getExperimentBenchmarkBeans().iterator();
	    	log.debug("Found # of ExperimentOverall BMGS: " + expBean.getExperimentBenchmarkBeans().size());
	    	boolean bError = false;
	    	while (iter.hasNext()) {
	    		BenchmarkBean bmb = iter.next();
	    		BenchmarkGoal bmg;
	    		if (bmb.getSelected()) {
		    		// get the bmgoal from the evaluation data
	    			bmg = exp.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(bmb.getID());
                    expBMgoals.add(bmg);
                    /* FIXME ANJ Clean this up:
	    		try {
		    		//update the bmg with the provided bean's data
		    		helper_addBMBSettingsToBMGoal(bmb,bmg);
		    		
		    		expBMgoals.add(bmg);
		    		log.debug("updating bmg's target:" + bmg.getTargetValue());
		    		
	    		} catch (InvalidInputException e) {
	    			//create an ErrorMessage
	    	        FacesMessage fmsg = new FacesMessage();
	    	        if(bmg!=null){
	    	        	fmsg.setSummary("Validation of "+bmg.getName()+" failed");
	    	        	fmsg.setDetail("Validation of "+bmg.getName()+" failed");
	    	        }
	    	        else{
	    	        	fmsg.setDetail("source/target value of a given file-Benchmarkgoal is not valid!"+e.toString());
	 	    	        fmsg.setSummary("source/target value of a given file-Benchmarkgoal is not valid!");
	    	        }
	    	        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			        FacesContext ctx = FacesContext.getCurrentInstance();
			        ctx.addMessage("bmTable",fmsg);
	    			log.error(e.toString());
	    			//set error true: all error messages are collected and then "failure" is returned
	    			bError = true;
	    		}
                    */
	    		}
	    	}
	    	
	    	//3. fill the file benchmark goals - used for evaluation of every input file
	    	boolean bError2 = false;
	    	Map<String,BenchmarkBean> mBMBs = expBean.getFileBenchmarkBeans();
	    	Map<URI,List<BenchmarkGoal>> mFileBMGs = new HashMap<URI,List<BenchmarkGoal>>();
	    	Iterator<String> itLocalInputFileRefs = expBean.getExperimentInputData().values().iterator();
	    	DataHandler dh = new DataHandlerImpl();
	   
	    	//iterate over every input file and add update their evaluation
	    	BenchmarkGoal bmg =null;
	    	try {
	    		while(itLocalInputFileRefs.hasNext()){
					String localInputFileRef = itLocalInputFileRefs.next();
					URI inputURI = dh.getDownloadURI(localInputFileRef);
					List<BenchmarkGoal> lbmgs = new ArrayList<BenchmarkGoal>();
					
					for(BenchmarkBean b : mBMBs.values()){
						bmg = exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoal(inputURI, b.getID());
						//BenchmarkBean bmb = mBMBs.get(inputURI+b.getID());
                        lbmgs.add(bmg);
                        /* FIXME ANJ Clean this up:
						try{
							this.helper_addBMBSettingsToBMGoal(bmb, bmg);
							lbmgs.add(bmg);
						}
						catch(InvalidInputException e){
							//create an ErrorMessage
			    	        FacesMessage fmsg = new FacesMessage();
			    	        if(bmg!=null){
			    	        	fmsg.setSummary("Validation of "+bmg.getName()+" failed");
			    	        	fmsg.setDetail("Validation of "+bmg.getName()+" failed");
			    	        }
			    	        else{
			    	        	fmsg.setDetail("source/target value of a given file-Benchmarkgoal is not valid!"+e.toString());
			 	    	        fmsg.setSummary("source/target value of a given file-Benchmarkgoal is not valid!");
			    	        }
			    	        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
					        FacesContext ctx = FacesContext.getCurrentInstance();
					        ctx.addMessage("modelpanel_error",fmsg);
			    			log.error(e.toString());
				    		bError2 = true;
						}
						*/
					}
					
					mFileBMGs.put(inputURI,lbmgs);
				}
			} catch (Exception e2) {
				//a system exception occurred:
				log.error("Failure within filling FileBenchmarkGoals");
				return "failure";
			}
			
			//if either overall bmgoal evaluation or file bmgoal evaluation caused a validation exception
			if((bError)||(bError2)){
	    		return "failure";
	    	}
	    	
	    	//4. now write these changes back to the experiment
	    	Experiment e = expBean.getExperiment();
	    	log.debug("Exp ID: "+ exp.getEntityID() + " exp: " + e);
	    	
	    	exp.getExperimentEvaluation().setEvaluatedExperimentBenchmarkGoals(expBMgoals);
	    	exp.getExperimentEvaluation().setEvaluatedFileBenchmarkGoals(mFileBMGs);
	    	
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

    public String finalizeBMEvaluationAction() {
        // Finalise the experiment:
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");        
        Experiment exp = expBean.getExperiment();
        // First, catch any updates.
        updateBMEvaluationAction();
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
	    ExperimentBean expBean = (ExperimentBean) JSFUtil.getManagedObject("ExperimentBean");
        Experiment exp = expBean.getExperiment();

        // modify the experiment's stage information
        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
        exp.setState(Experiment.STATE_IN_PROGRESS);
        // testbedMan.updateExperiment(exp);

        // Default state, staying on this stage unless success is maintained:
        exp.getExperimentSetup().setSubStage(ExperimentSetup.SUBSTAGE2);
        String result  ="success";
        
        // Verify that there is at least on DO:
        log.info("Checking for digital objects...");
        if( exp.getExperimentExecutable().getInputData().size() == 0 ) {
            FacesMessage fmsg = new FacesMessage();
            fmsg.setSummary("No input files specified!");
            fmsg.setDetail("You must specify at least one Digital Object to experiment upon.");
            fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage("addedFileInfoPanel",fmsg);
            log.info("Not enough digital objects.");
            result = "failure";
        }
        
        // Verify that the experiment type is set:
        log.info("Checking that the experiment type is set.");
        if( exp.getExperimentSetup().getExperimentTypeID() == null 
                || "".equals(exp.getExperimentSetup().getExperimentTypeID()) ) {
            FacesMessage fmsg = new FacesMessage();
            fmsg.setSummary("No experiment type set!");
            fmsg.setDetail("Please choose an experiment type.");
            fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage("etype",fmsg);
            log.info("Not set the type.");
            result = "failure";
        }
        
        // Verify that the experiment is configured correctly...
        
        // Get the workflow, to force the workflow to be configured based on the parameters.
        String expType = exp.getExperimentSetup().getExperimentTypeID();
        try {
            log.info("Setting experiment type to:" + expType);
            exp.getExperimentExecutable().setWorkflowType(expType);
        } catch( Exception e ) {
            FacesMessage fmsg = new FacesMessage();
            fmsg.setSummary("There was an error when configuring your experiment. Please check the workflow parameter(s).");
            fmsg.setDetail("ERROR: " + e );
            fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage("etype",fmsg);
            result = "failed";
        }
        
        // Warn if experiment will require approval.
        if( AdminManagerImpl.experimentRequiresApproval(exp) ) {
            FacesMessage fmsg = new FacesMessage();
            fmsg.setSummary("Experiments this big require approval by an administrator.");
            fmsg.setDetail(fmsg.getSummary());
            fmsg.setSeverity(FacesMessage.SEVERITY_WARN);
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage("etype",fmsg);
        }

        if( "success".equals(result)) {
            exp.getExperimentSetup().setSubStage(ExperimentSetup.SUBSTAGE3);
        }
        return result;
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
        log.info("commandAddInputDataItem: Uploading file.");
		FileUploadBean uploadBean = UploadManager.uploadFile(true);
		if( uploadBean == null ) {
	        log.warn("commandAddInputDataItem: Uploaded file was null.");
		    return "goToStage2";
		}
		String fileRef = uploadBean.getUniqueFileName();
		if(!(new File(fileRef).canRead())){
			log.error("Added file reference not correct or reachable by the VM "+fileRef);
		}
    	
    	//1) Add the file reference to the expBean
        log.info("Adding file to Experiment Bean.");
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	String position = expBean.addExperimentInputData(fileRef);
        log.info("Adding file to Experiment Bean at position "+position);
    	
    	//reload stage2 and displaying the added data items
        log.info("commandAddInputDataItem DONE");
    	return "goToStage2";
    }

    /**
     * The Stage 1-3 Save-Changes action, that just tries to save the current state.
     * 
     * Attempts to store the data for each page, and redirects the user to the first page with errors.
     * 
     * @return "success" upon success.
     */
    private String commandSaveExperiment(int stage ) {
        log.info("Attempting to save this experiment.");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        log.info("commandSaveExperiment: ExpBean: "+expBean.getEname()+" : "+expBean.getEsummary());
        
        // This saves the first three pages in turn, and redirects appropriately if there are any problems.
        String result = null;
        if( stage == 1 ) {
            // Page 1
            result = this.updateBasicPropsAction();
        } else if( stage == 2 ) {
            // Page 2
            result = this.commandSaveStep2Substep2Action();
        } else if( stage == 3 ) {
            // Page 3
            result = this.updateBenchmarksAction();
        } else {
            result = "success";
        }
        
        // Now save any updates, if stage 1 was okay:
        if( stage != 1 || ( stage == 1 && "success".equals(result) ) ) {
            // Now save any updates.
            Experiment exp = expBean.getExperiment();
            log.info("Saving the bean: "+exp.getExperimentSetup().getExperimentTypeID());
            // Commit the changes:
            testbedMan.updateExperiment(exp);
        } else {
            log.warn("Did not save update.");
        }
        
        FacesContext ctx = FacesContext.getCurrentInstance();
        // Add a message:
        FacesMessage fmsg = null;
        if( "success".equals(result)) {
            // Tell them it went well:
            fmsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Your data has been saved successfully.","Saved.");
            log.info("Message: Edit Succeeded.");
            // Add a Global message:
            ctx.addMessage(null,fmsg);
        } else {
            fmsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "There were problems with your experiment.","Save failed.");
            log.info("Message: Edit Failed.");
            // Add a Global message:
            ctx.addMessage(null,fmsg);
            return "failure";
        }

        // Pop to the destination:
        return result;
    }
    
    /**
     * Attempts to make a copy of the current experiment and return user to stage 1
     * of the "new" experiment
     */
    private String commandSaveExperimentAs() {
        log.info("Attempting to save this experiment as a new experiment.");
        ExperimentBean oldExpBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        
        // Create a deep copy via the XML serialisation system:
        Experiment exp = ExperimentViaJAXB.deepCopy( (ExperimentImpl)oldExpBean.getExperiment() );
        
        // Modify Start Date and name
        exp.setStartDate( Calendar.getInstance() );
        try {
            exp.getExperimentSetup().getBasicProperties().setExperimentName( 
                exp.getExperimentSetup().getBasicProperties().getExperimentName() + " (copy)" );
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
        
        // Clear out the results:
        ExperimentExecutableImpl.clearExecutionRecords( (ExperimentExecutableImpl) exp.getExperimentExecutable() );
        // Pair back to the 'editor' stage.
        ExperimentImpl.resetToApprovedStage(exp);
        ExperimentImpl.resetToEditingStage(exp);
        
        // Place new experiment bean into session:
        ExperimentBean newExpBean = ExpBeanReqManager.putExperimentIntoSessionExperimentBean(exp);
        
        log.info("commandSaveExperimentAs: ExpBean: "+oldExpBean.getEname()+" saved as "+newExpBean.getEname());

        // Test hard redirect:
        //NewExpWizardController.redirectToExpStage(exp.getEntityID(), 1);
        
        // Return generic result, to avoid JSF navigation taking over.
        //return "success";
        return "goToStage1";
    }
    
    /**
     * @param entityID
     * @param i
     */
    private static void redirectToExpStage(long eid, int i) {
        if( i < 1 || i > 6 ) i = 1;
        JSFUtil.redirect("/exp/exp_stage"+i+".faces?eid="+eid);
    }

    private String commandSaveExperimentAndGoto(int stage, String destination ) {
        String result = commandSaveExperiment( stage );
        log.info("Save: "+result);
        if( "success".equals(result)) {
            return destination;
        } else {
            return result;
        }
    }
    /*
    private String commandSaveExperimentAs() {
        return null;
    }
    */
    public String commandSaveAs() {
        return this.commandSaveExperimentAs();
    }
    
    public String commandSaveStage1() {
        return this.commandSaveExperiment(1);
    }
    
    public String commandSaveStage1AndGotoStage2() {
        return this.commandSaveExperimentAndGoto(1, "goToStage2");
    }

    public String commandSaveStage2() {
        return this.commandSaveExperiment(2);
    }
    
    public String commandSaveStage2AndGotoStage3() {
        return this.commandSaveExperimentAndGoto(2, "goToStage3");
    }

    public String commandSaveStage3() {
        return this.commandSaveExperiment(3);
    }
    
    /**
     * This one saves and attempts to submit.
     * @return
     */
    public String commandSaveStage3AndSubmit() {
        String result = this.commandSaveExperiment(3);
        if( "success".equals(result)) {
            return submitForApproval();
        } else {
            return result;
        }
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
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = expBean.getExperiment();   	
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
			//FacesContext facesContext = FacesContext.getCurrentInstance();
			
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
		TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = expBean.getExperiment();
        
        //clear the already added data in the backing bean
		expBean.removeAllExperimentInputData();
		expBean.setOpartionSelectionCompleted(false);
		
		//clear the already added data from the exp. executable
		if(exp.getExperimentExecutable()!=null){
//			exp.getExperimentExecutable().removeAllInputData();
			//this has a constructor which requires the ServiceTemplate to be set.
//			exp.removeExperimentExecutable();
			testbedMan.updateExperiment(exp);
		}
		
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
    	Map<String,String> oriMap = AdminManagerImpl.getInstance().getExperimentTypeIDsandNames();
    	Map<String,String> expTypeMap = new HashMap<String,String>();
    	Iterator<String> iter = oriMap.keySet().iterator();
    	while(iter.hasNext()){
    		String key = iter.next();
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
    	Map<String,String> ret = new TreeMap<String,String>();
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
    	//test if at least one template exists
    	if((templates!=null)&&(templates.size()>0)){
	    	//add data for rendering
			Iterator<TestbedServiceTemplate> itTemplates = templates.iterator();
			while(itTemplates.hasNext()){
				TestbedServiceTemplate template = itTemplates.next();
				//use the deployment date to extend the service name for selection
				String format = "yyyy-MM-dd HH:mm:ss";
				String date = doFormat(format, template.getDeploymentDate());
				ret.put(template.getName()+" - "+date+" - "+template.getDescription(),String.valueOf(template.getUUID()));
			}
			
			//only triggered for the first time
			if(expBean.getSelectedServiceTemplate()==null){
				expBean.setSelServiceTemplateID(ret.values().iterator().next());
				reloadOperations();
			}
    	}
    	return ret;
    }
    
    /**
     * Formats a gregorian calendar according to a specified format input schema
     * @param format
     * @param gc
     * @return
     */
    private String doFormat(String format, Calendar gc){
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	FieldPosition fpos = new FieldPosition(0);

    	StringBuffer b = new StringBuffer();
    	StringBuffer sb = sdf.format(gc.getTime(), b, fpos);

    	return sb.toString();
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
    	Iterator<String> iter = ev.getAllAcceptedEvaluationValues().iterator();
    	while (iter.hasNext()) {
    		String v = iter.next();
    		map.put(v, v);
    	}       	
    	return map;
    }
    
    
    
    /**
     * Retrieves the available BenchmarkGoals and adds the data into a BenchmarkBean
     * BMBean contains information e.g. on if it's selected, if an auto evaluation service is available etc.
     * for backing the GUI elements. Finally a list of List<BenchmarkBean> is put into the session map as "BenchmarkBeans".
     * @return
     */
    public String getRetrieveBenchmarkBeans() {
       	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	
       	//the available BenchmarkGoals as BenchmarkBean (only from the selected category)
       	Map<String,BenchmarkBean> availBenchmarks = new HashMap<String,BenchmarkBean>(expBean.getExperimentBenchmarks());
       	//get all BenchmarkGoals
       	Iterator<BenchmarkGoal> iter = BenchmarkGoalsHandlerImpl.getInstance().getAllBenchmarkGoals().iterator();
    	//the information if a BMGoal can be used within an autoEvaluationService
       	//Map<BMGoalID, EvaluationTestbedServiceTemplate>
       	Map<String,TestbedServiceTemplate> mapAutoEvalSer = this.getSupportedAutoEvaluationBMGoals();
       	
    	while (iter.hasNext()) {
    		BenchmarkGoal bmg = iter.next();
                Iterator<String> dtypeiter = expBean.getDtype().iterator();
                while (dtypeiter.hasNext()){
                    String currentType = dtypeiter.next();
                    DigitalObjectTypesImpl dtypeImpl = new DigitalObjectTypesImpl();
                    currentType = dtypeImpl.getDtypeName(currentType);
                    //only add the goals that match the already selected categories
                    if (currentType.equalsIgnoreCase(bmg.getCategory())) {
                        BenchmarkBean bmb = null;
                    	if (availBenchmarks.containsKey(bmg.getID())) {
                                bmb = availBenchmarks.get(bmg.getID());
                                bmb.setSelected(true);
                                
                        } else {
                                bmb = new BenchmarkBean(bmg);  			
                                bmb.setSelected(false);
                                availBenchmarks.put(bmg.getID(), bmb);
                        }
                        
                        //add the autoEvaluationService Information for this goal
                        if(mapAutoEvalSer.containsKey(bmg.getID())){
                        	//note: currently every BMGoal only contains max. one autoEval services behind
                            /* FIXME ANJ Clean this up:
                        	bmb.setAutoEvalService(mapAutoEvalSer.get(bmg.getID()));
                        	*/
                        }
                    }
                }
    	}
       // create and put BenchmarkBeans into session 
        List<BenchmarkBean> bmbeans = new ArrayList<BenchmarkBean>(availBenchmarks.values());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("BenchmarkBeans",bmbeans);
        return "";
    }
       
    
	    private void autoApproveExperiment(){
	    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	    	Experiment exp = expBean.getExperiment();
	    	
	    	// Approve the experiment, automatically:
	    	AdminManagerImpl.approveExperimentAutomatically(exp);
	
	    	// Update the Experiment Bean:
	        testbedMan.updateExperiment(exp);
	        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEXECUTION);
	        expBean.setApproved(true);
	    }
    

	   /**
	    * 
	    * @return
	    */
	   public void executeExperiment(){
	    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	    	Experiment exp = expBean.getExperiment();
	    	
	    	// NOT if already running.
	    	if( exp.getExperimentExecutable().isExecutionRunning() ) return;
	    	
	    	try {
	    		//call invocation on the experiment's executable
	    		testbedMan.executeExperiment(exp);
	    	} catch (Exception e) {
	    		log.error("Error when executing Experiment: " + e.toString());
	    		if( log.isDebugEnabled() ) e.printStackTrace();
	    	}
            log.info("Status: Invoked = "+exp.getExperimentExecutable().isExecutableInvoked());
            log.info("Status: Invoked = "+exp.getExperimentExecution().isExecutionInvoked());
            exp.getExperimentExecutable().setExecutableInvoked(true);
            exp.getExperimentExecutable().setExecutionCompleted(false);
            // Store any changes:
            testbedMan.updateExperiment(exp);
            log.info("Status: Invoked = "+exp.getExperimentExecutable().isExecutableInvoked());
            log.info("Status: Invoked = "+exp.getExperimentExecution().isExecutionInvoked());
	  }

	  public void executeExperimentStart() {
              this.executeExperiment();
	  }
	  public boolean getExecuteExperimentIsRunning() {
	      // Return:
          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          Experiment exp = expBean.getExperiment();
          boolean running = exp.getExperimentExecutable().isExecutionRunning();
          return running;
	  }
	  public int getExecuteExperimentProgress() {
          TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          Experiment exp = expBean.getExperiment();
          boolean running = exp.getExperimentExecutable().isExecutionRunning();
          
          if( running == false ) {
              if( exp.getExperimentExecutable().isExecutionCompleted() ) return 101;
              return -1;
          }
          
          log.info("Looking for experiment status... "+exp.getExperimentExecutable().getBatchExecutionIdentifier());
          
          TestbedBatchProcessor tbp = (TestbedBatchProcessor)JSFUtil.getManagedObject("TestbedBatchProcessor");
          String job_key = exp.getExperimentExecutable().getBatchExecutionIdentifier();
          log.info("Looking for experiment status under job key: "+job_key+" : " + tbp.getJobStatus(job_key));

          if( tbp.getJobStatus(job_key).equals(TestbedBatchJob.NO_SUCH_JOB ) ) {
              log.info("Got No Such Job.");
              exp.getExperimentExecutable().setExecutionSuccess(false);
              exp.getExperimentExecutable().setExecutionCompleted(true);
              testbedMan.updateExperiment(exp);
              return -1;
          } else if( tbp.getJobStatus(job_key).equals(TestbedBatchJob.NOT_STARTED) ) {
              log.info("Got NOT STARTED.");
              return 0;
          } else if( tbp.getJobStatus(job_key).equals(TestbedBatchJob.RUNNING) ) {
              int percent = tbp.getJobPercentComplete(job_key);
              log.info("Got percent complete:" + percent);
              return percent;

          } else {
              log.info("Got job complete.");
              this.recordExperimentCompleted(tbp.getJob(job_key));
              return 101;
          }
	  }
	  
	  private void recordExperimentCompleted(TestbedBatchJob job ) {
          TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          Experiment exp = expBean.getExperiment();

          // Update the experiment from the job:
          WorkflowResult.recordJobToExperiment( job, exp );
          
          // Store:
          testbedMan.updateExperiment(exp);
	  }
	  
	  /*
	   * This is a debug option, allowing experiments to be reset and re-run if they fail.
	   */
	  public String commandResetAfterFailure() {
          TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          // Save these changes:
          Experiment exp = expBean.getExperiment();
          ExperimentImpl.resetToApprovedStage(exp);
          testbedMan.updateExperiment(exp);
	      return "success";
	  }
		 
	 /**
	  * controller logic for handling the automated evaluation of an experiment
	  * currently the workflow is hardcoded (Droid->XCDL)
	  * @return
	  */
	 public String executeAutoEvalWf(){
	   	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	    ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
	   	Experiment exp = expBean.getExperiment(); 
   		
	    expBean.setExecuteAutoEvalWfRunning(true);
	   	
	   	//call invocation on the evaluation workflow
   		testbedMan.executeAutoEvaluationWf(exp);
       	
   		//update the data
   		testbedMan.updateExperiment(exp);
   		
   	    expBean.setExecuteAutoEvalWfRunning(false);
	    return "goToStage6";
    }
    
	/**
	 * Indicates if/not an autoEvaluation workflow is within execution
	 * @return
	 */
	public boolean isExecuteAutoEvalWfRunning(){
		 ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		 return expBean.isExecuteAutoEvalWfRunning();
	}
	
	public String getAutoEvalWFRunningSeconds(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		 return expBean.getAutoEvalWFRunningSeconds();
	}
	
	private String test ="A";
	public String getTestHelper(){
		return test+="1";
	}
    
    public String proceedToEvaluation() {
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = expBean.getExperiment(); 
  	  	if (exp.getExperimentExecution().isExecutionCompleted()) {
  	    	exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
  	    	exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);  	  	
  	  		expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTEVALUATION);
  	  		initEvaluationBenchmarks(exp);
  	  		testbedMan.updateExperiment(exp);
    		return "goToStage6";
  	  	} else
    		return null;
    }
    
    /**
     * Takes the Benchmarks set within the Setup phase and adds them as 
     * File Benchmarks for every input file and as experiment overall evaluation benchmarks
     */
    public void initEvaluationBenchmarks(Experiment exp){
    	log.debug("In NewExpWizardControler.initEvaluationBenchmarks...");
        try {

	    	List<BenchmarkGoal> lFileEvalBMgoals = new ArrayList<BenchmarkGoal>();
	    	List<BenchmarkGoal> lExpOverallEvalBMgoals = new ArrayList<BenchmarkGoal>();
	    	for(BenchmarkGoal bmg : exp.getExperimentSetup().getAllAddedBenchmarkGoals()){
	    		BenchmarkGoal bmgEval = ((BenchmarkGoalImpl)bmg).clone();
	    		lFileEvalBMgoals.add(bmgEval);
	    		BenchmarkGoal bmgEval2 = ((BenchmarkGoalImpl)bmg).clone();
	    		lExpOverallEvalBMgoals.add(bmgEval2);
	    	}
	    	
	    	//fill the benchmark goals for every input file - used for evaluation
	    	Map<URI,Collection<BenchmarkGoal>> fileEvalBMGs = new HashMap<URI,Collection<BenchmarkGoal>>();
	    	for(URI inputFileURI : exp.getExperimentExecutable().getAllInputHttpDataEntries()){
	    		//iterate over every input file and add it's BM goals
	    		fileEvalBMGs.put(inputFileURI,lFileEvalBMgoals);
	    	}
	    	
	    	//fill the file bm goals 
	    	exp.getExperimentEvaluation().setInputFileBenchmarkGoals(fileEvalBMGs);
	    	//fill the benchmark goals for experiment overall evaluation
	    	exp.getExperimentEvaluation().setInputExperimentBenchmarkGoals(lExpOverallEvalBMgoals);
	        log.debug("Having set: # of "+exp.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoals().size()+" experiment evaluation BMGoals");
	        log.debug("Having set: # of "+exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoals()+" file evaluation BMGoals");
        } catch (Exception e) {
        	log.error("Exception when trying to init the experiment evaluation benchmark goals: "+e.toString());
        }
    }
    
    /**
     * Please take care as this object is null within the first and second stage
     * @return
     */
    public ExperimentExecutable getExperimentExecutable(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
       	Experiment exp = expBean.getExperiment();
       	return exp.getExperimentExecutable();
    }

    public String saveEvaluation(){
    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	Experiment exp = expBean.getExperiment();
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
    
    /**
     * The filter on stage3 for selecting which BMGoal category to display
     * Please note the selection is already pre-filtered by the
     * experiment digital object type from page 1. 
     * @param vce
     */
    public void processBMGoalCategoryFilterChange(ValueChangeEvent vce){
		String sCatName = (String)vce.getNewValue();
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		expBean.setBMGoalCategoryFilterValue(sCatName);
	}
    
    //TODO: Andrew DELETE OR CHECK IF USED
    public void processSelBMValueTicked(ValueChangeEvent e){
    	//System.out.println("Andrew to DELETE" +e.getNewValue());
    	//FacesContext context = FacesContext.getCurrentInstance();
		//Object o1 = context.getExternalContext().getRequestParameterMap().get("bmGoalID2");
		//Object o2 = context.getExternalContext().getRequestParameterMap().get("bmGoalID");
    }
    
    public String command_configureAutoEvalService(){
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("configBMGoalID");
		Object o2 = context.getExternalContext().getRequestParameterMap().get("autoEvalSerUUID");
		String BMGoalID="";
		String autoEvalSerUUID="";
		if((o1!=null)&&(o2!=null)){
			 BMGoalID = o1.toString();
			 autoEvalSerUUID = o2.toString();
		}
		
		//save the current BMGoal selection
		String success = this.updateBenchmarksAction();
		
		if((success.equals("success"))&&(!BMGoalID.equals(""))&&(!autoEvalSerUUID.equals(""))){
			//create the config gui backing bean
			AutoBMGoalEvalUserConfigBean evalConfBean = new AutoBMGoalEvalUserConfigBean();
			//BenchmarkBean contains the BMGoal+EvaluationSerTemplate to configure
			evalConfBean.initBean(BMGoalID,autoEvalSerUUID);
			Manager manager_backing = (Manager)JSFUtil.getManagedObject("Manager_Backing");
			//takes the bean and puts it into the session map
			manager_backing.reinitAutoBMGoalEvalUserConfigBean(evalConfBean);
			//load screen for configuring the autoEvalSerivce
			return "configAutoEvalSer";
		}
		else{
			//an error has occured - display the page with error message
			return success;
		}
    }
    
    /**
     * Returns a map of ALL BenchmarkGoals that are registered within a
     * EvaluationServiceTemplate and therefore can be evaluated automatically
     * by using the corresponding EvaluationServiceTemplate
     * @return Map<BenchmarkGoalID, EvaluationTestbedServiceTemplateImpl>
     */
    public Map<String, TestbedServiceTemplate> getSupportedAutoEvaluationBMGoals(){
    	Map<String, TestbedServiceTemplate> ret = new HashMap<String, TestbedServiceTemplate>();
    	ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
    	Collection<TestbedServiceTemplate> evalSerTemplates = registry.getAllServicesWithType(TestbedServiceTemplate.ServiceOperation.SERVICE_OPERATION_TYPE_EVALUATION);
    	if((evalSerTemplates!=null)&&(evalSerTemplates.size()>0)){
    		Iterator<TestbedServiceTemplate> itTemplates = evalSerTemplates.iterator();
    		//The template which registeres the BMGoal mapping
    		EvaluationTestbedServiceTemplateImpl evalSerTemplate = (EvaluationTestbedServiceTemplateImpl) itTemplates.next();
    		
    		//all supported BM goalsIDs of this ServiceTemplate
    		Collection<String> lBMGoalIDs = evalSerTemplate.getAllMappedBenchmarkGoalIDs();
    		if((lBMGoalIDs!=null)&&(lBMGoalIDs.size()>0)){
    			Iterator<String> itBMGoalIDs = lBMGoalIDs.iterator();
    			while(itBMGoalIDs.hasNext()){
    				String bmGoalID = itBMGoalIDs.next();
    				//add the bmGoal and it's template
    				ret.put(bmGoalID, evalSerTemplate);
    			}
    		}
    	}
    	return ret;
    }

/*
 * END methods for new_experiment wizard page
 * -------------------------------------------
 */

    /**
     * Imports and experiment from an uploaded file
     */
    public String importUploadedExperiment() {
        FileUploadBean uploadBean = UploadManager.uploadFile(false);
        log.info("Looking for upload.");
        if( uploadBean == null ) return "failure";
        File uploaded = uploadBean.getTemporaryFile();
        log.info("Found upload: "+uploaded);
        UploadManager upm = (UploadManager) JSFUtil.getManagedObject("UploadManager");
        return upm.importExperiment(uploaded);
    }
    

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
			
			Iterator<UIComponent> it = panel.getChildren().iterator();
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
    
	/* ------------------------------------------------------------------------------- */
	
	public List<ExperimentStageBean> getStages() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        ExpTypeBackingBean exptype = ExpTypeBackingBean.getExpTypeBean(expBean.getEtype());
	    return exptype.getStageBeans();
	}
	
	/* The observables list */
	
	HtmlDataTable obsTable = new HtmlDataTable();
	HtmlDataTable obsManualTable = new HtmlDataTable();
	String obsEType = null;
    List<MeasurementBean> obs = null;
    List<MeasurementBean> obsManual = null;
	
    /**
     * @return the obsTable for automatically measured properties
     */
    public HtmlDataTable getObsTable() {
        return obsTable;
    }

    /**
     * @param obsTable the obsTable to set for automatically measured properties
     */
    public void setObsTable(HtmlDataTable obsTable) {
        this.obsTable = obsTable;
    }
    
    /**
     * @return the obsTable for manually measured properties
     */
    public HtmlDataTable getManualObsTable() {
        return this.obsManualTable;
    }

    /**
     * @param obsTable the obsTable to set for manually measured properties
     */
    public void setManualObsTable(HtmlDataTable manualobsTable) {
        this.obsManualTable = manualobsTable;
    }

    /**
     * @return The list of automatically measurable properties, depending on the experiment type.
     */
    public List<MeasurementBean> getObservables() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // Use the stage to narrow the list:
        String selectedStage = expBean.getSelectedStage().getName();
        this.chooseObservablesForEtype(expBean.getEtype(), expBean.getExperiment(), selectedStage );
        return obs;
    }
    
    /**
     * @return The list of manually measurable properties, depending on the experiment type.
     */
    public List<MeasurementBean> getManualObservables() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // Use the stage to narrow the list:
        String selectedStage = expBean.getSelectedStage().getName();
        this.chooseManualObservablesForEtype(expBean.getEtype(), expBean.getExperiment(), selectedStage );
        return obsManual;
    }
    
    private void chooseManualObservablesForEtype(String etype, Experiment exp, String stage){
    	this.obsManual = this.getObservablesForEtype(etype, exp, stage, true);
    }
    
    private void chooseObservablesForEtype(String etype, Experiment exp, String stage){
    	this.obs = this.getObservablesForEtype(etype, exp, stage, false);
    }
    
    /**
     * Returns a list of MeasurementBeans for a given experimentType and experiment instance and experiment stage
     * @param etype
     * @param exp
     * @param stage
     * @param manualObs flag if manual or automatically measurable properties are retrieved
     * @return
     */
    private List<MeasurementBean> getObservablesForEtype(String etype, Experiment exp, String stage, boolean manualObs) {

    	List<MeasurementBean> ret = new ArrayList<MeasurementBean>();
    	ExpTypeBackingBean exptype = ExpTypeBackingBean.getExpTypeBean(etype);
    	if(exptype != null){
    		if(manualObs){
        		ret = this.createMeasurementBeans(exptype.getManualObservables(), stage);
        	}
        	else{
        		ret = this.createMeasurementBeans(exptype.getObservables(), stage);
        	}
    	}
    	else {
            // For unrecognised experiment types return empty list:
            return ret;
        }

        // Determine 'selected' state for this observable:
    	Vector<String> props =null;
        if(manualObs){
        	props = exp.getExperimentExecutable().getManualProperties(stage);
    	}
    	else{
    		props = exp.getExperimentExecutable().getProperties();
    	}
        
        for( MeasurementBean m : ret ) {
            if( props != null && m.getIdentifier() != null && props.contains(m.getIdentifier().toString()) ) {
                m.setSelected(true);
            } else {
                // FIXME This should remember properly, and set to false if necessary.
            	//m.setSelected(false);
                m.setSelected(true);
            }
        }
        return ret;
    }
    
    /**
     * @param measurements
     * @return
     */
    private List<MeasurementBean> createMeasurementBeans( HashMap<String,List<MeasurementImpl>> measurements, 
            String selectedStage ) {
        List<MeasurementBean> mbeans = new ArrayList<MeasurementBean>();
        for( String stage: measurements.keySet() ) {
            if( selectedStage == null || selectedStage.equals(stage) ) {
                for( MeasurementImpl measurement : measurements.get(stage) ) {
                    MeasurementBean measurebean =  new MeasurementBean(measurement);
                    measurebean.setSelected(true);
                    measurebean.setStage(stage);
                    mbeans.add(measurebean);
                }
            }
        }
        return mbeans;
    }
    
    /**
     * 
     * @param valueChangedEvent
     */
    public void handleObsSelectChangeListener(ValueChangeEvent valueChangedEvent) {
        log.info("Handling event in handleObsSelectChangeListener.");
        
        MeasurementImpl targetBean = (MeasurementImpl) this.getObsTable().getRowData();
        
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        Vector<String> props = expBean.getExperiment().getExperimentExecutable().getProperties();
        if( props.contains(targetBean.getIdentifier().toString()) ) {
            props.remove(targetBean.getIdentifier().toString());
            log.info("Removed: "+targetBean.getIdentifier());
        } else {
            props.add(targetBean.getIdentifier().toString());
            log.info("Added: "+targetBean.getIdentifier());
        }
        
    }
    
    /**
     * 
     * @param valueChangedEvent
     */
    public void handleManualObsSelectChangeListener(ValueChangeEvent valueChangedEvent) {
        log.info("Handling event in handleManualObsSelectChangeListener.");
        
        //the measurement we're operating upon
        MeasurementImpl targetBean = (MeasurementImpl) this.getManualObsTable().getRowData();
        
        //the experiment bean
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		String etype = expBean.getEtype();
		//the experiment type backing bean
    	ExpTypeBackingBean exptype = ExpTypeBackingBean.getExpTypeBean(etype);
    	//the model element
		Vector<String> props = expBean.getExperiment().getExperimentExecutable().getManualProperties(targetBean.getStage());
		// Use the stage to narrow the list:
        String stageName = expBean.getSelectedStage().getName();

        if( props.contains(targetBean.getIdentifier().toString()) ) {
            //remove from the model
        	expBean.getExperiment().getExperimentExecutable().removeManualProperty(stageName, targetBean.getIdentifier().toString());
            //remove from the bean
        	exptype.getManualObservables().get(stageName);
        	MeasurementImpl removeMeasurement=null;
        	for(MeasurementImpl m : exptype.getManualObservables().get(stageName)){
        		if(m.getIdentifier().toString().equals(targetBean.getIdentifier().toString())){
        			removeMeasurement = m;		
        		}
        	}
        	if(removeMeasurement!=null){
        		exptype.getManualObservables().get(stageName).remove(removeMeasurement);
        	}
        	
        	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	    	testbedMan.updateExperiment(expBean.getExperiment());
	    	
            log.info("Removed: "+targetBean.getIdentifier());
        } else {
            /*props.add(targetBean.getIdentifier().toString());
            log.info("Added: "+targetBean.getIdentifier());*/
        }
        
    }
    
    /**
     * Takes the properties from the OntologBrowser component and adds them
     * as Measurements on the current experiment.
     */
    public String addOntoPropsToExp(){
    	
    	log.info("Adding properties from the ontology to the experiment");	
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	String etype = expBean.getEtype();
    	ExpTypeBackingBean exptype = ExpTypeBackingBean.getExpTypeBean(etype);
    	
    	if( etype.equals( AdminManagerImpl.IDENTIFY ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeIdentify");
        } else if( etype.equals( AdminManagerImpl.MIGRATE ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeMigrate");
        } else {
            // For unrecognised experiment types, set to NULL:
            log.error("unrecognised experiment type");
        	return "goToStage3";
        }
    	
    	//get the information from the ontology tree bean
    	FacesContext ctx = FacesContext.getCurrentInstance();
		PropertyDnDTreeBean treeBean = (PropertyDnDTreeBean)JSFUtil.getManagedObject("simpleTreeDndBean");
		if(treeBean==null){
			// ontology tree bean has not been set
	        log.error("ontology tree bean not set");
	        return "goToStage3";
		}
		
		//the bean's manual observables
		HashMap<String, List<MeasurementImpl>> props = exptype.getManualObservables();
    	
		//now iterate over all added properties
		Map<String,HashMap<String,String>> stagePropsSel = treeBean.getStageSelectedState();
		List<OntologyProperty> selProps = treeBean.getSelectedOntologyProperties();
		
		try {	
			for(ExperimentStageBean stageb : exptype.getStageBeans()){
				for(OntologyProperty prop : selProps){
					String pURI = prop.getURI();
					if(stagePropsSel.get(pURI) !=null){
						String val = stagePropsSel.get(pURI).get(stageb.getName());
						if(val!=null){
						
							//create a MeasurementImpl from the OntologyProperty
							MeasurementImpl measurement = OntoPropertyUtil.createMeasurementFromOntologyProperty(prop);
							
							//check if this property was already added
							boolean bContained = false;
							MeasurementImpl measurementContained=null;
							List<MeasurementImpl> lExistingMeasurements = props.get(stageb.getName());
							for(MeasurementImpl m : lExistingMeasurements){
								if(m.getIdentifier().equals(measurement.getIdentifier())){
									bContained = true;
									measurementContained = m;
								}
							}
							
							//add new
							if(val.equals("true")){
								//add the Measurement to the experimentType's backing bean
					    		if(! bContained) {
					    			lExistingMeasurements.add(measurement);
					                log.info("Added manual property: "+measurement.getIdentifier());
					            }
					    		//update the model's information
						    	expBean.getExperiment().getExperimentExecutable().addManualProperty(stageb.getName(), measurement.getIdentifier().toString());
							}
							
							//remove existing
							if(val.equals("false")){
								//remove the Measurement from the experimentType's backing bean
					    		if(bContained) {
					    			lExistingMeasurements.remove(measurementContained);
					                log.info("Removed manual property: "+measurement.getIdentifier());
					            }
					    		
					    		//update the model's information
						    	expBean.getExperiment().getExperimentExecutable().removeManualProperty(stageb.getName(), measurement.getIdentifier().toString());
							}
						}
					}
					
				}
			}
			
			//store the updated experiment
	    	TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	    	testbedMan.updateExperiment(expBean.getExperiment());
		    	
		} catch (Exception e) {
			log.debug("error building Measurement from OntologyProperty",e);
		}
		
		return "goToStage3";

    }

}
