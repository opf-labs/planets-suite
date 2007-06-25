package eu.planets_project.tb.api.model;

/**
 * @author alindley
 * This interface covers step 6 of the Testbed workflow: Evaluate Experiment
 * The constructor of the ExperimentAnalysis Object needs to take an 
 *  - ExperimentObjectives and an ExperimentResults object as input in the constructor.
 *  
 *  Note: It's still not toally clear what exactly is going to be evaluated
 *
 */
public interface ExperimentEvaluation extends ExperimentPhase{
	
	//ExperimentObjectives werte hinzufügen
	//was sonst noch bei Analysis?
	
	public ExperimentObjectives evaluateExperimentObjectives();
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
}
