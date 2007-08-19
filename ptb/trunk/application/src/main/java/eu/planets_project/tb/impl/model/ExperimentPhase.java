/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.sql.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.Id;

import eu.planets_project.tb.api.data.DataRegistryBinding;

/**
 * @author alindley
 *
 */
//@Entity
public class ExperimentPhase implements
		eu.planets_project.tb.api.model.ExperimentPhase {
	
	//@Id
	private String sPhaseID;
	private long lDuration;
	private GregorianCalendar endDate, startDate;
	private int iProgress, iState;
	boolean bSuccess;

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getDuration()
	 */
	public long getDurationInMillis() {
		calculateDuration();
		return this.lDuration;
	}
	
	private void calculateDuration(){
		this.lDuration = endDate.getTimeInMillis()- startDate.getTimeInMillis();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getPhaseID()
	 */
	public String getPhaseID() {
		return this.sPhaseID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getProgress()
	 */
	public int getProgress() {
		return this.iProgress;
	}
	
	/**
	 * checks if setProgress lies in [-1..not started; 0..in progress; 1..completed]
	 * @param progress
	 * @return
	 */
	private boolean checkProgressInput(int progress){
		boolean bret= false;
		if(progress<=1&&progress>=-1){
			bret = true;
		}
		return bret;
	}
	
	/**
	 * -1..not available; 0..sucess/accepted; 1..failure/rejected
	 * @param state
	 * @return
	 */
	private boolean checkStateInput(int state){
		boolean bret= false;
		if(state<=1&&state>=-1){
			bret = true;
		}
		return bret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getState()
	 */
	public int getState() {
		return this.iState;
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
		int iProgress = this.getProgress();
		if (iProgress == 1)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isInProgress()
	 */
	public boolean isInProgress() {
		int iProgress = this.getProgress();
		if (iProgress == 0)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isStarted()
	 */
	public boolean isNotStarted() {
		int iProgress = this.getProgress();
		if (iProgress == -1)
			return true;
		return false;
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setProgress(int)
	 */
	public void setProgress(int progress) {
		boolean bOK = this.checkProgressInput(progress);
		if(bOK)
			this.iProgress = progress;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setState(int)
	 */
	public void setState(int state) {
		boolean bOk = this.checkStateInput(state);
		if (bOk)
			this.iState = state;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDate()
	 */
	public GregorianCalendar getEndDate() {
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
	public GregorianCalendar getStartDate() {
		return this.startDate;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDateInMillis()
	 */
	public long getStartDateInMillis() {
		return this.startDate.getTimeInMillis();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setEndDate(java.util.GregorianCalendar)
	 */
	public void setEndDate(GregorianCalendar endDate) {
		this.endDate = endDate;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setStartDate(java.util.GregorianCalendar)
	 */
	public void setStartDate(GregorianCalendar startDate) {
		this.startDate = startDate;
	}

}
