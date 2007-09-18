package eu.planets_project.tb.api.model.benchmark;

import java.util.List;

public interface BenchmarkHandler {
	
	public void buildBenchmarksFromXML();
	public List<Benchmark> getAllBenchmarks();
	/**
	 * This method is used to retrieve all available benchmark goals for a given category
	 * @see the BenchmarkGoals.xsd for additional information
	 * @param sCategoryName
	 * @return
	 */
	public List<Benchmark> getAllBenchmarks(String sCategoryName);
	public List<String> getAllBenchmarkIDs(String sCategoryName);
	public List<String> getAllBenchmarkIDs();
	public Benchmark getBenchmark(String sID);
	public List<String> getCategoryNames();
	

}
