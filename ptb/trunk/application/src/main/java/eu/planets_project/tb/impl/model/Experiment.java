/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.sql.Date;
import java.util.GregorianCalendar;

import eu.planets_project.tb.api.data.DataRegistryBinding;
import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentSetup;

/**
 * @author alindley
 *
 */
public class Experiment implements eu.planets_project.tb.api.model.Experiment,
									java.io.Serializable{

	public ExperimentEvaluation getExperimentAnalysis() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentApproval getExperimentApproval() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentExecution getExperimentExecution() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getExperimentID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ExperimentSetup getExperimentSetup() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExperimentAnalysis(ExperimentEvaluation analysisPhase) {
		// TODO Auto-generated method stub
		
	}

	public void setExperimentApproval(ExperimentApproval approvalPhase) {
		// TODO Auto-generated method stub
		
	}

	public void setExperimentExecution(ExperimentExecution executionPhase) {
		// TODO Auto-generated method stub
		
	}

	public void setExperimentSetup(ExperimentSetup setupPhaseObject) {
		// TODO Auto-generated method stub
		
	}

	public long getDurationInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	public GregorianCalendar getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getEndDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPhaseID() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	public GregorianCalendar getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getStartDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInProgress() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNotStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEndDate(GregorianCalendar endDate) {
		// TODO Auto-generated method stub
		
	}

	public void setProgress(int progress) {
		// TODO Auto-generated method stub
		
	}

	public void setStartDate(GregorianCalendar startDate) {
		// TODO Auto-generated method stub
		
	}

	public void setState(int state) {
		// TODO Auto-generated method stub
		
	}


}
