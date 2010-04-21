package eu.planets_project.tb.api.model;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.PropertyEvaluationRecordImpl;

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
	 * All Property Evaluation Records for this Experiment for a given digitalObjectInputRef
	 * @return
	 */
	public HashMap<String,ArrayList<PropertyEvaluationRecordImpl>> getPropertyEvaluations();
	public void addPropertyEvaluation(String digObjInputRef, PropertyEvaluationRecordImpl propEval);
	public ArrayList<PropertyEvaluationRecordImpl> getPropertyEvaluation(String inputDigitalObjectRef);
	public Integer getOverallPropertyEvalWeight(String propertyID);
	
	/**
	 * The weights to apply for properties for calculating the experiment's overall evaluation result
	 * @return
	 */
	public HashMap<String,Integer> getOverallPropertyEvalWeights();
	public void addOverallPropertyEvalWeights(String propertyID, Integer weight);
	public void setOverallPropertyEvalWeights(HashMap<String,Integer> propertyWeights);
	
	public void setExperimentReport(ExperimentReport report);
	public ExperimentReport getExperimentReport();
	public File getExperimentReportFile();
	
	public void setExperimentRating(int rating);
	public int getExperimentRating();
	
	public void setServiceRating(int rating);
	public int getServiceRating();
	
	/**
	 * Goals to evaluate the entire experiment
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	@Deprecated
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
	@Deprecated
	public Collection<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals();
	@Deprecated
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String sGoalXMLID);
	/**
	 * Takes a List of Benchmark Objects, extracts their IDs and values to create a new evaluatedExperimentBenchmarkGoal
	 * with the ID and sets it's value according to the input. Only BenchmarkGoals that have been
	 * set in the ExperimentSetup phase are taken into account.
	 * @param addedBMGoals
	 */
	@Deprecated
	public void setEvaluatedExperimentBenchmarkGoals(List<BenchmarkGoal> lBMGoals) throws InvalidInputException;
	@Deprecated
	public void setEvaluatedExperimentSourceBenchmarkGoals (List<BenchmarkGoal> addedSourceBMGoals) throws InvalidInputException;
	@Deprecated
	public void setEvaluatedExperimentTargetBenchmarkGoals (List<BenchmarkGoal> addedTargetBMGoals) throws InvalidInputException;
	
	
	/**
	 * Goals applied to a single input - output File tuple 
	 * @param addedBenchmarkGoal
	 * @param value
	 */
	@Deprecated
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
	@Deprecated
	public void evaluateFileBenchmarkGoal(Entry<URI,URI> ioFile, String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException;
	@Deprecated
	public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile);
	@Deprecated
	public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals();
	@Deprecated
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile, String sGoalXMLID);
	/**
	 * Takes a List of Benchmark Objects, extracts their IDs and values to create a new evaluatedFileBenchmarkGoal
	 * with the ID and sets it's value according to the input. Only BenchmarkGoals that have been
	 * set in the ExperimentSetup phase are taken into account.
	 * @param addedBMGoals
	 */
	@Deprecated
	public void setEvaluatedFileBenchmarkGoals(Map<URI,List<BenchmarkGoal>> addedFileBMGoals) throws InvalidInputException;
	
	/**
	 * This is the setter, handing over a list of File BMGoals, all other evaluation methods may use to evaluate
	 * @param addedFileBMGoals
	 */
	@Deprecated
	public void setInputFileBenchmarkGoals(Map<URI,Collection<BenchmarkGoal>> addedFileBMGoals);
	/**
	 * This is the setter, handing over a list of Experiment Overall BMGoals, all other evaluation methods may use to evaluate
	 * @param addedFileBMGoals
	 */
	@Deprecated
	public void setInputExperimentBenchmarkGoals(Collection<BenchmarkGoal> addedOverallBMGoals);
	
	/**
	 * Returns the List of inputBenchmarkGoals which the evaluation uses as basis for the experimentBenchmarks as well as for the fileBenchmarks
	 * @return emty list if either no benchmark was defined in the SetupPhase or when the benchmarkListisFinal is not true
	 */
	@Deprecated
	public Collection<String> getInputBenchmarkGoalIDs();
	@Deprecated
	public List<String> getAllAcceptedEvaluationValues();
	
	/**
	 * returns a list of digital object references that were provided during the evaluation stage of an experiment
	 * e.g. manual-excel spreadsheets, etc.
	 * @return
	 */
	public ArrayList<String> getExternalEvaluationDocuments();
	public void setExternalEvaluationDocuments(ArrayList<String> records);
	public void addExternalEvaluationDocument(String digitalObjectRef);
	public void removeExternalEvaluationDocument(String digitalObjectRef);
}
