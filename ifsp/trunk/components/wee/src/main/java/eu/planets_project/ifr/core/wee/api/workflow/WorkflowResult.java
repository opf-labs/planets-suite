/**
 * 
 */
package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * TODO The return object of a workflow execution still needs to be defined. It
 * should contain a log of operations that took place registry pointers to
 * produced data of the individual steps etc.
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 15.12.2008
 */
/**
 * First draft of an actual WorkflowResult implementation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResult implements Serializable {

	@Transient
    @XmlTransient
    private static final long serialVersionUID = -7804803563573452403L;
    @Transient
    @XmlTransient
    private static ReportingLog logger;

    //@SuppressWarnings("unused")
    // For JAXB - even if empty
    public WorkflowResult() {
    	this(null);
    }
    
    public WorkflowResult(ReportingLog wfLogger){
    	if(wfLogger!=null){
    		logger = wfLogger;
    	}else{
    		logger = new ReportingLog(Logger.getLogger(WorkflowResult.class));
    	}
    	this.startTime = System.currentTimeMillis();
    	resultItems = new LinkedList<WorkflowResultItem>();
    }

    private List<URL> results;
    private URL log;
    private URL report;
    private List<WorkflowResultItem> resultItems;
    private long startTime=-1;
    private long endTime=-1;
    //private String partialResults;

    /**
     * @param report The location of the report
     * @param log The location of the log
     * @param results The location of the results
     */
    public WorkflowResult(URL report, URL log, List<URL> results) {
    	this();
        this.report = report;
        this.log = log;
        this.results = results;
    }
    
    /**
	 * Use a custom logger to report this - otherwise use default log.
	 * @param logger
	 */
	public void setReportingLog(ReportingLog logger){
		this.logger = logger;
	}

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, report: %s, log: %s, results: %s, result-items: %s, start-time: %s, end-time: %s", this
                .getClass().getSimpleName(), report, log, results, resultItems, startTime, endTime);
    }

    /**
     * A list of URL pointers to all objects that have been created within this workflow
     * @return the objects
     */
    @Deprecated
    public List<URL> getResults() {
        return results;
    }
    
    /**
     * @return the objects
     */
    @Deprecated
    public void setResults(List<URL> results) {
        this.results = results;
    }

    /**
     * @return the log
     */
    public URL getLog() {
        return log;
    }
    
    /**
     * @return the log
     */
    public void setLog(URL log) {
    	logger.info("WorkflowLogURL: "+report);
        this.log = log;
    }

    /**
     * @return the report
     */
    public URL getReport() {
        return report;
    }
    
    public void setReport(URL report) {
    	logger.info("WorkflowReport: "+report);
        this.report = report;
    }
    
    /**
     * Start Time of the entire workflow - server specific timestamp
     * @param millis
     */
    public void setStartTime(long millis){
    	logger.info("WorkflowStartTime: "+millis);
    	this.startTime = millis;
    }
    
    
    /**
     * End Time of the entire workflow - server specific timestamp
     * @param millis
     */
    public void setEndTime(long millis){
    	logger.info("WorkflowEndTime: "+millis);
    	this.endTime = millis;
    }
    
    /**
     * Start Time of the entire workflow - server specific timestamp
     * @param millis
     */
    public long getStartTime(){
    	return startTime;
    }
    
    /**
     * End Time of the entire workflow - server specific timestamp
     * @param millis
     */
    public long getEndTime(){
    	return endTime;
    }
    
    /**
     * Duration if the start time and end time of the workflow have been set
     * @return
     */
    public long getDuration(){
    	if((this.getStartTime()!=-1)&&(this.getEndTime()!=-1)){
    		return this.getEndTime() - this.getStartTime();
    	}
    	return -1;
    }
    
    /**
     * returns a list of WorkflowResultItems in the order they took place 
     * @return
     */
    public List<WorkflowResultItem> getWorkflowResultItems(){
    	return resultItems;
    }
    
    public void addWorkflowResultItem(WorkflowResultItem item){
    	this.getWorkflowResultItems().add(item);
    }
    
	/**
	 * Document a 'migration' specific workflow result item
	 * @param inputDigo - the input data
	 * @param outputDigo - possibly an output file
	 * @param startTime - before the 'migration' service was called
	 * @param endTime - after the 'migration' service returned
	 * @param logInfo - any additional logInformation for this step
	 */
	public void addMigrationWorkflowResultItem(DigitalObject inputDigo, DigitalObject outputDigo, long startTime, long endTime, String logInfo){
		WorkflowResultItem item = new WorkflowResultItem(WorkflowResultItem.SERVICE_ACTION_MIGRATION,startTime,endTime,logger);
		item.setInputDigitalObject(inputDigo);
		if(outputDigo!=null){
			item.setOutputDigitalObject(outputDigo);
		}
		item.addLogInfo(logInfo);
	}
	
	/**
	 * Document a 'identification' specific workflow result item
	 * @param inputDigo - the input data
	 * @param identifier - possibly the extracted identifier(s)
	 * @param startTime - before the 'identification' service was called
	 * @param endTime - after the 'identification' service returned
	 * @param logInfo - any additional logInformation for this step
	 */
	public void addIdentificationWorkflowResultItem(DigitalObject inputDigo, List<String> identifier, long startTime, long endTime, String logInfo){
		WorkflowResultItem item = new WorkflowResultItem(WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,startTime,endTime,logger);
		item.setInputDigitalObject(inputDigo);
		if((identifier!=null)&&(identifier.size()>0)){
			item.setExtractedInformation(identifier);
		}
		item.addLogInfo(logInfo);
	}
	
	/**
	 * Document a 'characterisation' specific workflow result item
	 * @param inputDigo - the input data
	 * @param characterisation - extracted characteristics
	 * @param startTime - before the 'characterisation' service was called
	 * @param endTime - after the 'characterisation' service returned
	 * @param logInfo - any additional logInformation for this step
	 */
	public void addCharacterisationWorkflowResultItem(DigitalObject inputDigo, List<String> characterisation, long startTime, long endTime, String logInfo){
		WorkflowResultItem item = new WorkflowResultItem(WorkflowResultItem.SERVICE_ACTION_CHARACTERISATION,startTime,endTime,logger);
		item.setInputDigitalObject(inputDigo);
		if((characterisation!=null)&&(characterisation.size()>0)){
			item.setExtractedInformation(characterisation);
		}
		item.addLogInfo(logInfo);
	}
	
	//TODO continue for other Planets supported service types as create view, etc...

	/**
	 * Indicates if partial results or all results are contained within this WF report
	 * @return
	 */
	public boolean isPartialResults() {
		if(this.getProgress()<100){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Indicates the percentage of digital objects out of the overall job
	 * have been processed. This does not give any information about the success / failure
	 * @return 0-100
	 */
	public int getProgress(){
		//For now we don't support incremental WorkflowResult updates - set it to 100%
		return 100;
	}
	
	public void setProgress(int i){
		//TODO
	}
}
