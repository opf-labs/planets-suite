/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.mockups.DataRegistryBinding;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;

/**
 * @author alindley
 * 
 * Annotation:
 * MappedSuperClass: A class that provides mapping information but is not an entity 
 * and thus not queryable nor persistent
 *
 */

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ExperimentPhaseImpl implements
		eu.planets_project.tb.api.model.ExperimentPhase, java.io.Serializable {
	
	@Id
	@GeneratedValue
    @XmlTransient
	private long lEntityID = -1;
	private Calendar endDate, startDate;
	private int iState, iResult;
	boolean bSuccess;
	int iStagePointer;
	
	
	public ExperimentPhaseImpl(){
		this.lEntityID = -1;
		this.startDate = new GregorianCalendar();
		this.iState = ExperimentPhase.STATE_NOT_STARTED;
		this.iResult = ExperimentPhase.RESULT_NOT_AVAILABLE;
		this.iStagePointer = -1;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStageMarker()
	 */
	public int getPhasePointer(){
		return this.iStagePointer;
	}
	
	public String getPhaseName(){
		if(this.iStagePointer == PHASE_EXPERIMENTSETUP)
			return PHASENAME_EXPERIMENTSETUP;
		if(this.iStagePointer == PHASE_EXPERIMENTAPPROVAL)
			return PHASENAME_EXPERIMENTAPPROVAL;
		if(this.iStagePointer == PHASE_EXPERIMENTEXECUTION)
			return PHASENAME_EXPERIMENTEXECUTION;
        if(this.iStagePointer == PHASE_EXPERIMENTEVALUATION) {
            if( this.getState() == ExperimentPhase.STATE_COMPLETED ) {
                return PHASENAME_EXPERIMENTFINALIZED;
            } else {
                return PHASENAME_EXPERIMENTEVALUATION;                
            }
        }
        if(this.iStagePointer == PHASE_EXPERIMENTFINALIZED)
            return PHASENAME_EXPERIMENTFINALIZED;

		//else (should never be reached)
		return PHASENAME_NOPHASE;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setStageMarker(int)
	 */
	protected void setPhasePointer(int stage){
	    
		if(stage>=ExperimentPhase.PHASE_NOPHASE&&stage<=ExperimentPhase.PHASE_EXPERIMENTFINALIZED)
			this.iStagePointer = stage;
		// If Evaluation is Complete, set as Finalized:
		if( stage == ExperimentPhase.PHASE_EXPERIMENTEVALUATION && this.isCompleted() )
		    this.iStagePointer = ExperimentPhase.PHASE_EXPERIMENTFINALIZED;
	}
	
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
	public long getEntityID(){
		return this.lEntityID;
	}
	
	public void setEntityID(long lEntityID){
		this.lEntityID = lEntityID; 
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getState()
	 */
	public int getState() {
		return this.iState;
	}
	
	/**
	 * checks if setState lies in [-1..not started; 0..in progress; 1..completed]
	 * @param state
	 * @return
	 */
	private boolean checkStateInput(int state){
		boolean bret= false;
		if(state<=STATE_COMPLETED&&state>=STATE_NOT_STARTED){
			bret = true;
		}
		return bret;
	}
	
	/**
	 * -1..not available; 0..success; 1..accepted; 2..failure; 3..rejected;
	 * @param result
	 * @return
	 */
	private boolean checkResultInput(int result){
		boolean bret= false;
		if(result<=RESULT_REJECTED&&result>=RESULT_NOT_AVAILABLE){
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
		if (this.getState() == ExperimentPhaseImpl.STATE_COMPLETED)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isInProgress()
	 */
	public boolean isInProgress() {
		if (this.getState() == ExperimentPhaseImpl.STATE_IN_PROGRESS)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isNotStarted()
	 */
	public boolean isNotStarted() {
		if (this.getState() == ExperimentPhaseImpl.STATE_NOT_STARTED)
			return true;
		return false;
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setState(int)
	 */
	public void setState(int state) {
		boolean bOK = this.checkStateInput(state);
		if(bOK){
			this.iState = state;
			
			if(this.iState == STATE_IN_PROGRESS)
				this.setStartDate(new GregorianCalendar());
			
			if(this.iState == STATE_COMPLETED)
				this.setEndDate(new GregorianCalendar());
		}
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
