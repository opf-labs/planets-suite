package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.finals.DigitalObjectTypesImpl;


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
    private ExperimentWorkflow eworkflow;    
    private String workflowtypeid;
    private Map<String,BenchmarkBean> benchmarks = new HashMap<String,BenchmarkBean>();
    private String intensity="0";
    private String nrOutputFiles="1";
    private String inputData;
    private String outputData;
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
    	this.eworkflow = exp.getExperimentSetup().getExperimentWorkflow();
    	if (this.eworkflow !=null) {
    		this.workflowtypeid=String.valueOf(eworkflow.getWorkflow().getEntityID());
   			if (eworkflow.getInputData()!=null)     			
   				this.inputData = eworkflow.getInputData().toArray()[0].toString();   			    		    		
    	}

    	// set benchmarks
    	try {
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
        }
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
    	String nroutputfiles = Integer.toString(exp.getExperimentSetup().getExperimentResources().getNumberOfOutputFiles());
    	if (nroutputfiles !=null && nroutputfiles != "-1") 
    		this.nrOutputFiles = nroutputfiles;
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
    
    public void setEworkflow(ExperimentWorkflow ewf) {
    	this.eworkflow = ewf;
    }
    
    public ExperimentWorkflow getEworkflow(){
    	return this.eworkflow;
    }
    
    public String getWorkflowTypeId() {
    	if (eworkflow !=null)
    		return String.valueOf(eworkflow.getWorkflow().getEntityID());
    	else
    		return this.workflowtypeid;
    }
    
    public void setWorkflowTypeId(String wftypeId) {
    	this.workflowtypeid = wftypeId;
    }
    
    public String getEworkflowInputData() {
    	return this.inputData;
    }
    
    public void setEworkflowInputData(String inputdata) {
    	this.inputData = inputdata;
    }
    
    public String getEworkflowOutputData() {
    	return this.outputData;
    }
    
    public void setEworkflowOutputData(String outputdata) {
    	this.outputData = outputdata;
    }

    public void setNumberOfOutputFiles(String nr) {
		this.nrOutputFiles = nr;
	}

	public String getNumberOfOutputFiles() {
		return this.nrOutputFiles;
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
    
}
