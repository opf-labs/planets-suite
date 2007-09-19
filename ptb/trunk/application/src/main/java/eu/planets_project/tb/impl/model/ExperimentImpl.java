/**
 * 
 */
package eu.planets_project.tb.impl.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentPhaseImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;


/**
 * @author alindley
 *
 */
@Entity
public class ExperimentImpl extends ExperimentPhaseImpl
		implements Experiment, java.io.Serializable{
	
	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentEvaluationImpl expEvaluation;
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentApprovalImpl expApproval;
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentExecutionImpl expExecution;
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentSetupImpl expSetup;
	
	public ExperimentImpl(){
		expSetup = new ExperimentSetupImpl();
		expExecution = new ExperimentExecutionImpl();
		expApproval = new ExperimentApprovalImpl(expSetup);
		expEvaluation = new ExperimentEvaluationImpl();
		
		expSetup.setState(ExperimentSetup.STATE_IN_PROGRESS);
		
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
		
		if(this.expEvaluation.getState() == ExperimentPhase.STATE_IN_PROGRESS)
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
		
		return ret;
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
				if(this.expSetup.getState()==ExperimentPhase.STATE_COMPLETED && this.expApproval.getState()==ExperimentPhase.STATE_COMPLETED)
					bRet = true;
				
			case iExperimentEvaluation:
				//check if the previous stages were completed 
				if(this.expSetup.getState()==ExperimentPhase.STATE_COMPLETED && this.expApproval.getState()==ExperimentPhase.STATE_COMPLETED && this.expExecution.getState()==ExperimentPhase.STATE_COMPLETED)
					bRet = true;
		}
		
		return bRet;
	}


}
