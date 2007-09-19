package eu.planets_project.tb.api.model;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;

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
	
	
	/**
	 * Goals to evaluate the entire experiment
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID, String value);
	public Collection<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals();
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String sGoalXMLID);
	/**
	 * Goals applied to a single input - output File tuple 
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID, String value);
	public void evaluateFileBenchmarkGoal(Entry<URI,URI> ioFile, String addedBenchmarkGoalID, String value);
	public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile);
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile, String sGoalXMLID);
	
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
	public File getExperimentReportFile();
	
	/**
	 * Takes the ExperimentSetup object and extracts the added BenchmarkGoals if the benchmarkListisFinal() was set to true
	 * @param inputBenchmarkGoals
	 */
	public void setInput(ExperimentSetup expSetup);
	/**
	 * Returns the List of inputBenchmarkGoals which the evaluation uses as basis for the experimentBenchmarks as well as for the fileBenchmarks
	 * @return emty list if either no benchmark was defined in the SetupPhase or when the benchmarkListisFinal is not true
	 */
	public Collection<String> getInputBenchmarkGoalIDs();
}
