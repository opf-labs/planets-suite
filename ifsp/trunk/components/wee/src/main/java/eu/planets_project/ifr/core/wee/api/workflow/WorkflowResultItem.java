package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResultItem implements Serializable{
	
	public static final String GENERAL_WORKFLOW_ACTION = "general workflow action";
    public static final String SERVICE_ACTION_MIGRATION = "migration";
    //used to indicate the final migration that produces the output object
    public static final String SERVICE_ACTION_FINAL_MIGRATION = "migration.final";
    public static final String SERVICE_ACTION_IDENTIFICATION = "identification";
    public static final String SERVICE_ACTION_CHARACTERISATION = "characterisation";
    public static final String SERVICE_ACTION_CREATEVIEW = "create view";
    public static final String SERVICE_ACTION_COMPARE = "compare";
    
    private long startTime = -1;
    private long endTime = -1;
    private String sActionIdentifier="";
    //xml serialization of the digital object
    private String digoIn;
    //xml serialization of the digital object
    private String digoOut;
    private List<String> extractedInformation;
    private List<String> logInfo;
    private ServiceReport serReport;
    private String serviceEndpoint;
    //a list of the actually called serviceParameters
    private List<Parameter> serParams;
    //the inputDigitalObject reference on which the execute was called. This can be different from the digoIn.
    //e.g. if you identify-migrate-identify then this would have the same identifier (the digo ref the execute was called from) but different inputDigos.
    private URI aboutExecutionDigoRef;
    @XmlTransient
    private boolean aboutExecutionDigoDifferentThanInputDigo = false;
    
    //required for JAXB - even if empty
    private WorkflowResultItem(){
    	extractedInformation = new ArrayList<String>();
    	serParams = new ArrayList<Parameter>();
    	logInfo = new ArrayList<String>();
    }
    
    /**
     * The most common constructor - if no reference to the aboutDigo is given, then we assume that the inputDigitalObject
     * is also the one that the execute method is currently processing.
     * @param serviceActionIdentifier
     * @param startTime
     * @param endTime
     */
    public WorkflowResultItem(String serviceActionIdentifier, long startTime){
    	this(null,serviceActionIdentifier,startTime,-1);
    }
    
    public WorkflowResultItem(String serviceActionIdentifier, long startTime, long endTime){
    	this(null,serviceActionIdentifier,startTime,endTime);
    }
	
    /**
     * The most common constructor
     * @param aboutDigo The original reference for which the execute was called
     * @param serviceActionIdentifier
     * @param startTime
     * @param endTime
     */
    public WorkflowResultItem(DigitalObject aboutDigo, String serviceActionIdentifier, long startTime){
    	this(aboutDigo,serviceActionIdentifier,startTime,-1);
    }
    
	public WorkflowResultItem(DigitalObject aboutDigo,String serviceActionIdentifier, long startTime, long endTime){
		this();
		if(aboutDigo==null){
			aboutExecutionDigoDifferentThanInputDigo = false;
		}
		else{
			aboutExecutionDigoDifferentThanInputDigo = true;
			this.aboutExecutionDigoRef = aboutDigo.getPermanentUri();
		}
		this.sActionIdentifier = serviceActionIdentifier;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	public void setInputDigitalObject(DigitalObject inDigo){
		if(inDigo!=null){
			this.digoIn = inDigo.toXml();
			if(!aboutExecutionDigoDifferentThanInputDigo){
				this.aboutExecutionDigoRef = inDigo.getPermanentUri();
			}
		}
	}
	
	public void setOutputDigitalObject(DigitalObject outDigo){
		if(outDigo!=null){
			this.digoOut = outDigo.toXml();
			this.addLogInfo("Successfully added OutputDigitalObject");
		}
	}
	
	public void setExtractedInformation(List<String> information){
		this.extractedInformation = information;
	}
	
	public void addExtractedInformation(String information){
		this.extractedInformation.add(information);
	}
	
	public void addLogInfo(String logInfo){
		this.logInfo.add(logInfo);
	}
	
	public List<String> getLogInfo(){
		return this.logInfo;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
    /**
     * Duration if the start time and end time of the workflow-item have been set
     * @return
     */
    public long getDuration(){
    	if((this.getStartTime()!=-1)&&(this.getEndTime()!=-1)){
    		return this.getEndTime() - this.getStartTime();
    	}
    	return -1;
    }

	public String getSActionIdentifier() {
		return sActionIdentifier;
	}

	public void setSActionIdentifier(String actionIdentifier) {
		sActionIdentifier = actionIdentifier;
	}

	public DigitalObject getInputDigitalObject() {
		if(this.digoIn!=null){
			return new DigitalObject.Builder(this.digoIn).build();
		}
		else{
			return null;
		}
	}


	public DigitalObject getOutputDigitalObject() {
		if(this.digoOut!=null){
			return new DigitalObject.Builder(this.digoOut).build();
		}
		else{
			return null;
		}
	}


	public List<String> getExtractedInformation() {
		return extractedInformation;
	}


	public ServiceReport getServiceReport() {
		return serReport;
	}

	public void setServiceReport(ServiceReport serviceReport) {
		this.serReport = serviceReport;
	}
	
	/**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, actionIdentifier: %s,logInfo: %s, serviceReport: %s,  start-time: %s, end-time: %s", this
                .getClass().getSimpleName(), sActionIdentifier,logInfo, serReport, startTime, endTime);
    }

	public List<Parameter> getServiceParameters() {
		return serParams;
	}

	public void setServiceParameters(List<Parameter> serviceParams) {
		this.serParams = serviceParams;
	}

	public URI getAboutExecutionDigoRef() {
		return aboutExecutionDigoRef;
	}

	public void setAboutExecutionDigoRef(URI aboutExecutionDigoRef) {
		this.aboutExecutionDigoRef = aboutExecutionDigoRef;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(URL serviceEndpoint) {
		if(serviceEndpoint!=null){
			this.serviceEndpoint = serviceEndpoint.toExternalForm();
		}
		
	}

}
