package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;


public class ExperimentBean {
	
	private Log log = LogFactory.getLog(ExperimentBean.class);
	private long id;
	private boolean eformality = true;
	private String ename = new String();
    private String esummary = new String();
    private String econtactname = new String();
    private String econtactemail = new String();
    private String econtacttel = new String();
    private String econtactaddress = new String();
    private String epurpose = new String();
    private String efocus = new String();
    private String erefs = new String();
    private String escope = new String();
    private String eapproach = new String();
    private String econsiderations = new String();
    private String etype;
    private ExperimentWorkflow eworkflow;    
    private String workflowtypeid;
    private Map<String,BenchmarkBean> benchmarks = new HashMap<String,BenchmarkBean>();
    private String intensity="0";
    private String nrOutputFiles="1";
    private String inputData;
    private String outputData;
    private int currStage =1;
    
        
    public ExperimentBean() {
    	/*benchmarks = new HashMap<String,BenchmarkBean>();
    	Iterator iter = BenchmarkGoalsHandlerImpl.getInstance().getAllBenchmarkGoals().iterator();
    	while (iter.hasNext()) {
    		BenchmarkGoal bm = (BenchmarkGoal)iter.next();
    		benchmarks.put(bm.getID(), new BenchmarkBean(bm));
    	}*/
    }
    
    public void fill(Experiment exp) {
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
    	this.efocus=(props.getFocus());
    	this.epurpose=(props.getPurpose());
    	this.esummary=(props.getSummary());
    	this.eformality = props.isExperimentFormal();    	
    	this.etype = String.valueOf(expsetup.getExperimentTypeID());
    	this.eworkflow = exp.getExperimentSetup().getExperimentWorkflow();
    	if (this.eworkflow !=null) {
    		this.workflowtypeid=String.valueOf(eworkflow.getWorkflow().getEntityID());
   			if (eworkflow.getInputData()!=null)     			
   				this.inputData = eworkflow.getInputData().toArray()[0].toString();   			    		    		
    	}
    	
    	// set benchmarks
    	try {
    		if (this.inputData != null) {
    			Iterator iter = exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoals(new URI(inputData)).iterator();
    			while (iter.hasNext()) {
		    		BenchmarkGoal bm = (BenchmarkGoal)iter.next();
		    		BenchmarkBean bmb = new BenchmarkBean(bm);
					bmb.setValue(bm.getValue());
					bmb.setWeight(String.valueOf(bm.getWeight()));
					bmb.setSelected(true);
		    		benchmarks.put(bm.getID(), bmb);
    			}
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
    	String currPhase = exp.getCurrentPhase().getPhaseName();
    	if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTSETUP)) {
    		this.currStage = exp.getExperimentSetup().getSubStage();
    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTAPPROVAL)) {
    		this.currStage = 4;
    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTEXECUTION)) {
    		this.currStage = 5;
    	} else if (currPhase.equals(ExperimentPhase.PHASENAME_EXPERIMENTEVALUATION)) {
    		this.currStage = 6;
    	}
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
        this.eformality = formality;
    }
    
    public boolean getFormality() {
        return eformality;
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
    
    public String getErefs() {
        return erefs;
    }
    
    public void setErefs(String erefs) {
        this.erefs = erefs;
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

}
