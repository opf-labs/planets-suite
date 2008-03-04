package eu.planets_project.tb.api.model;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;

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
	
	public final String EVALUATION_VALUE_VERY_GOOD = "very good";
	public final String EVALUATION_VALUE_GOOD = "good";
	public final String EVALUATION_VALUE_BAD = "bad";
	public final String EVALUATION_VALUE_VERY_BAD = "very bad";
	
	/**
	 * Goals to evaluate the entire experiment
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
	public Collection<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals();
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String sGoalXMLID);
	/**
	 * Takes a List of Benchmark Objects, extracts their IDs and values to create a new evaluatedExperimentBenchmarkGoal
	 * with the ID and sets it's value according to the input. Only BenchmarkGoals that have been
	 * set in the ExperimentSetup phase are taken into account.
	 * @param addedBMGoals
	 */
	public void setEvaluatedExperimentBenchmarkGoals(List<BenchmarkGoal> lBMGoals) throws InvalidInputException;
	public void setEvaluatedExperimentSourceBenchmarkGoals (List<BenchmarkGoal> addedSourceBMGoals) throws InvalidInputException;
	public void setEvaluatedExperimentTargetBenchmarkGoals (List<BenchmarkGoal> addedTargetBMGoals) throws InvalidInputException;
	
	
	/**
	 * Goals applied to a single input - output File tuple 
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
	public void evaluateFileBenchmarkGoal(Entry<URI,URI> ioFile, String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
    public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile);
    public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals();
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile, String sGoalXMLID);
	/**
	 * Takes a List of Benchmark Objects, extracts their IDs and values to create a new evaluatedFileBenchmarkGoal
	 * with the ID and sets it's value according to the input. Only BenchmarkGoals that have been
	 * set in the ExperimentSetup phase are taken into account.
	 * @param addedBMGoals
	 */
	public void setEvaluatedFileBenchmarkGoals(Map<URI,List<BenchmarkGoal>> addedFileBMGoals) throws InvalidInputException;
	
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
	public File getExperimentReportFile();
	
	
	/**
	 * Returns the List of inputBenchmarkGoals which the evaluation uses as basis for the experimentBenchmarks as well as for the fileBenchmarks
	 * @return emty list if either no benchmark was defined in the SetupPhase or when the benchmarkListisFinal is not true
	 */
	public Collection<String> getInputBenchmarkGoalIDs();
	
	public List<String> getAllAcceptedEvaluationValues();
}
