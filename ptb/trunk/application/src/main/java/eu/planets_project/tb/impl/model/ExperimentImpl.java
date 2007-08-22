/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

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
public class ExperimentImpl extends eu.planets_project.tb.impl.model.ExperimentPhaseImpl
						implements eu.planets_project.tb.api.model.Experiment,
									java.io.Serializable{

	@Id
	@GeneratedValue
	private long lEntityID;
	private ExperimentEvaluationImpl expEvaluation;
	private ExperimentApprovalImpl expApproval;
	private ExperimentExecutionImpl expExecution;
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentSetupImpl expSetup;
	
	public ExperimentImpl(){
		
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentAnalysis()
	 */
	public ExperimentEvaluationImpl getExperimentAnalysis() {
		return this.expEvaluation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentApproval()
	 */
	public eu.planets_project.tb.api.model.ExperimentApproval getExperimentApproval() {
		return this.expApproval;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentExecution()
	 */
	public eu.planets_project.tb.api.model.ExperimentExecution getExperimentExecution() {
		return this.expExecution;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentID()
	 */
	public long getExperimentID() {
		return this.getEntityID();
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getExperimentSetup()
	 */
	public eu.planets_project.tb.api.model.ExperimentSetup getExperimentSetup() {
		return this.expSetup;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentAnalysis(eu.planets_project.tb.api.model.ExperimentEvaluation)
	 */
	public void setExperimentAnalysis(eu.planets_project.tb.api.model.ExperimentEvaluation analysisPhase) {
		this.expEvaluation = (ExperimentEvaluationImpl)analysisPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentApproval(eu.planets_project.tb.api.model.ExperimentApproval)
	 */
	public void setExperimentApproval(eu.planets_project.tb.api.model.ExperimentApproval approvalPhase) {
		this.expApproval = (ExperimentApprovalImpl)approvalPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentExecution(eu.planets_project.tb.api.model.ExperimentExecution)
	 */
	public void setExperimentExecution(eu.planets_project.tb.api.model.ExperimentExecution executionPhase) {
		this.expExecution = (ExperimentExecutionImpl)executionPhase;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#setExperimentSetup(eu.planets_project.tb.api.model.ExperimentSetup)
	 */
	public void setExperimentSetup(eu.planets_project.tb.api.model.ExperimentSetup setupPhaseObject) {
		this.expSetup = (ExperimentSetupImpl)setupPhaseObject;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Experiment#getCurrentPhase()
	 */
	public ExperimentPhaseImpl getCurrentPhase() {
		//TODO implement
		return null;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.model.ExperimentPhase#getPhaseID()
	 */
	public long getEntityID() {
		return this.lEntityID;
	}
	
	private void setEntityID(long entityID){
		this.lEntityID = entityID;
	}

}
