package eu.planets_project.tb.gui.backing.exp;


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

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlInputTextarea;
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
    
	private Log log = LogFactory.getLog(NewExpWizardController.class);
        private HtmlInputTextarea ereport;
    
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
                
        String litRefDesc = expBean.getLitRefDesc();
        String litRefURI = expBean.getLitRefURI();     
	    List<String[]> refList = new ArrayList<String[]>();
        if (litRefDesc != null && !litRefDesc.equals("")) {
	    	refList.add(new String[]{litRefDesc,litRefURI});
        }
        try {
        	props.setLiteratureReferences(refList);
        } catch (InvalidInputException e) {
        	log.error("Problems setting literature references: "+e.toString());
        }
        List<Long> refs = new ArrayList<Long>();
        if (expBean.getEref() != null && !expBean.getEref().equals(""))
        	refs.add(expBean.getEref());
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
    
    //TODO:comment in again
    /*public String updateEvaluationAction() {
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
    }*/
    
      
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
	        }
	        
	        // store the provided input data
	        executable.setInputData(expBean.getExperimentInputData().values());	        
	        // store all other metadata
	        executable.setSelectedServiceOperationName(expBean.getSelectedServiceOperationName());
	        
	        //modify the experiment's stage information
	        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
	        exp.setState(Experiment.STATE_IN_PROGRESS);
	        testbedMan.updateExperiment(exp);
	        
	        expBean.setCurrentStage(ExperimentBean.PHASE_EXPERIMENTSETUP_3);	        	        
	    	return "goToStage3";
        } catch (Exception e) {
        	log.error("Exception when trying to create/update ExperimentWorkflow: "+e.toString());
        	return "failure";
        }
    }
    
    
    /**
     * In the process of selecting the proper TBServiceTemplate to work with
     * Reacts to changes within the selectOneMenu
     * @param ce
     */
    public void changedSelTBServiceTemplateEvent(ValueChangeEvent ce) {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	//also sets the beans selectedServiceTemplate (from the registry)
    	expBean.setSelServiceTemplateID(ce.getNewValue().toString());
    	//reloadOperations();
    }
    
    /**
     * In the process of selecting the proper TBServiceTemplate to work with
     * Reacts to changes within the selectOneMenu
     * @param ce
     */
    public void changedSelServiceOperationEvent(ValueChangeEvent ce) {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	expBean.setSelectedServiceOperationName(ce.getNewValue().toString());
    }
    
    /**
     * When the selected ServiceTemplate has changed - reload it's available
     * ServiceOperations.
     */
    /*private void reloadOperations(){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	expBean.setSelectedServiceOperationName("");
    }*/
    
    
    /**
     * A file has been slected for being uploaded and the add icon was pressed to add a reference
     * for this within the experiment bean
     * @return
     */
    public String commandAddInputDataItem(){
    	//0) upload the specified data to the Testbed's file repository
		FileUploadBean uploadBean = this.uploadFile();
		String fileRef = uploadBean.getLocalFileRef();
		if(!(new File(fileRef).canRead())){
			log.debug("Added file reference not correct or reachable by the VM "+fileRef);
		}
    	
    	//1) Add the file reference to the expBean
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	String position = expBean.addExperimentInputData(fileRef);
    	
    	//2) For the current fileRef create an GUI outputfield + a RemoveIcon+Link
    	UIComponent panel = this.getComponent("panelAddedFiles");
    	helperCreateRemoveFileElement(panel, fileRef, position);
    	
    	//reload stage2 and displaying the added data items
    	return "goToStage2";
    }
    
    /**
     * Creates the JSF Elements to render a given fileRef as CommandLink within the given UIComponent
     * @param panel
     * @param fileRef
     * @param key
     */
    private void helperCreateRemoveFileElement(UIComponent panel, String fileRef, String key){
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			//file ref
			HtmlOutputText outputText = (HtmlOutputText) facesContext
					.getApplication().createComponent(
							HtmlOutputText.COMPONENT_TYPE);
			outputText.setValue(fileRef);
			outputText.setId("fileName" + key);
			//file name
			HtmlOutputLink link_src = (HtmlOutputLink) facesContext
					.getApplication().createComponent(
							HtmlOutputLink.COMPONENT_TYPE);
			link_src.setId("fileRef" + key);
			DataHandler dh = new DataHandlerImpl();
			URI URIFileRef = dh.getHttpFileRef(new File(fileRef), true);
			link_src.setValue("file:///" + URIFileRef);

			//CommandLink+Icon allowing to delete this entry
			HtmlCommandLink link_remove = (HtmlCommandLink) facesContext
					.getApplication().createComponent(
							HtmlCommandLink.COMPONENT_TYPE);
			//set the ActionMethod to the method: "commandRemoveAddedFileRef(ActionEvent e)"
			Class[] parms = new Class[] { ActionEvent.class };
			MethodBinding mb = FacesContext.getCurrentInstance()
					.getApplication().createMethodBinding(
							"#{NewExp_Controller.commandRemoveAddedFileRef}",
							parms);
			link_remove.setActionListener(mb);
			link_remove.setId("removeLink" + key);
			//send along an helper attribute to identify which component triggered the event
			link_remove.getAttributes().put("IDint", key);
			HtmlGraphicImage image = (HtmlGraphicImage) facesContext
					.getApplication().createComponent(
							HtmlGraphicImage.COMPONENT_TYPE);
			image.setUrl("../graphics/button_delete.gif");
			image.setAlt("delete-image");
			image.setId("graphicRemove" + key);
			link_remove.getChildren().add(image);

			//add all three components
			panel.getChildren().add(link_remove);
			link_src.getChildren().add(outputText);
			panel.getChildren().add(link_src);
			
		} catch (Exception e) {
			log.error("error building components for file removal "+e.toString());
		}
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
			int IDnr = ((Integer)event.getComponent().getAttributes().get("IDint")).intValue();
			
			//2) Remove the data from the bean's variable
			ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
			expBean.removeExperimentInputData(IDnr+"");
			
			//3) Remove the GUI elements from the panel
			UIComponent comp_link_remove = this.getComponent("panelAddedFiles:removeLink"+IDnr);
		
			UIComponent comp_link_src = this.getComponent("panelAddedFiles:fileRef"+IDnr);
			getComponent("panelAddedFiles").getChildren().remove(comp_link_remove);
			//this.getComponentPanelStep3Add().getChildren().remove(comp_text);
			getComponent("panelAddedFiles").getChildren().remove(comp_link_src);

			
		} catch (Exception e) {
			// TODO: handle exception
		}
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
	public FileUploadBean uploadFile(){
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
    	if(template!=null){
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
    
    //TODO: comment in again
    /*public String executeExperiment(){
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
    }*/
    
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
    
    public String finishReaderStage6() {
        return "goToBrowseExperiments";
    }

    public HtmlInputTextarea getEreport() {
        return ereport;
    }
    
    public void setEreport(HtmlInputTextarea ereport) {
        this.ereport = ereport;
    }
/*
 * END methods for new_experiment wizard page
 * -------------------------------------------
 */
    
    /**
	 * Get the component from the JSF view model - it's id is registered withinin the page
	 * @return
	 */
	private UIComponent getComponent(String sID){

			FacesContext facesContext = FacesContext.getCurrentInstance();
			
			//the ViewRoot contains all children not only in sub-level1
			Iterator<UIComponentBase> it = facesContext.getViewRoot().getChildren().iterator();
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
