package eu.planets_project.tb.api.model;

import java.util.Calendar;

/**
 * @author alindley
 *
 */
/**
 * @author alindley
 *
 */
public interface ExperimentPhase {
	
	public static final int STATE_NOT_STARTED = -1;
	public static final int STATE_IN_PROGRESS = 0;
	public static final int STATE_COMPLETED	  = 1;
	
	public static final int RESULT_NOT_AVAILABLE = -1;
	public static final int RESULT_SUCCESS 		 = 0;
	public static final int RESULT_ACCEPTED		 = 1;
	public static final int RESULT_FAILURE		 = 2;
	public static final int RESULT_REJECTED		 = 3;
	
	/**
	 * Using the EJB persistence mechanism to store an Entity, the EntityID is injected by
	 * the container. If an Entity (e.g. Experiment, ExperimentSetup, etc.) is not managed
	 * by the container it returns ID -1;
	 * @return -1 of not managed by the container;
	 */
	public long getEntityID();
	
	public void setStartDate(Calendar startDate);
	public long getStartDateInMillis();
	public Calendar getStartDate();
	
	public void setEndDate(Calendar endDate);
	public long getEndDateInMillis();
	public Calendar getEndDate();
	
	/**
	 * Returns the Duration of the Phase between start and completion in millis
	 * @return millis
	 */
	public long getDurationInMillis();
	
	/**
	 * Possible states: see final int of this class
	 * @param iState
	 */
	public void setState(int iState);
	/**
	 * Possible states the phase's progress can be in: see final int of this class
	 * return iState
	 */
	public int getState();
	
	public boolean isCompleted();
	public boolean isInProgress();
	public boolean isNotStarted();
	
	/**
	 * The outcome of a phase
	 * -1..not available; 0..sucess/accepted; 1..failure/rejected
	 * @param iState
	 */
	public void setResult(int iResult);
	/**
	 * The outcome of a phase
	 * -1..not available; 0..success/accepted; 1..failure/rejected
	 * @return iState
	 */
	public int getResult();
	
	
	/**
	 * 
	 * @param success
	 */
	//public void setSuccess(int success);
	
	/**
	 * @return html, xml, etc. summary?
	 */
	public String getSummary();

}
