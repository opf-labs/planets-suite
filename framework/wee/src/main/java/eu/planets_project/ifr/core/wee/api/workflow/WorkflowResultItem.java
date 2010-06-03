package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;

import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResultItem implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -7296972920451369134L;

	@Transient
    @XmlTransient
    private static Log logger = LogFactory.getLog(WorkflowResultItem.class);
	
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
    //xml serialization of the digital object - deprecated
    private String digoIn;
    private URI digoInRef;
    //xml serialization of the digital object - deprecated
    private String digoOut;
    private URI digoOutRef;
    private List<String> extractedInformation;
    private List<String> logInfo;
    private ServiceReport serReport;
    private String serviceEndpoint;
    private ServiceDescription serviceDescription;
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
    	this(null,serviceActionIdentifier,startTime,-1,null);
    }
    /**
     * Allows to pass a specific logger.
     * @see WorkflowResultItem#WorkflowResultItem(String, long)
     */
    public WorkflowResultItem(String serviceActionIdentifier, long startTime, ReportingLog logger){
    	this(null,serviceActionIdentifier,startTime,-1,logger);
    }
    
    public WorkflowResultItem(String serviceActionIdentifier, long startTime, long endTime){
    	this(null,serviceActionIdentifier,startTime,endTime,null);
    }
    /**
     * Allows to pass a specific logger.
     * @see WorkflowResultItem#WorkflowResultItem(String, long, long)
     */
    public WorkflowResultItem(String serviceActionIdentifier, long startTime, long endTime,ReportingLog logger){
    	this(null,serviceActionIdentifier,startTime,endTime,logger);
    }
    
    /**
     * The most common constructor
     * since 15.03.2010
     * Updated method signature to deal with shared data references (data registry). 
     * @param aboutDigoRef The original reference for which the execute was called
     * @param serviceActionIdentifier
     * @param startTime
     * @param endTime
     */
    public WorkflowResultItem(URI aboutDigoRef, String serviceActionIdentifier, long startTime){
    	this(aboutDigoRef,serviceActionIdentifier,startTime,-1,null);
    }
 
    /**
     * Allows to pass a specific logger.
     * since 15.03.2010
     * Updated method signature to deal with shared data references (data registry). 
     * @see WorkflowResultItem#WorkflowResultItem(URI, String, long)
     */
    public WorkflowResultItem(URI aboutDigoRef, String serviceActionIdentifier, long startTime,ReportingLog logger){
    	this(aboutDigoRef,serviceActionIdentifier,startTime,-1,logger);
    }
    
    /**
     * Allows to pass a specific logger.
     * since 15.03.2010
     * Updated method signature to deal with shared data references (data registry). 
     * @see WorkflowResultItem#WorkflowResultItem(URI, String, long)
     */
    public WorkflowResultItem(URI aboutDigoRef,String serviceActionIdentifier, long startTime, long endTime){
		this(aboutDigoRef,serviceActionIdentifier,startTime,endTime,null);
	}
    

    public WorkflowResultItem(URI aboutDigoRef,String serviceActionIdentifier, long startTime, long endTime,ReportingLog logger){
		this();
		if(logger!=null){
			WorkflowResultItem.logger = logger;
		}
		else{
	    	logger = new ReportingLog(Logger.getLogger(WorkflowResultItem.class));
		}
		if(aboutDigoRef==null){
			aboutExecutionDigoDifferentThanInputDigo = false;
		}
		else{
			aboutExecutionDigoDifferentThanInputDigo = true;
			this.setAboutExecutionDigoRef(aboutDigoRef);
		}
		this.setSActionIdentifier(serviceActionIdentifier);
		this.setStartTime(startTime);
		this.setEndTime(endTime);
	}
	
	/**
	 * Use a custom logger to report this item.
	 * @param logger
	 */
	public void setReportingLog(ReportingLog logger){
		WorkflowResultItem.logger = logger;
	}
	
	@Deprecated
	public void setInputDigitalObject(DigitalObject inDigo){
		if(inDigo!=null){
			this.digoIn = inDigo.toXml();
			if(!aboutExecutionDigoDifferentThanInputDigo){
				this.setAboutExecutionDigoRef(inDigo.getPermanentUri());
			}
			if(digoIn.length()>3000){
				logger.info("setInputDigitalObject: "+inDigo.toString()+" \n details: \n "+digoIn.subSequence(0, 1500)+"\n [...] \n"+digoIn.subSequence(digoIn.length()-1500, digoIn.length()));
			}
			else{
				logger.info("setInputDigitalObject: "+inDigo.toString()+" \n details: \n "+digoIn);
			}
		}
	}
	
	public void setInputDigitalObjectRef(URI inDigoRef){
		if(inDigoRef!=null){
			if(!aboutExecutionDigoDifferentThanInputDigo){
				this.setAboutExecutionDigoRef(inDigoRef);
			}
			logger.info("setInputDigitalObjectRef: "+inDigoRef.toString());
			this.digoInRef = inDigoRef;
		}
	}
	
	@Deprecated
	public void setOutputDigitalObject(DigitalObject outDigo){
		if(outDigo!=null){
			this.digoOut = outDigo.toXml();
			this.addLogInfo("Successfully added OutputDigitalObject");
			if(digoOut.length()>3000){
				logger.info("setOutputDigitalObject: "+outDigo.toString()+" \n details: \n "+digoOut.subSequence(0, 1500)+"\n [...] \n"+digoOut.subSequence(digoOut.length()-1500, digoOut.length()));
			}
			else{
				logger.info("setOutputDigitalObject: "+outDigo.toString()+" \n details: \n "+digoOut);
			}
		}
	}
	
	public void setOutputDigitalObjectRef(URI outDigoRef){
		if(outDigoRef!=null){
			this.addLogInfo("Successfully added OutputDigitalObjectRef");
			logger.info("setOutputDigitalObjectRef: "+outDigoRef);
			this.digoOutRef = outDigoRef;
		}
	}
	
	public void setExtractedInformation(List<String> information){
		this.extractedInformation = information;
		logger.info("setExtractedInformation: "+information.toString());
	}
	
	public void addExtractedInformation(String information){
		this.extractedInformation.add(information);
		logger.info("addExtractedInformation: "+information);
	}
	
	public void addLogInfo(String logInfo){
		this.logInfo.add(logInfo);
		logger.info("addLogInfo: "+logInfo);
	}
	
	public List<String> getLogInfo(){
		return this.logInfo;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
		logger.info("setStartTime: "+startTime);
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
		logger.info("setEndTime: "+endTime);
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
		logger.info("setSActionIdentifier: "+actionIdentifier);
	}

	@Deprecated
	public DigitalObject getInputDigitalObject() {
		if(this.digoIn!=null){
			return new DigitalObject.Builder(this.digoIn).build();
		}
		else if(this.getInputDigitalObjectRef()!=null){
			//let's check maybe we can find the digital object in the data registry somewhere
			try {
				return DataRegistryFactory.getDataRegistry().retrieve(this.getInputDigitalObjectRef());
			} catch (DigitalObjectNotFoundException e) {
			}
		}
		return null;
	}
	
	public URI getInputDigitalObjectRef() {
		if(this.digoInRef!=null){
			return digoInRef;
		}
		return null;
	}

	@Deprecated
	public DigitalObject getOutputDigitalObject() {
		if(this.digoOut!=null){
			return new DigitalObject.Builder(this.digoOut).build();
		}
		else if(this.getOutputDigitalObjectRef()!=null){
			try {
				//let's check maybe we can find the digital object in the data registry somewhere
				return DataRegistryFactory.getDataRegistry().retrieve(this.getOutputDigitalObjectRef());
			} catch (DigitalObjectNotFoundException e) {
			}
		}
		return null;
	}
	
	public URI getOutputDigitalObjectRef() {
		if(this.digoOutRef!=null){
			return digoOutRef;
		}
		return null;
	}


	public List<String> getExtractedInformation() {
		return extractedInformation;
	}


	public ServiceReport getServiceReport() {
		return serReport;
	}

	public void setServiceReport(ServiceReport serviceReport) {
		this.serReport = serviceReport;
		logger.info("setServiceReport: "+serviceReport.toString());
	}

	public List<Parameter> getServiceParameters() {
		return serParams;
	}

	public void setServiceParameters(List<Parameter> serviceParams) {
		this.serParams = serviceParams;
		logger.info("setServiceParameters: "+serviceParams.toString());
	}

	public URI getAboutExecutionDigoRef() {
		return aboutExecutionDigoRef;
	}

	public void setAboutExecutionDigoRef(URI aboutExecutionDigoRef) {
		this.aboutExecutionDigoRef = aboutExecutionDigoRef;
		logger.info("setAboutExecutionDigoRef: "+aboutExecutionDigoRef);
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(URL serviceEndpoint) {
		if(serviceEndpoint!=null){
			this.serviceEndpoint = serviceEndpoint.toExternalForm();
			logger.info("setServiceEndpoint: "+serviceEndpoint);
		}
	}
	
	public ServiceDescription getServiceDescription(){
		return serviceDescription;
	}
	
	public void setServiceDescription(ServiceDescription serDescr){
		if(serDescr!=null){
			logger.info("setServiceDescription "+serDescr.toString());
			this.serviceDescription = serDescr;
		}
	}
	
	/**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, actionIdentifier: %s,logInfo: %s, serviceReport: %s,  start-time: %s, end-time: %s, aboutExecutionDigitalObjectRef: %s, endpoint: %s, parameters: %s", this
                .getClass().getSimpleName(), sActionIdentifier,logInfo, serReport, startTime, endTime, this.aboutExecutionDigoRef, this.getServiceEndpoint(), this.getServiceParameters());
    }

}
