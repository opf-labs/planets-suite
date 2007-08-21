package eu.planets_project.tb.api.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.planets_project.tb.api.data.DataRegistryBinding;

/**
 * @author alindley
 *
 */
/**
 * @author alindley
 *
 */
public interface ExperimentPhase {
	
	public long getPhaseID();
	
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
	 * Possible states:
	 * -1..not started; 0..in progress; 1..completed
	 * @param iState
	 */
	public void setState(int iState);
	/**
	 * Possible states the phase's progress can be in:
	 * -1..not started; 0..in progress; 1..completed
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
	
	//Method parameters still unclear!!
	//public void persist(DataRegistryBinding registry);
	
	/**
	 * @return html, xml, etc. summary?
	 */
	public String getSummary();

}
