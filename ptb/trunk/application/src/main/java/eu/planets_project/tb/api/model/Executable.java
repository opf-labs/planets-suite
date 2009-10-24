package eu.planets_project.tb.api.model;

import java.util.Calendar;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * @author Andrew Lindley, ARC
 * This interface contains all general bits of information (metadata, etc.) that are
 * required for experiment and automated evaluation service execution.
 */
public interface Executable {
	
	/**
	 * An indicator if the service invocation has been triggered
	 */
	public boolean isExecutableInvoked();
	public void setExecutableInvoked(boolean b);
	
	/**
	 * An indicator if the service invocation has terminated
	 * @return
	 */
	public boolean isExecutionCompleted();
	public void setExecutionCompleted(boolean b);
	
	/**
	 * An indicator if the service invocation was completed successfully AND
	 * if output could be parsed
	 */
	public boolean isExecutionSuccess();
	public void setExecutionSuccess(boolean b);
	
	/**
	 * Indicates if execution was invoked and has already terminated
	 * @return
	 */
	public boolean isExecutionRunning();
	
	/**
	 * To retrieve the plain text of the XML request  message that's been used for
	 * invoking the web service
	 * @return
	 */
	public String getServiceXMLRequest();
	public void setServiceXMLRequest(String xmlrequest);
	/**
	 * To retrieve the plain text of the XML responds message that's been received from
	 * invoking the web service
	 * @return
	 */
	public String getServiceXMLResponds();
	public void setServiceXMLResponds(String xmlresponds);
	
	/**
	 * Records the execution's start date
	 * @param date
	 */
	public void setExecutionStartDate(long timeInMillis);
	public Calendar getExecutionStartDate();
	/**
	 * Records the execution's end date
	 * @param date
	 */
	public void setExecutionEndDate(long timeInMillis);
	public Calendar getExecutionEndDate();
	
	/**
	 * A TBServiceTemplate is produced by the Testbed administrator role and
	 * mainly contains 
	 * @return
	 */
	public TestbedServiceTemplate getServiceTemplate();
	public void setServiceTemplate(TestbedServiceTemplate template);
	

    /**
     * Identifies the WorkflowExecution System we're using
     * @param batchQueueIdentifier
     */
    public void setBatchSystemIdentifier(String batchQueueIdentifier);
    /**
     * Identifies the WorkflowExecution System we're using
     * @return
     */
    public String getBatchSystemIdentifier();
    
    /**
     * Identifier (ticket) for a submitted batch job.
     * @param batchExecutionIdentifier
     */
    public void setBatchExecutionIdentifier(String batchExecutionIdentifier);
    /**
     * Identifier (ticket) for a submitted batch job.
     * @return
     */
    public String getBatchExecutionIdentifier();

}
