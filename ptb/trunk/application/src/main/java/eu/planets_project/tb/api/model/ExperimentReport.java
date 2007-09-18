package eu.planets_project.tb.api.model;

import java.util.List;

public interface ExperimentReport {
	
	public String getExecutionMetadata();

	public void setText(String report);
	public String getText();
	
	public List<String> getGeneralBenchmarkGoals();
	public List<String> getBenchmarkGoalsForFiles();

}
