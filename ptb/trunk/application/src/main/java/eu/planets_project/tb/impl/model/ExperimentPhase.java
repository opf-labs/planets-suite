/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.data.DataRegistryBinding;

/**
 * @author alindley
 *
 */
//@Entity
public abstract class ExperimentPhase implements
		eu.planets_project.tb.api.model.ExperimentPhase {
	
	
	public static final int NOT_STARTED = -1;
	public static final int IN_PROGRESS = 0;
	public static final int COMPLETED = 1;
	
	public static final int NOT_AVAILABLE = -1;
	public static final int SUCCESS = 0;
	public static final int ACCEPTED = 1;
	public static final int FAILURE = 2;
	public static final int REJECTED = 3;
	
	@Id
	@GeneratedValue
	private long sPhaseID;
	private Calendar endDate, startDate;
	private int iState, iResult;
	boolean bSuccess;
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getDuration()
	 */
	public long getDurationInMillis() {
		return calculateDuration();
	}
	
	private long calculateDuration(){
		return endDate.getTimeInMillis()- startDate.getTimeInMillis();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getPhaseID()
	 */
	public long getPhaseID() {
		return this.sPhaseID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getState()
	 */
	public int getState() {
		return this.iState;
	}
	
	/**
	 * checks if setState lies in [-1..not started; 0..in progress; 1..completed]
	 * @param progress
	 * @return
	 */
	private boolean checkStateInput(int state){
		boolean bret= false;
		if(state<=1&&state>=-1){
			bret = true;
		}
		return bret;
	}
	
	/**
	 * -1..not available; 0..success; 1..accepted; 2..failure; 3..rejected;
	 * @param state
	 * @return
	 */
	private boolean checkResultInput(int result){
		boolean bret= false;
		if(result<=3&&result>=-1){
			bret = true;
		}
		return bret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getResult()
	 */
	public int getResult() {
		return this.iResult;
	}

	/* (non-Javadoc)
	 * This method is not implemented yet.
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getSummary()
	 */
	public String getSummary() {
		// TODO This method is not implemented yet.
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isCompleted()
	 */
	public boolean isCompleted() {
		int iState = this.getState();
		if (iState == ExperimentPhase.COMPLETED)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isInProgress()
	 */
	public boolean isInProgress() {
		int iState = this.getState();
		if (iState == ExperimentPhase.IN_PROGRESS)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isNotStarted()
	 */
	public boolean isNotStarted() {
		int iState = this.getState();
		if (iState == ExperimentPhase.NOT_STARTED)
			return true;
		return false;
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setState(int)
	 */
	public void setState(int state) {
		boolean bOK = this.checkStateInput(state);
		if(bOK)
			this.iState = state;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setResult(int)
	 */
	public void setResult(int result) {
		boolean bOk = this.checkResultInput(result);
		if (bOk)
			this.iResult = result;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDate()
	 */
	public Calendar getEndDate() {
		return this.endDate;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDateInMillis()
	 */
	public long getEndDateInMillis() {
		return this.endDate.getTimeInMillis();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDate()
	 */
	public Calendar getStartDate() {
		return this.startDate;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDateInMillis()
	 */
	public long getStartDateInMillis() {
		return this.startDate.getTimeInMillis();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setEndDate(java.util.Calendar)
	 */
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setStartDate(java.util.Calendar)
	 */
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

}
