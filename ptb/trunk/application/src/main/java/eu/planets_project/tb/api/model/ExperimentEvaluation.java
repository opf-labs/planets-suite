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
	
	/**
	 * This method takes a set of given inputObjectives, evaluates them and produces a set of output ExperimentObjectives
	 * @param inputObjectives
	 * @return
	 */
	//public void setEvaluatedExperimentObjectives(ExperimentObjectives inputObjectives);
	//public ExperimentObjectives getEvaluatedExperimentObjectives();
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
}
