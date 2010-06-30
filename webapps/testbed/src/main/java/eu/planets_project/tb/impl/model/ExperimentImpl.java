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
/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 *
 */
@Entity
@XmlRootElement(name = "Experiment", namespace = "http://www.planets-project.eu/testbed/experiment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"version", "expSetup", "executable" , "expApproval", "expExecution", "expEvaluation", "mCommunityExperimentRatings" })
public class ExperimentImpl extends ExperimentPhaseImpl
		implements Experiment, java.io.Serializable {
    
    // The version of this ExperimentImpl, used for load/store, not for the DB.
    @Transient
    public int version = 2;
    
    //the EntityID and it's setter and getters are inherited from ExperimentPhase
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentEvaluationImpl expEvaluation;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentApprovalImpl expApproval;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentExecutionImpl expExecution;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentSetupImpl expSetup;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentExecutableImpl executable;
    //get's instantiated within the experimentSetup phase
	
	//HashMap<UserName, expRating>
	private HashMap<String, Double> mCommunityExperimentRatings;
	
    @XmlTransient
    private static final long serialVersionUID = 123497123479L;
    
    @Transient
    @XmlTransient
    private static Log log = LogFactory.getLog(ExperimentImpl.class);
	
	public ExperimentImpl(){
		//the experiment's stages
		expSetup = new ExperimentSetupImpl();
		expExecution = new ExperimentExecutionImpl();
		expApproval = new ExperimentApprovalImpl();
		expEvaluation = new ExperimentEvaluationImpl();
		executable = new ExperimentExecutableImpl();
//		this.getExperimentSetup().getBasicProperties().getExperimenter();
		expSetup.setState(ExperimentSetup.STATE_IN_PROGRESS);
		mCommunityExperimentRatings = new HashMap<String,Double>();
		log.debug("ExperimentImpl initialised.");
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentAnalysis()
	 */
	
    public ExperimentEvaluation getExperimentEvaluation() {
		return this.expEvaluation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentApproval()
	 */
	public ExperimentApproval getExperimentApproval() {
		return this.expApproval;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentExecution()
	 */
	public ExperimentExecution getExperimentExecution() {
		return this.expExecution;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentSetup()
	 */
	public ExperimentSetup getExperimentSetup() {
		return this.expSetup;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentAnalysis(eu.planets_project.tb.api.model.ExperimentEvaluation)
	 */
	public void setExperimentEvaluation(ExperimentEvaluation analysisPhase) {
		this.expEvaluation = (ExperimentEvaluationImpl)analysisPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentApproval(eu.planets_project.tb.api.model.ExperimentApproval)
	 */
	public void setExperimentApproval(ExperimentApproval approvalPhase) {
		this.expApproval = (ExperimentApprovalImpl)approvalPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentExecution(eu.planets_project.tb.api.model.ExperimentExecution)
	 */
	public void setExperimentExecution(ExperimentExecution executionPhase) {
		this.expExecution = (ExperimentExecutionImpl)executionPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentSetup(eu.planets_project.tb.api.model.ExperimentSetup)
	 */
	public void setExperimentSetup(ExperimentSetup setupPhaseObject) {
		this.expSetup = (ExperimentSetupImpl)setupPhaseObject;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getCurrentPhase()
	 */
	public ExperimentPhase getCurrentPhase() {
		
		ExperimentPhase ret = null;
		if(this.expSetup.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(0))
				ret = this.expSetup;
			
		if(this.expApproval.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(1))
				ret = this.expApproval;

		if(this.expExecution.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(2))
				ret = this.expExecution;
		
		if(this.expEvaluation.getState() == ExperimentPhase.STATE_IN_PROGRESS ||
		        this.expEvaluation.getState() == ExperimentPhase.STATE_COMPLETED )
			if(checkAllOtherStagesCompleted(3))
				ret = this.expEvaluation;
		
		return ret;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getCurrentPhasePointer()
	 */
	public int getCurrentPhasePointer() {
		int ret = Experiment.PHASE_NOPHASE;
		if(this.expSetup.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(0))
				ret = Experiment.PHASE_EXPERIMENTSETUP;
			
		if(this.expApproval.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(1))
				ret = Experiment.PHASE_EXPERIMENTAPPROVAL;

		if(this.expExecution.getState() == ExperimentPhase.STATE_IN_PROGRESS)
			if(checkAllOtherStagesCompleted(2))
				ret = Experiment.PHASE_EXPERIMENTEXECUTION;
		
        if(this.expEvaluation.getState() == ExperimentPhase.STATE_IN_PROGRESS)
            if(checkAllOtherStagesCompleted(3))
                ret = Experiment.PHASE_EXPERIMENTEVALUATION;
        
        if(this.expEvaluation.getState() == ExperimentPhase.STATE_COMPLETED)
            if(checkAllOtherStagesCompleted(3))
                ret = Experiment.PHASE_EXPERIMENTFINALIZED;
        
		return ret;
	}
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.Experiment#getCurrentPhaseIndex()
     */
    public int getCurrentPhaseIndex() {
        int index = this.getCurrentPhasePointer() + 1;
        if( index >= 2 ) {
        	index += 2;
        } else {
        	// Patch in the early phases
        	if( index == 1 && this.getExperimentSetup().getSubStage() > 0 ) {
        		index = this.getExperimentSetup().getSubStage();
        	}
        }
        // Now re-map down to reflect the new wizard stages.
        if( index == 1 ) index = 1; // Start
        else if ( index == 2 ) index = 2; // Design
        else if ( index == 3 ) index = 2; // Design
        else if ( index == 4 ) index = 3; // Approving
        else if ( index == 5 ) index = 3; // Running
        else if ( index == 6 ) index = 6; // Evaluating
        else if ( index == 7 ) index = 7; // Finalise
        
        return index;
    }
    
    

    /**
	 * @param iPhaseNr may reach from 0..3, representing 
	 * 0=ExperimentSetup, 1=ExperimentApproval, 2=ExperimentExecution, 3=ExperimentEvaluation
	 * @return
	 */
	private boolean checkAllOtherStagesCompleted(int iPhaseNr){
		boolean bRet = false;
		final int iExperimentSetup 		= 0;
		final int iExperimentApproval 	= 1;
		final int iExperimentExecution 	= 2;
		final int iExperimentEvaluation = 3;
		
		switch(iPhaseNr){
			case iExperimentSetup: 
				//first stage - nothing to check
				bRet = true;
				
			case iExperimentApproval: 
				//check if the previous stage was completed 
				if(this.expSetup.getState()==ExperimentPhase.STATE_COMPLETED)
					bRet = true;
			
			case iExperimentExecution:
				//check if the previous stages were completed 
				if((this.expSetup.getState()==ExperimentPhase.STATE_COMPLETED) && (this.expApproval.getState()==ExperimentPhase.STATE_COMPLETED))
					bRet = true;
				
			case iExperimentEvaluation:
				//check if the previous stages were completed 
				if((this.expSetup.getState()==ExperimentPhase.STATE_COMPLETED) && (this.expApproval.getState()==ExperimentPhase.STATE_COMPLETED) && (this.expExecution.getState()==ExperimentPhase.STATE_COMPLETED))
					bRet = true;
		}
		
		return bRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentExecutable()
	 */
	public ExperimentExecutable getExperimentExecutable() {
//	    if( this.executable == null ) {
//            this.executable = new ExperimentExecutableImpl();
//	    }
		return this.executable;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentExecutable(eu.planets_project.tb.api.model.ExperimentExecutable)
	 */
	public void setExperimentExecutable(ExperimentExecutable executable) {
		this.executable = (ExperimentExecutableImpl) executable;
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#removeExperimentExecutable()
	 */
	public void removeExperimentExecutable() {
		this.executable = null;
		
	}

    /**
     * Utilities for querying details about the experimental approval status
     */
	
    public boolean isAwaitingApproval() {
        if( this.getCurrentPhasePointer() != ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        if( AdminManagerImpl.experimentAwaitingApproval(this) ) return true;
        return false;
    }
    public boolean isApproved() {
        if( this.getCurrentPhasePointer() <= ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        if( isAwaitingApproval() ) return false;
        if( AdminManagerImpl.experimentWasApproved(this)) return true;
        if( this.getExperimentApproval().getGo() ) return true;
        return false;
    }
    public boolean isDenied() {
        if( this.getCurrentPhasePointer() != ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        if( isAwaitingApproval() ) return false;
        if( AdminManagerImpl.experimentWasDenied(this)) return true;
        if( ! this.getExperimentApproval().getGo() ) return true;
        return false;
    }
    public String getUsernameOfApprover() {
        if( ! this.isApproved() && ! this.isDenied() ) return null;
        if( this.getExperimentApproval().getApprovalUsersIDs() == null ) return null;
        if( this.getExperimentApproval().getApprovalUsersIDs().size() == 0 ) return null;
        return this.getExperimentApproval().getApprovalUsersIDs().get(0);
    }
    
    /**
     * @return
     */
    public Set<MeasurementImpl> getAllMeasurements() {
        Set<MeasurementImpl> mi = new HashSet<MeasurementImpl>();
        /*
        for( BatchExecutionRecordImpl b : this.getExperimentExecutable().getBatchExecutionRecords() ) {
            for( ExecutionRecordImpl run : b.getRuns() ) {
                for( InvocationRecordImpl iri : run.getServiceCalls() ) {
                    for( MeasurementEventImpl me : iri.getMeasurementEvents() ) {
                        mi.addAll(me.getMeasurements());
                    }
                }
            }
        }
        */
        return mi;
    }

    /* ------------------------------------------------------------ */
    
    /**
     * 
     */
    public static void resetToApprovedStage( Experiment exp ) {
    	log.info("Resetting experiment to 'Approved': "+exp.getExperimentSetup().getBasicProperties().getExperimentName());
    	// Wipe any results
        for( BatchExecutionRecordImpl b : exp.getExperimentExecutable().getBatchExecutionRecords() ) {
            b.setExecutable(null);
        }
        exp.removeAllUserRatingsFromExperiment();
        
    	//exp.getExperimentExecutable().getBatchExecutionRecords().clear();
    	exp.getExperimentExecutable().setBatchExecutionIdentifier(null);
    	// Set the state
        exp.getExperimentExecutable().setExecutableInvoked(false);
        exp.getExperimentExecutable().setExecutionCompleted(false);
        exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
        exp.getExperimentEvaluation().setState(Experiment.STATE_NOT_STARTED);
    }

    /**
     * 
     */
    public static void resetToEditingStage(Experiment exp ) {
    	// Revert exec:
    	resetToApprovedStage(exp);
    	// And revert further:
        AdminManagerImpl.toEditFromDenied(exp);
    }

    
    // --- start: allow users to rate the experiment --
    
	/** {@inheritDoc} */
	public Map<String, Double> getAllUserRatingsOfExperiment() {
		if(mCommunityExperimentRatings==null){
			return new HashMap<String,Double>();
		}else{
			return mCommunityExperimentRatings;
		}
	}

	/** {@inheritDoc} */
	public Double getUserRatingOfExperiment(String userID) {
		if(getAllUserRatingsOfExperiment().containsKey(userID)){
			return getAllUserRatingsOfExperiment().get(userID);
		}else{
			return new Double(0);
		}
	}

	/** {@inheritDoc} */
	public void setUserRatingForExperiment(String userID, Double rating) {
		if((mCommunityExperimentRatings!=null) &&(userID!=null) &&(rating!=null)){
			this.getAllUserRatingsOfExperiment().put(userID, rating);
		}
	}
	
	public int getAverageUserExperimentRatings(){
        int numberOfRatings = getNumberOfUserExperimentRatings();
        double ratingValues=0;
        Collection<Double> cRatingValues = this.getAllUserRatingsOfExperiment().values();
        for(Double ratingVal :cRatingValues){
        	ratingValues+=ratingVal;
        }
        if(numberOfRatings>0 && ratingValues>0){
        	return (int)Math.round(ratingValues/numberOfRatings);
        }else{
        	return 0;
        }
	}
	
	public int getNumberOfUserExperimentRatings(){
        return this.getAllUserRatingsOfExperiment().size();
	}
	// --- end: allow users to rate the experiment --

	public void removeAllUserRatingsFromExperiment() {
		mCommunityExperimentRatings = new HashMap<String, Double>();
		
	}
    
}
