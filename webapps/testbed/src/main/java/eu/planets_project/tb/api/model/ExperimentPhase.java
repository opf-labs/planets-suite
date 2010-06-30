/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.api.model;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author alindley
 *
 */
public interface ExperimentPhase {
	
	public static final int STATE_NOT_STARTED = -1;
	public static final int STATE_IN_PROGRESS = 0;
    public static final int STATE_COMPLETED   = 1;
	
	public static final int RESULT_NOT_AVAILABLE = -1;
	public static final int RESULT_SUCCESS 		 = 0;
	public static final int RESULT_ACCEPTED		 = 1;
	public static final int RESULT_FAILURE		 = 2;
	public static final int RESULT_REJECTED		 = 3;
	
	public static final int PHASE_NOPHASE		       = -1;
	public static final int PHASE_EXPERIMENTSETUP 	   = 0;
	public static final int PHASE_EXPERIMENTAPPROVAL   = 1;
	public static final int PHASE_EXPERIMENTEXECUTION  = 2;
    public static final int PHASE_EXPERIMENTEVALUATION = 3;
    public static final int PHASE_EXPERIMENTFINALIZED  = 4;
	
	public final String PHASENAME_NOPHASE			  = "No Phase";
	public final String PHASENAME_EXPERIMENTSETUP	  = "Experiment Setup";
	public final String PHASENAME_EXPERIMENTAPPROVAL  = "Experiment Approval";
	public final String PHASENAME_EXPERIMENTEXECUTION = "Experiment Execution";
    public final String PHASENAME_EXPERIMENTEVALUATION= "Experiment Evaluation";
    public final String PHASENAME_EXPERIMENTFINALIZED = "Experiment Finalized";

    public static final Log log = LogFactory.getLog(ExperimentPhase.class);


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
	 * @return int stage: as defined within the finals starting with "STAGE_"
	 */
	public int getPhasePointer();
	/**
	 * @param stage: as defined within the finals starting with "STAGE_"
	 */
	public String getPhaseName();
	
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
