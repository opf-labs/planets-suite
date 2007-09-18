package eu.planets_project.tb.api.model;

import java.io.File;
import java.net.URI;
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
	
	public List<BenchmarkGoal> getAddedExperimentBenchmarkGoals();
	/**
	 * Goals to evaluate the entire experiment
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateExperimentBenchmarkGoal(BenchmarkGoal addedBenchmarkGoal, String value);
	public List<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals();
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String sGoalXMLID);
	/**
	 * Goals applied to a single input - output File tuple 
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, BenchmarkGoal addedBenchmarkGoal, String value);
	public void evaluateFileBenchmarkGoal(Entry<URI,URI> ioFile, BenchmarkGoal addedBenchmarkGoal, String value);
	public List<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile);
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile, String sGoalXMLID);
	
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
	public File getExperimentReportFile();
}
