package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResultItem implements Serializable{
	
    public static final String SERVICE_ACTION_MIGRATION = "migration";
    public static final String SERVICE_ACTION_IDENTIFICATION = "identification";
    public static final String SERVICE_ACTION_CHARACTERISATION = "characterisation";
    public static final String SERVICE_ACTION_CREATEVIEW = "create view";
    
    private long startTime = -1;
    private long endTime = -1;
    private String sActionIdentifier="";
    //xml serialization of the digital object
    private String digoIn;
    //xml serialization of the digital object
    private String digoOut;
    private List<String> extractedInformation;
    private String logInfo = "";
    private ServiceReport serReport;
    
    //required for JAXB - even if empty
    private WorkflowResultItem(){
    	extractedInformation = new ArrayList<String>();
    }
	
    /**
     * The most common constructor
     * @param serviceActionIdentifier
     * @param startTime
     * @param endTime
     */
    public WorkflowResultItem(String serviceActionIdentifier, long startTime){
    	this(serviceActionIdentifier,startTime,-1);
    }
    
	public WorkflowResultItem(String serviceActionIdentifier, long startTime, long endTime){
		this();
		this.sActionIdentifier = serviceActionIdentifier;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public void setInputDigitalObject(DigitalObject inDigo){
		this.digoIn = inDigo.toXml();
	}
	
	public void setOutputDigitalObject(DigitalObject outDigo){
		this.digoOut = outDigo.toXml();
	}
	
	public void setExtractedInformation(List<String> information){
		this.extractedInformation = information;
	}
	
	public void addExtractedInformation(String information){
		this.extractedInformation.add(information);
	}
	
	public void setLogInfo(String logInfo){
		this.logInfo = logInfo;
	}
	
	public String getLogInfo(){
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
		return new DigitalObject.Builder(this.digoIn).build();
	}


	public DigitalObject getOutputDigitalObject() {
		return new DigitalObject.Builder(this.digoOut).build();
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

}
