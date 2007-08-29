package eu.planets_project.tb.api.model.benchmark;

import java.util.List;

public interface BenchmarkGoalsHandler {
	
	public void buildBenchmarkGoalsFromXML();
	public List<BenchmarkGoal> getAllBenchmarkGoals();
	/**
	 * This method is used to retrieve all available benchmark goals for a given category
	 * @see the BenchmarkGoals.xsd for additional information
	 * @param sCategoryName
	 * @return
	 */
	public List<BenchmarkGoal> getAllBenchmarkGoals(String sCategoryName);
	public List<String> getAllBenchmarkGoalIDs(String sCategoryName);
	public BenchmarkGoal getBenchmarkGoal(String sID);
	public List<String> getCategoryNames();
	

}
