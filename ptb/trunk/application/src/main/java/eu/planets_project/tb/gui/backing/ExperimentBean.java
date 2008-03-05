package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.component.UIPanel;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.component.html.ext.HtmlDataTable;
//import org.apache.myfaces.component.html.ext.HtmlDataTable;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.finals.DigitalObjectTypesImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;


public class ExperimentBean {
	
	public static final int PHASE_EXPERIMENTSETUP_1   = 1;
	public static final int PHASE_EXPERIMENTSETUP_2   = 2;
	public static final int PHASE_EXPERIMENTSETUP_3   = 3;
	public static final int PHASE_EXPERIMENTAPPROVAL   = 4;
	public static final int PHASE_EXPERIMENTEXECUTION  = 5;
    public static final int PHASE_EXPERIMENTEVALUATION = 6;
    public static final int PHASE_EXPERIMENTFINALIZED   = 7;
	
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

    private String exid = new String();
    private ArrayList<String> eref = new ArrayList<String>();
    private ArrayList<String> litrefdesc = new ArrayList<String>();
    private ArrayList<String> litrefuri = new ArrayList<String>();

    private String escope = new String();
    private String eapproach = new String();
    private String econsiderations = new String();
    private String etype;
    private String etypeName;  
    //the selected TBServiceTemplate's ID
    private TestbedServiceTemplate selSerTemplate;
    private String sSelSerTemplateID="";
    private String sSelSerOperationName="";
    private UIComponent panelAddedSerTags = new UIPanel();
    private UIComponent panelAddedFiles = new UIPanel();
    private boolean bOperationSelectionCompleted = false;
    
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
    
    private String ereportTitle;
    private String ereportBody;
    
    //a list of added annotationTagNames to restrict the displayed services
    private Map<String,ServiceTag> mapAnnotTagVals = new HashMap<String,ServiceTag>();
    //triggers the XMLResponds in case of an error for stage6
    private boolean bRenderXMLResponds = false;
        
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
        
