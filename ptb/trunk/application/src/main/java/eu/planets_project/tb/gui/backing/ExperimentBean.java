package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.myfaces.component.html.ext.HtmlDataTable;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.finals.DigitalObjectTypesImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;


public class ExperimentBean {
	
	public static final int PHASE_EXPERIMENTSETUP_1   = 1;
	public static final int PHASE_EXPERIMENTSETUP_2   = 2;
	public static final int PHASE_EXPERIMENTSETUP_3   = 3;
	public static final int PHASE_EXPERIMENTAPPROVAL   = 4;
	public static final int PHASE_EXPERIMENTEXECUTION  = 5;
	public static final int PHASE_EXPERIMENTEVALUATION = 6;
	
	// To avoid the data held here falling out of date, store the experiment:
	Experiment exp = null;
               
	private Log log = LogFactory.getLog(ExperimentBean.class);
	private long id;
	private boolean formality = true;
	private String ename = new String();
    private String esummary = new String();
    private String econtactname = new String();
    private String econtactemail = new String();
    private String econtacttel = new String();
    private String econtactaddress = new String();
    private String epurpose = new String();
    private String efocus = new String();
    private String eparticipants = new String();

    private String litrefdesc = new String();
    private String litrefuri = new String();
    private Long eref;
    private String exid = new String();

    private String escope = new String();
    private String eapproach = new String();
    private String econsiderations = new String();
    private String etype;
    private String etypeName;  
    //the selected TBServiceTemplate's ID
    private TestbedServiceTemplate selSerTemplate;
    private String sSelSerTemplateID="";
    private String sSelSerOperationName="";
    private UIComponent panelAddedFiles = new UIPanel();
    private boolean bOperationSelectionCompleted = false;
    //tomahawk data table binding for input/output data table
    private HtmlPanelGrid inputOutputTable = new HtmlPanelGrid();
    
    private Map<String,BenchmarkBean> benchmarks = new HashMap<String,BenchmarkBean>();
    private String intensity="0";

    //The input file refs with Map<Position+"",localFileRef>
    private Map<String,String> inputData = new HashMap<String,String>();
    //distinguish between migration and characterisation output results
    //output in the form of localInputFile Ref and localFileRef/String
    private Collection<Map.Entry<String,String>> outputData = new Vector<Map.Entry<String,String>>();
    
    private int currStage = ExperimentBean.PHASE_EXPERIMENTSETUP_1;
    private boolean approved = false;
    
    private List dtype = new ArrayList();
    private List dtypeList = new ArrayList();
    private DigitalObjectTypesImpl dtypeImpl = new DigitalObjectTypesImpl();
    private List<String[]> fullDtypes = new ArrayList<String[]>();
        
    public ExperimentBean() {
    	/*benchmarks = new HashMap<String,BenchmarkBean>();
    	Iterator iter = BenchmarkGoalsHandlerImpl.getInstance().getAllBenchmarkGoals().iterator();
    	while (iter.hasNext()) {
    		BenchmarkGoal bm = (BenchmarkGoal)iter.next();
    		benchmarks.put(bm.getID(), new BenchmarkBean(bm));
    	}*/
        
        fullDtypes = dtypeImpl.getAlLDtypes();
        
        for(int i=0;i<fullDtypes.size();i++) {
            
            String[] tmp = fullDtypes.get(i);
            
            SelectItem option = new SelectItem(tmp[0],tmp[1]);
            dtypeList.add(option);  
        }    
    }
    
