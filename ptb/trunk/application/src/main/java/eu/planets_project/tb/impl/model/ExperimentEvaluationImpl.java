/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.ExperimentObjectives;
import eu.planets_project.tb.api.model.ExperimentReport;

/**
 * @author alindley
 *
 */
//@Entity
public class ExperimentEvaluationImpl extends eu.planets_project.tb.impl.model.ExperimentPhaseImpl
implements eu.planets_project.tb.api.model.ExperimentEvaluation, java.io.Serializable {

	//@Id
	//@GeneratedValue
	private long lEntityID;
	private ExperimentObjectives evaluatedExpObjectives;
	private ExperimentObjectives inputObjectives;
	private ExperimentReport expReport;
	
	
	public ExperimentEvaluationImpl(ExperimentObjectives inputObjectives){
		this.inputObjectives = inputObjectives;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateExperimentObjectives(ExperimentObjectives)
	 */
	public void setEvaluatedExperimentObjectives(ExperimentObjectives objectives) {
		this.evaluatedExpObjectives = objectives;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentObjectives()
	 */
	public ExperimentObjectives getEvaluatedExperimentObjectives(){
		return this.evaluatedExpObjectives;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getExperimentReport()
	 */
	public ExperimentReport getExperimentReport() {
		return this.expReport;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setExperimentReport(eu.planets_project.tb.api.model.ExperimentReport)
	 */
	public void setExperimentReport(ExperimentReport report) {
		this.expReport = report;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.model.ExperimentPhase#getEntityID()
	 */
	//@Override
	public long getEntityID() {
		return this.lEntityID;
	}

}