        // Add in default data:
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        if( user != null ) {
            this.eparticipants = user.getUserid();
            this.econtactname = user.getFullname();
            this.econtactemail = user.getEmail();
            this.econtacttel = user.getTelephone();
            this.econtactaddress = user.getAddress();
        }
        // Default spaces:
        this.litrefdesc.add("");
        this.litrefuri.add("");
    }
    
    public void fill(Experiment exp) {
        log.debug("Filling the ExperimentBean with experiment: "+ exp.getExperimentSetup().getBasicProperties().getExperimentName());
        log.debug("Experiment Phase Name = " + exp.getPhaseName());
        log.debug("Experiment Current Phase " + exp.getCurrentPhase());
        log.debug("Experiment Current Phase Stage is " + exp.getCurrentPhase().getState());

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
            this.litrefdesc = new ArrayList<String>(lit.size());
            this.litrefuri = new ArrayList<String>(lit.size());
            for( int i = 0; i < lit.size(); i++ ) {
        	  this.litrefdesc.add(i, lit.get(i)[0]);
        	  this.litrefuri.add(i,lit.get(i)[1]);
            }
        }       
        
        List<Long> refs = props.getExperimentReferences();
        if (refs != null && !refs.isEmpty()) {
            for( int i = 0; i < refs.size(); i++ )
                this.eref.add(i, ""+refs.get(i) );
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
        log.debug("fill expBean: executable = "+exp.getExperimentExecutable());
        if(exp.getExperimentExecutable()!=null){
        	ExperimentExecutable executable = exp.getExperimentExecutable();
        	this.selSerTemplate = executable.getServiceTemplate();
        	if( selSerTemplate != null )
        	    this.sSelSerTemplateID = selSerTemplate.getUUID();
        	this.sSelSerOperationName = executable.getSelectedServiceOperationName();
        	this.bOperationSelectionCompleted = true;
        	helperLoadInputData(executable.getInputData());
        	if(executable.isExecutionSuccess()){
        		//uses the executable to get the data
            	this.outputData = exp.getExperimentExecutable().getOutputDataEntries();
        	}
        }

    	// set benchmarks
        try {
    		if (this.inputData != null) {
    			Iterator<BenchmarkGoal> iter;
    			if (exp.getCurrentPhase() instanceof ExperimentEvaluation) { 
                    iter = exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoals().iterator();
    			} else {
    				iter = exp.getExperimentSetup().getAllAddedBenchmarkGoals().iterator();
    			}
    			while (iter.hasNext()) {
		    		BenchmarkGoal bm = iter.next();
		    		BenchmarkBean bmb = new BenchmarkBean(bm);
					bmb.setSourceValue(bm.getSourceValue());
					bmb.setTargetValue(bm.getTargetValue());
					bmb.setEvaluationValue(bm.getEvaluationValue());
					bmb.setWeight(String.valueOf(bm.getWeight()));
					bmb.setSelected(true);
		    		benchmarks.put(bm.getID(), bmb);
                    //log.debug("Filling bmg: target:" + bmb.getTargetValue());
    			}
    			//this.outputData = eworkflow.getOutputData().toArray()[0].toString();
    			/*
    			if( this.outputData == null ) outputData = new String[this.inputData.length];
    			if (exp.getExperimentExecution() != null && 
    			        exp.getExperimentExecution().getExecutionOutputData((new URI(this.inputData[i]))) != null )
    				this.outputData[i] = (exp.getExperimentExecution().getExecutionOutputData((new URI(this.inputData[i])))).toString();
    			*/       
              }
        } catch (Exception e) {
        	log.error("Exception while setting benchmarks, during attempt to create ExperimentBean from database object: "+e.toString());
        	if( log.isDebugEnabled() ) e.printStackTrace();
        }
    	// merge information to benchmark beans    	
        /* Seems not to be needed, and overwrites the Target value data during evaluation:
    	Iterator iter = exp.getExperimentSetup().getAllAddedBenchmarkGoals().iterator();
    	while (iter.hasNext()) {
    		BenchmarkGoal bmg = (BenchmarkGoal)iter.next();
    		if (benchmarks.containsKey(bmg.getID())) {
    			BenchmarkBean bmb = benchmarks.get(bmg.getID());
    			bmb.setTargetValue(bmg.getTargetValue());
    			bmb.setWeight(String.valueOf(bmg.getWeight()));
    			bmb.setSelected(true);
    		}
    	}
    	*/
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
            } else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTFINALIZED)) {
                this.currStage = ExperimentBean.PHASE_EXPERIMENTFINALIZED;                
            }
    	}
	    if(currStage>ExperimentBean.PHASE_EXPERIMENTSETUP_3)
	    	approved=true;
	    
        
        this.dtype = props.getDigiTypes();
        
        // Set the report up:
        this.ereportTitle = exp.getExperimentEvaluation().getExperimentReport().getHeader();
        this.ereportBody = exp.getExperimentEvaluation().getExperimentReport().getBodyText();
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
    
    public void removeSelectedServiceTemplate(){
    	this.selSerTemplate = null;
    	this.sSelSerTemplateID = null;
    	this.sSelSerOperationName = null;
    }

    public ServiceOperation getSelectedServiceOperation(){
      if( this.selSerTemplate != null ) {
    	  TestbedServiceTemplate template = this.getSelectedServiceTemplate();
    	  List<String> allOpNames = template.getAllServiceOperationNames();
    	  String selOpName = this.getSelectedServiceOperationName();
    	  //template contains no operations
    	  if(allOpNames.size()==0)
    		  return null;
    	  //template is being changed - select first op as selected
    	  if(!allOpNames.contains(selOpName)){
    		  this.setSelectedServiceOperationName(allOpNames.iterator().next());
    	  }
    	  
    	  return template.getServiceOperation(this.getSelectedServiceOperationName());
      } 
      return null;
    }
    
    /**
     * Returns a Map of added file Refs
     * Map<position+"",fileRef>
     * @return
     */
    public Map<String,String> getExperimentInputData() {
        log.debug("getting experiment input data: "+this.inputData);
        return this.inputData;
    }
    
    /**
     * Get the list of files as a simple String collection.
     * @return
     */
    public Collection<String> getExperimentInputDataFiles() {
        return this.inputData.values();
    }
    
    /**
     * Returns a map containing the input data's uri as key and its corresponding
     * original logical file name as value
     * e.g. Collection<Map<"http://../planets-testbed/inputdata/fdsljfsdierw.doc,"data1.doc">>
     * This is used to render as dataTable within the GUI
     * @return
     */
    public Collection<Map<String,String>> getExperimentInputDataNamesAndURIs(){
    	Collection<Map<String,String>> ret = new Vector<Map<String,String>>();
    	DataHandler dh = new DataHandlerImpl();
    	Iterator<String> localFileRefs = this.getExperimentInputDataFiles().iterator();
    	while(localFileRefs.hasNext()){
    		try {
    			Map<String,String> map = new HashMap<String,String>();
    			//retrieve URI
    			File fInput = new File(localFileRefs.next());
				URI uri = dh.getHttpFileRef(fInput, true);
				String name = dh.getInputFileIndexEntryName(fInput);
				map.put("uri", uri.toString());
				map.put("name", name);
				ret.add(map);
			} catch (FileNotFoundException e) {
				log.error(e.toString());
			} catch (URISyntaxException e) {
				log.error(e.toString());
			}
    	}
    	return ret;
    }
        
    
    /**
     * Returns the position where this item has been added
     * @param localFileRef
     * @return
     */
    public String addExperimentInputData(String localFileRef) {
        log.debug("Adding input file: "+localFileRef);
    	String key = getNextInputDataKey();
    	if(!this.inputData.values().contains(localFileRef)){
    		this.inputData.put(key, localFileRef); 
    		// Also add to UI component:
    		UIComponent panel = this.getPanelAddedFiles();
    		this.helperCreateRemoveFileElement(panel, localFileRef, key);
            return key;
    	} else {
    	    return null;
        }
    }
    
    public void removeExperimentInputData(String key){
    	if(this.inputData.containsKey(key)){
    		this.inputData.remove(key);
    	}
    }
    
    public void removeAllExperimentInputData(){
    	this.inputData = new HashMap<String,String>();
    	//remove all added delete-link GUI elements
    	this.getPanelAddedFiles().getChildren().clear();
    }
    
    /**
     * As the InputData HashMap should be filled up with IDs without any 
     * gap, this method is used to find the next possible key
     * @return
     */
    private String getNextInputDataKey(){
    	boolean bFound = true;
    	int count = 0;
    	while(bFound){
    		// Check if the current key is already in use: 
    		if(this.inputData.containsKey(count+"")){
                count++;
    		}
    		else{
                bFound = false;
    		}
    	}
    	log.debug("Returning input data key: "+count+" / "+inputData.size());
    	return count+"";
    }
    
    
    public void setOutputData(Collection<Entry<String,String>> data){
    	this.outputData = data;
    }
    
    
    /**
     * Returns the data used in the experiment's input-output data table
     * @return
     */
    public Collection<Map<String,String>> getIOTableDataForGUI(){
    	//Entry of inputComponent, outputComponent
    	Collection<Map<String,String>> ret = new Vector<Map<String,String>>();
    	Iterator<Entry<String,String>> itData = this.outputData.iterator();
    	
    	DataHandler dh = new DataHandlerImpl();
    	while(itData.hasNext()){
    		Entry<String,String> entry = itData.next();
    		String input = entry.getKey();
    		String output = entry.getValue();
    		
    		HashMap<String,String> hm = new HashMap<String,String>();
    	 //For the Input:
    		try{
    		//test: convert input to URI
    			URI uriInput = dh.getHttpFileRef(new File(input), true);
    			//add "inputURI" and "inputName" into ret hashmap
    			hm.put("input", uriInput.toString());
    			hm.put("inputName", dh.getInputFileIndexEntryName(new File(input)));
    			hm.put("inputTypeURI", "URI");
    			
    		}
    		catch(Exception e){
    			//this input was not a file or fileRef not readable  - display as text
    			hm.put("input", input);
    			hm.put("inputName", null);
    			hm.put("inputTypeURI", null);
    		}
    		
    	 //For the Output:
    		try{
        		//test: convert output to URI
        		URI uriOutput = dh.getHttpFileRef(new File(output), false);
        		//add "outputURI" and "outputName" "outputType" into ret hashmap
    			hm.put("output", uriOutput.toString());
    			hm.put("outputName", dh.getOutputFileIndexEntryName(new File(output)));
    			hm.put("outputTypeURI", "URI");
        	}
        	catch(Exception e){
        		//this input was not a file or fileRef not readable  - display as text
        		hm.put("output", output);
    			hm.put("outputName", null);
    			hm.put("outputType", null);
        	}  
        	ret.add(hm);
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
    
    public void setLitRefDesc(ArrayList<String> litref) {
    	this.litrefdesc = litref;
    }
    
    public ArrayList<String> getLitRefDesc() {
    	return this.litrefdesc;
    }

    public void setLitRefURI(ArrayList<String> uri) {
    	this.litrefuri = uri;
    }
    
    public ArrayList<String> getLitRefURI() {
    	return this.litrefuri;
    }
    
    public void addLitRefSpot() {
        // Add space for another lit ref:
        this.litrefdesc.add("");
        this.litrefuri.add("");
    }

    public void setEparticipants(String eparticipants) {
    	this.eparticipants = eparticipants;
    }
    
    public String getEparticipants() {
    	return this.eparticipants;
    }

    public ArrayList<String> getEref() {
        return eref;
    }

    public ArrayList<Experiment> getErefBeans() {
        ArrayList<Experiment> ert = new ArrayList<Experiment>();
        TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
        for( int i=0; i < this.eref.size(); i++ ) {
            Experiment erp = testbedMan.getExperiment((Long.parseLong(this.eref.get(i))));
            ert.add(i,erp);
        }
        return ert;
    }

    
    public void setEref(ArrayList<String> eref) {
    	this.eref = eref;
    }
    
    public Collection<SelectItem> getAllExperimentsSelectItems() {
        ListExp listExp = (ListExp)JSFUtil.getManagedObject("ListExp_Backing");
        
        ArrayList<SelectItem> expList = new ArrayList<SelectItem>();
        Collection<Experiment> allExps = listExp.getAllExperiments();
        for( Experiment exp : allExps ) {
            SelectItem item = new SelectItem();
            item.setValue( ""+exp.getEntityID() );
            item.setLabel(exp.getExperimentSetup().getBasicProperties().getExperimentName());
            if( exp.getEntityID() != this.getID() )
                expList.add(item);
        }
        return expList;
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
    
    public boolean isFinished() {
        if( this.currStage == ExperimentBean.PHASE_EXPERIMENTFINALIZED ) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getReportHeader() {
        log.debug("get Report Header: "+this.ereportTitle);
        return this.ereportTitle;
    }
    
    public void setReportHeader(String text) {
        log.debug("set Report Header: "+text);
        this.ereportTitle = text;
    }
    
    public String getReportBody() {
        return this.ereportBody;
    }
    
    public void setReportBody(String text) {
        this.ereportBody = text;
    }
    
    /**
     * Helper to load already entered input data from the experiment's executable
     * into this backing bean
     * @param fileRefs
     */
    private void helperLoadInputData(Collection<String> fileRefs){
        // Use a new, empty panel:
        this.panelAddedFiles = new UIPanel();
    	Iterator<String> itFileRefs = fileRefs.iterator();
    	int i =0;
    	while(itFileRefs.hasNext()){
    	    log.debug("helperLoadInputData: adding file: "+i);
    	    String fileRef = itFileRefs.next();
    		this.inputData.put(i+"", fileRef);
    		// Also add an item to the panel:
    	    helperCreateRemoveFileElement( this.panelAddedFiles, fileRef, i+"" );
    		i++;
    	}
    }
    
    public boolean isOperationSelectionCompleted(){
    	if(this.currStage <= PHASE_EXPERIMENTSETUP_2)
    		return this.bOperationSelectionCompleted;
    	return true;
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
     * The panel to restrict service selection by available tags
     * @param panel
     */
    public void setAddedSerTags(UIComponent panel){
    	this.panelAddedSerTags = panel;
    }
    
    public UIComponent getAddedSerTags(){  
    	return this.panelAddedSerTags;
    }
    
    
    /**
     * Creates the JSF Elements to render a given fileRef as CommandLink within the given UIComponent
     * @param panel
     * @param fileRef
     * @param key
     */
    public void helperCreateRemoveFileElement(UIComponent panel, String fileRef, String key){
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            DataHandler dh = new DataHandlerImpl();
            //file ref
            HtmlOutputText outputText = (HtmlOutputText) facesContext
                    .getApplication().createComponent(
                            HtmlOutputText.COMPONENT_TYPE);
            outputText.setValue(dh.getInputFileIndexEntryName(new File(fileRef)));
            outputText.setId("fileName" + key);
            //file name
            HtmlOutputLink link_src = (HtmlOutputLink) facesContext
                    .getApplication().createComponent(
                            HtmlOutputLink.COMPONENT_TYPE);
            link_src.setId("fileRef" + key);
            URI URIFileRef = dh.getHttpFileRef(new File(fileRef), true);
            link_src.setValue(URIFileRef);
            link_src.setTarget("_new");

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
        
    
    /*---------for step2 popup overlay-----------*/
    
    /**
     * Method adds a given service annotation tag and its value. Tags are
     * used to restrict the list of displayed services
     */
    public void addAnnotationTag(ServiceTag tag){
    	if(tag!=null){
    		this.mapAnnotTagVals.put(tag.getName(), tag);
    	}
    }
    
    /**
     * Method removes a given service annotation tag and its value. Tags are
     * used to restrict the list of displayed services
     * @param name
     */
    public void removeAnnotationTag(String name){
    	if(name!=null){
    		if(this.mapAnnotTagVals.containsKey(name)){
    			//remove tag
    			this.mapAnnotTagVals.remove(name);
    		}
    	}
    }
    
    /**
     * Method removes all service annotation tags and their values, which are
     * used to restrict the list of displayed services
     */
    public void removeAllAnnotationTags(){
    	this.mapAnnotTagVals = new HashMap<String,ServiceTag>();
    }
    
    /**
     * Returns a list of maps containing the selected annotation tag names and values
     * e.g. list<map<"name","author">,<"value","val1 or ""><"description","description1">..
     * @return
     */
    public Collection<ServiceTag> getSelectedAnnotationTags(){
    	Collection<ServiceTag> ret = new Vector<ServiceTag>();
    	return this.mapAnnotTagVals.values();
    }
    
    public String getServiceXMLResponds(){
    	return this.exp.getExperimentExecutable().getServiceXMLResponds();
    }
    
   
    /**
     * Triggers: display xml responds in case of an error on page 6
     * @param b
     */
    public void setViewXMLRespondsTrue(){
    	this.bRenderXMLResponds = true;
    }
    
    public void setViewXMLRespondsFalse(){
    	this.bRenderXMLResponds = false;
    }
    
    public boolean isViewXMLResponds(){
    	return this.bRenderXMLResponds;
    }
}