    public void fill(Experiment exp) {
        log.debug("Filling the ExperimentBean with experiment: "+ exp.getExperimentSetup().getBasicProperties().getExperimentName());

        this.exp = exp; 
    	ExperimentSetup expsetup = exp.getExperimentSetup();
    	BasicProperties props = expsetup.getBasicProperties();
    	this.id = exp.getEntityID();
    	this.ename =(props.getExperimentName());
    	this.escope=(props.getScope());
    	this.econsiderations=(props.getConsiderations());
    	this.econtactaddress=(props.getContactAddress());
    	this.econtactemail=(props.getContactMail());
    	this.econtacttel=(props.getContactTel());
    	this.econtactname=(props.getContactName());
        
        // references
        this.exid=props.getExternalReferenceID();
        
        List<String[]> lit = props.getAllLiteratureReferences();
        if (lit != null && !lit.isEmpty()) {
        	String[] l = lit.get(0);
        	this.litrefdesc = l[0];
        	this.litrefuri = l[1];
        }       
        
        List<Long> refs = props.getExperimentReferences();
        if (refs != null && !refs.isEmpty()) {
        	this.eref = refs.get(0);
        }        
        List<String> involvedUsers = props.getInvolvedUserIds();
        String partpnts = " ";
        for(int i=0;i<involvedUsers.size();i++) {
            partpnts +=involvedUsers.get(i);
            if( i < involvedUsers.size()-1 ) partpnts += ", ";
        }
        

        this.eparticipants = partpnts;
        
        String Test = props.getExternalReferenceID();
        
        this.exid=(Test);
        

        this.efocus=(props.getFocus());

    	this.epurpose=(props.getPurpose());
    	this.esummary=(props.getSummary());
    	this.formality = props.isExperimentFormal();    	
    	this.etype = String.valueOf(expsetup.getExperimentTypeID());
        this.etypeName = AdminManagerImpl.getInstance().getExperimentTypeName(this.etype);
    	
        //get already added TestbedServiceTemplate data
        if(exp.getExperimentExecutable()!=null){
        	ExperimentExecutable executable = exp.getExperimentExecutable();
        	this.selSerTemplate = executable.getServiceTemplate();
        	this.sSelSerTemplateID = selSerTemplate.getUUID();
        	this.sSelSerOperationName = executable.getSelectedServiceOperationName();
        	helperLoadInputData(executable.getInputData());
        	if(executable.isExecutionSuccess()){
        		//uses the executable to get the data
            	this.outputData = exp.getExperimentExecutable().getOutputDataEntries();
            	//fill the bound table
            	this.buildIODataTable();
        	}
        }

    	// set benchmarks
    	//TODO einkommentieren
        /*try {
    		if (this.inputData != null) {
    			Iterator<BenchmarkGoal> iter;
    			if (exp.getCurrentPhase() instanceof ExperimentEvaluation) 
    				iter = exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoals(new URI(inputData)).iterator();
    			else
    				iter = exp.getExperimentSetup().getAllAddedBenchmarkGoals().iterator();
    			while (iter.hasNext()) {
		    		BenchmarkGoal bm = iter.next();
		    		BenchmarkBean bmb = new BenchmarkBean(bm);
					bmb.setSourceValue(bm.getSourceValue());
					bmb.setTargetValue(bm.getTargetValue());
					bmb.setEvaluationValue(bm.getEvaluationValue());
					bmb.setWeight(String.valueOf(bm.getWeight()));
					bmb.setSelected(true);
		    		benchmarks.put(bm.getID(), bmb);
    			}
    			//this.outputData = eworkflow.getOutputData().toArray()[0].toString();
    			if (exp.getExperimentExecution() != null)
    				this.outputData = (exp.getExperimentExecution().getExecutionOutputData((new URI(this.inputData)))).toString();
    		}
        } catch (Exception e) {
        	log.error("Exception when trying to create ExperimentBean from database object: "+e.toString());        	
        }*/
    	// merge information to benchmark beans    	
    	/*Iterator iter = exp.getExperimentSetup().getAllAddedBenchmarkGoals().iterator();
    	while (iter.hasNext()) {
    		BenchmarkGoal bmg = (BenchmarkGoal)iter.next();
    		if (benchmarks.containsKey(bmg.getID())) {
    			BenchmarkBean bmb = benchmarks.get(bmg.getID());
    			bmb.setValue(bmg.getValue());
    			bmb.setWeight(String.valueOf(bmg.getWeight()));
    			bmb.setSelected(true);
    		}
    	}*/
    	String intensity = Integer.toString(exp.getExperimentSetup().getExperimentResources().getIntensity());
    	if (intensity != null && intensity != "-1") 
    		this.intensity = intensity;
    	// determine current Stage
    	ExperimentPhase currPhaseObj = exp.getCurrentPhase();
    	if (currPhaseObj != null) {
    		String currPhase = currPhaseObj.getPhaseName();
	    	if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTSETUP)) {
	    		this.currStage = exp.getExperimentSetup().getSubStage();
	    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTAPPROVAL)) {
	    		this.currStage = ExperimentBean.PHASE_EXPERIMENTAPPROVAL;
	    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTEXECUTION)) {
	    		this.currStage = ExperimentBean.PHASE_EXPERIMENTEXECUTION;
	    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTEVALUATION)) {
	    		this.currStage = ExperimentBean.PHASE_EXPERIMENTEVALUATION;
	    	}
    	}
	    if(currStage>ExperimentBean.PHASE_EXPERIMENTSETUP_3)
	    	approved=true;
	    
        
        this.dtype = props.getDigiTypes();
    }
    //END OF FILL METHOD
    
    public Map<String,BenchmarkBean> getBenchmarks() {
		return benchmarks;    	
    }
    
    public List<BenchmarkBean> getBenchmarkBeans() {
    	return new ArrayList<BenchmarkBean>(benchmarks.values());
    }
    
    public void addBenchmarkBean(BenchmarkBean bmb) {
    	this.benchmarks.put(bmb.getID(),bmb);
    }
    
    public void deleteBenchmarkBean(BenchmarkBean bmb) {
    	this.benchmarks.remove(bmb.getID());
    }
    
    public void setBenchmarks(Map<String,BenchmarkBean>bms) {
    	this.benchmarks = bms;
    }
    
    /**
     * Returns the selected service template UUID
     * @return
     */
    public String getSelectedServiceTemplateID() {
    	if (selSerTemplate !=null)
    		return selSerTemplate.getUUID();
    	else
    		return this.sSelSerTemplateID;
    }
    
    public void setSelServiceTemplateID(String sID){
    	this.sSelSerTemplateID = sID;
    	setSelectedServiceTemplate(sID);
    }
    
    public void setSelectedServiceOperationName(String sName){
    	this.sSelSerOperationName = sName;
    }
    
    public String getSelectedServiceOperationName(){
    	return this.sSelSerOperationName;
    }
    
    /**
     * Sets the selected object's id and also fetches the object from the registry
     * @param sID
     */
    public void setSelectedServiceTemplate(String sID){
    	ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
    	this.selSerTemplate = registry.getServiceByID(sID);
    }
    
    public TestbedServiceTemplate getSelectedServiceTemplate(){
    	return this.selSerTemplate;
    }

    public ServiceOperation getSelectedServiceOperation(){
    	return this.getSelectedServiceTemplate().getServiceOperation(
    			this.getSelectedServiceOperationName()
    			);
    }
    
    /**
     * Returns a Map of added file Refs
     * Map<position+"",fileRef>
     * @return
     */
    public Map<String,String> getExperimentInputData() {
    	return this.inputData;
    }
    
    /**
     * Returns the position where this item has been added
     * @param localFileRef
     * @return
     */
    public String addExperimentInputData(String localFileRef) {
    	String key = getNextInputDataKey();
    	if(!this.inputData.values().contains(localFileRef)){
    		this.inputData.put(key, localFileRef); 
    	}
    	return key;
    }
    
    public void removeExperimentInputData(String key){
    	if(this.inputData.containsKey(key)){
    		this.inputData.remove(key);
    	}
    }
    
    public void removeAllExperimentInputData(){
    	this.inputData = new HashMap<String,String>();
    }
    
    /**
     * As the InputData HashMap should be filled up with IDs without any 
     * gap, this method is used to find the next possible key
     * @return
     */
    private String getNextInputDataKey(){
    	boolean bFound = false;
    	int count = 0;
    	while(!bFound){
    		//int the case that no item was added until now - index is 0
    		if((this.inputData.size()<=0)||(!this.inputData.containsKey(count+""))){
    			bFound = true;
    		}
    		else{
    			count++;
    		}
    	}
    	return count+"";
    }
    
    
    public void setOutputData(Collection<Entry<String,String>> data){
    	this.outputData = data;
    }
    
    /**
     * Retrieves the URI references as UIComponents 
     * i.e. all localFileRefs are converted to URIs and wraped within an
     * HtmlOutputLink link. In the case of an characterisation experiment
     * the results are put into a HtmlOutputText telement
     * @return
     */
    public Collection<Entry<UIComponent,UIComponent>> getOutputDataForGUI(){
    	//Entry of inputComponent, outputComponent
    	Collection<Entry<UIComponent,UIComponent>> ret = new Vector<Entry<UIComponent,UIComponent>>();
    	Iterator<Entry<String,String>> itData = this.outputData.iterator();
    	
    	FacesContext facesContext = FacesContext.getCurrentInstance();
    	DataHandler dh = new DataHandlerImpl();
    	int count =0;
    	while(itData.hasNext()){
    		Entry<String,String> entry = itData.next();
    		String input = entry.getKey();
    		String output = entry.getValue();
    		UIComponent componentInput = null;
    		UIComponent componentOutput = null;
    		
    	 //For the Input:
    		try{
    		//test: convert input to URI
    			URI uriInput = dh.getHttpFileRef(new File(input), true);
    			//wrap uri as output link - get its original file name as label
    			HtmlOutputText outputText = (HtmlOutputText) facesContext
					.getApplication().createComponent(
						HtmlOutputText.COMPONENT_TYPE);
    			outputText.setValue(dh.getIndexFileEntryName(new File(input)));
    			outputText.setId("inputFileName" + count+"");
    			
    			//use its URI as file Output Link
    			HtmlOutputLink link_src = (HtmlOutputLink) facesContext
					.getApplication().createComponent(
						HtmlOutputLink.COMPONENT_TYPE);
    			link_src.setId("inputFileRef" + count+"");
    			link_src.setValue("file:///" + uriInput);	
    			link_src.getChildren().add(outputText);
    			//add to return
    			componentInput = link_src;
    		}
    		catch(Exception e){
    			//this input was not a file or fileRef not readable  - display as text
    			//wrap input as outputText
    			HtmlOutputText outputText = (HtmlOutputText) facesContext
					.getApplication().createComponent(
						HtmlOutputText.COMPONENT_TYPE);
    			outputText.setValue(dh.getIndexFileEntryName(new File(input)));
    			outputText.setId("inputFileName" + count+"");
    			componentInput = outputText;
    		}
    		
    	 //For the Output:
    		try{
        		//test: convert output to URI
        		URI uriOutput = dh.getHttpFileRef(new File(output), false);
        		//wrap uri as output link - get its original file name as label
        		HtmlOutputText outputText = (HtmlOutputText) facesContext
    				.getApplication().createComponent(
    					HtmlOutputText.COMPONENT_TYPE);
        		outputText.setValue(output);
        		outputText.setId("outputFileName" + count+"");
        			
        		//use its URI as file Output Link
        		HtmlOutputLink link_src = (HtmlOutputLink) facesContext
    				.getApplication().createComponent(
    					HtmlOutputLink.COMPONENT_TYPE);
        		link_src.setId("outputFileRef" + count+"");
        		link_src.setValue("file:///" + uriOutput);	
        		link_src.getChildren().add(outputText);
        		//add to return
        		componentOutput = link_src;
        	}
        	catch(Exception e){
        		//this input was not a file or fileRef not readable  - display as text
        		//wrap input as outputText
        		HtmlOutputText outputText = (HtmlOutputText) facesContext
    				.getApplication().createComponent(
    					HtmlOutputText.COMPONENT_TYPE);
        		outputText.setValue(output);
        		outputText.setId("outputFileName" + count+"");
        		componentOutput = outputText;
        	}  
        	HashMap<UIComponent,UIComponent> helper = new HashMap<UIComponent,UIComponent>();
        	helper.put(componentInput, componentOutput);
        	ret.add(helper.entrySet().iterator().next());
        	count++;
    	}
    	return ret;
    }

	public String getNumberOfOutput() {
		return this.outputData.size()+"";
	}
	
	public int getNumberOfInputFiles(){
		return this.inputData.values().size();
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}
	
	public String getIntensity(){
		return this.intensity;
	}
	
    public void setID(long id) {
    	this.id = id;
    }
    
    public long getID(){
    	return this.id;
    }
    
    public void setFormality(boolean formality) {
        this.formality = formality;
    }
    
    public boolean getFormality() {
        return formality;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }
    
    public String getEname() {
        return ename;
    }
    
    public void setEsummary(String esummary) {
        this.esummary = esummary;
    }
    
    public String getEsummary() {
        return esummary;
    }
    
    public String getEcontactname() {
        return econtactname;
    }
    
    public void setEcontactname(String econtactname) {
        this.econtactname = econtactname;
    }
    
    public String getEcontactemail() {
        return econtactemail;
    }
    
    public void setEcontactemail(String econtactemail) {
        this.econtactemail = econtactemail;
    }
    
    public String getEcontacttel() {
        return econtacttel;
    }
    
    public void setEcontacttel(String econtacttel) {
        this.econtacttel = econtacttel;
    }
    
    public String getEcontactaddress() {
        return econtactaddress;
    }
    
    public void setEcontactaddress(String econtactaddress) {
        this.econtactaddress = econtactaddress;
    }
    
    public String getEpurpose() {
        return epurpose;
    }
    
    public void setEpurpose(String epurpose) {
        this.epurpose = epurpose;
    }
    
    public String getEfocus() {
        return efocus;
    }
    
    public void setEfocus(String efocus) {
        this.efocus = efocus;
    }
    
    public String getEscope() {
        return escope;
    }
    
    public void setEscope(String escope) {
        this.escope = escope;
    }
    
    public String getEapproach() {
        return eapproach;
    }
    
    public void setEapproach(String eapproach) {
        this.eapproach = eapproach;
    }
    
    public String getEconsiderations() {
        return econsiderations;
    }
    
    public void setEconsiderations(String econsiderations) {
        this.econsiderations = econsiderations;
    }
  
    public void setEtype(String type) {
    	this.etype = type;
    }
    
    public String getEtype() {
    	return this.etype;
    }
    
    public String getEtypeName() {
        if (etype != null) 
        	return AdminManagerImpl.getInstance().getExperimentTypeName(etype);
        return null;
    }
    
    public int getCurrentStage() {
    	return this.currStage;
    }
    
    public void setCurrentStage(int cs) {
    	this.currStage = cs;
    }
    
    public boolean getApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    public void setExid(String exid) {
    	this.exid = exid;
    }
    
    public String getExid() {
    	return this.exid;
    }
    
    public void setLitRefDesc(String litref) {
    	this.litrefdesc = litref;
    }
    
    public String getLitRefDesc() {
    	return this.litrefdesc;
    }

    public void setLitRefURI(String uri) {
    	this.litrefuri = uri;
    }
    
    public String getLitRefURI() {
    	return this.litrefuri;
    }

    public void setEparticipants(String eparticipants) {
    	this.eparticipants = eparticipants;
    }
    
    public String getEparticipants() {
    	return this.eparticipants;
    }

    public Long getEref() {
        return eref;
    }
   
    public void setEref(Long eref) {
    	this.eref = eref;
    }
    
    public List getDtype() {
        return dtype;
    }
    
    public void setDtype(List dtype) {
    	this.dtype = dtype;
    }
    
    public List getDtypeList() {
        if( dtypeList == null ) return new ArrayList();
        return dtypeList;
    }
   
    public void setDtypeList(List dtypeList) {
    	this.dtypeList = dtypeList;
    }

    /**
     * Gets a list of all the phases of this experiment.
     * @return List of ExperimentPhaseBean, one for each possible Phase.
     */
    public List<ExperimentPhaseBean> getPhaseBeans() {
        return java.util.Arrays.asList(getPhaseBeanArray());
    }
    
    private ExperimentPhaseBean[] getPhaseBeanArray() {
        // TODO ANJ Surely there is a better way of organising this:
        log.debug("Building array of ExperimentPhaseBeans");
        ExperimentPhaseBean[] phaseBeans = new ExperimentPhaseBean[7]; 
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTSETUP_1] =  
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTSETUP);
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTSETUP_2] =  
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTSETUP);
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTSETUP_3] = 
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTSETUP);
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTAPPROVAL] =
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTAPPROVAL);
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTEXECUTION] =
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTEXECUTION);
        phaseBeans[ExperimentBean.PHASE_EXPERIMENTEVALUATION] =
                new ExperimentPhaseBean(this, ExperimentPhase.PHASENAME_EXPERIMENTEVALUATION);
        return phaseBeans;
    }
    
    public String getCurrentPhaseName() {
        return exp.getCurrentPhase().getPhaseName();
    }
    
    /**
     * Helper to load already entered input data from the experiment's executable
     * into this backing bean
     * @param fileRefs
     */
    private void helperLoadInputData(Collection<String> fileRefs){
    	Iterator<String> itFileRefs = fileRefs.iterator();
    	int i =0;
    	while(itFileRefs.hasNext()){
    		this.inputData.put(i+"",itFileRefs.next());
    		i++;
    	}
    }
    
    public boolean isOperationSelectionCompleted(){
    	return this.bOperationSelectionCompleted;
    }
    
    /**
     * Marks the process of selecting service + operation as completed
     * Note: selecting an other service or operation after this step leads to
     * loosing all added input data
     * @param b
     */
    public void setOpartionSelectionCompleted(boolean b){
    	this.bOperationSelectionCompleted = b;
    }
    
    /**
     * The panel to render all added file inputs
     * @return
     */
    public UIComponent getPanelAddedFiles(){
    	return this.panelAddedFiles;
    }
    
    public void setPanelAddedFiles(UIComponent panel){
    	this.panelAddedFiles = panel;
    }
    
    /**
     * Returns the binding for the input/output data table
     * @param table
     */
    public void setInputOutputTable(HtmlPanelGrid table){
    	this.inputOutputTable = table;
    }
    
    public HtmlPanelGrid getInputOutputTable(){
    	return this.inputOutputTable;
    }
    
    public void buildIODataTable(){
    	HtmlPanelGrid table = this.getInputOutputTable();
    	table = new HtmlPanelGrid();
    	//table.setId("inputOutputTable");
    		
    	//table.setPreserveDataModel(false);
    	//table.setCellpadding("2");
    	//table.setCellspacing("0");
    	//table.setBorder(1);
    	//table.setStyle("border: 1px solid #579EC2;");
    	
    	table.setColumns(2);
    	/*UIColumn col1 = new UIColumn();
    	col1.setId("colInputData1");
    	UIOutput header1 = new HtmlOutputText();
    	header1.setId("header1");
    	header1.setValue("Input Files");
    	col1.setHeader(header1);*/
    	
    	/*UIColumn col2 = new UIColumn();
    	col2.setId("colInputData2");
    	UIOutput header2 = new HtmlOutputText();
    	header2.setId("header2");
    	header2.setValue("Output Files");
    	col2.setHeader(header2);  	*/
    	
    	/*Iterator<Entry<UIComponent,UIComponent>> itIO = getOutputDataForGUI().iterator();
    	while(itIO.hasNext()){
    		Entry<UIComponent,UIComponent> entry = itIO.next();
    		//input
    		try{
    			HtmlOutputLink input = (HtmlOutputLink)entry.getKey();
    			col1.getChildren().add(input);
    		}catch(Exception e){
    			HtmlOutputText input= (HtmlOutputText)entry.getKey();
    			col1.getChildren().add(input);
    		}
    		//output
    		try{
    			HtmlOutputLink output = (HtmlOutputLink)entry.getKey();
    			col2.getChildren().add(output);
    		}catch(Exception e){
    			HtmlOutputText output= (HtmlOutputText)entry.getKey();
    			col2.getChildren().add(output);
    		}
    	}*/
    	
    	HtmlOutputText outputText = new HtmlOutputText();
    	outputText.setValue("TestValue");
    	outputText.setId("outputFileName1");
    	//col1.getChildren().add(outputText);
    	
    	/*table.getChildren().add(col1);
    	table.getChildren().add(col2);
    	table.setRows(1);*/
    	table.getChildren().add(outputText);
    }
}
