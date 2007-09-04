package eu.planets_project.tb.api.model;

/**
 *
 * -Phases: Every Experiment consists out of four phases Setup, Approval, Execution and Analysis
 * Every Phase may consist itself of multiple steps (e.g. setup experiment includes: fill in basic properties, design a workflow, etc.). 
 * Within this steps it's possible to navigate forward and backward - afer a phase has completed its data may not be modified any longer. 
 * <p>
 * - Status: Every phase contains a status as well as there's an overall status status for the entire Experiment
 * @author alindley
 *
 */
public interface Experiment extends ExperimentPhase{

	//public long getExperimentID();
	
	public void setExperimentSetup(ExperimentSetup setupPhaseObject);
	public ExperimentSetup getExperimentSetup();
	
	public void setExperimentApproval(ExperimentApproval approvalPhase);
	public ExperimentApproval getExperimentApproval();
	
	public void setExperimentExecution(ExperimentExecution executionPhase);
	public ExperimentExecution getExperimentExecution();
	
	public void setExperimentAnalysis(ExperimentEvaluation analysisPhase);
	public ExperimentEvaluation getExperimentAnalysis();
	
	public ExperimentPhase getCurrentPhase();
	public int getCurrentPhasePointer();
	
}
