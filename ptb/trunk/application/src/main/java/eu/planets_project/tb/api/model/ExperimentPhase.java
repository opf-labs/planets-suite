package eu.planets_project.tb.api.model;

import java.sql.Date;
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
public abstract interface ExperimentPhase {
	
	public String getPhaseID();
	
	public void setStartDate(Date millis);
	public Date getStartDate();
	public GregorianCalendar getStartDateReadable();
	
	public void setEndDate(Date millis);
	public Date getEndDate();
	public GregorianCalendar getEndDateReadable();
	
	/**
	 * Returns the Duration of the Phase between start and completion in millis
	 * @return millis
	 */
	public long getDuration();
	
	/**
	 * Possible states:
	 * -1..not started; 0..in progress; 1..completed
	 * @param iState
	 */
	public void setProgress(int iProgress);
	/**
	 * Possible states the phase's progress can be in:
	 * -1..not started; 0..in progress; 1..completed
	 * return iState
	 */
	public int getProgress();
	
	public boolean isCompleted();
	public boolean isInProgress();
	public boolean isStarted();
	
	/**
	 * The outcome of a phase
	 * -1..not available; 0..sucess/accepted; 1..failure/rejected
	 * @param iState
	 */
	public void setState(int iState);
	/**
	 * The outcome of a phase
	 * -1..not available; 0..sucess/accepted; 1..failure/rejected
	 * @return iState
	 */
	public int getState();
	
	
	/**
	 * 
	 * @param success
	 */
	public void setSuccess(int success);
	
	//Method parameters still unclear!!
	public void persist(DataRegistryBinding registry);
	
	/**
	 * @return html, xml, etc. summary?
	 */
	public String getSummary();

}
