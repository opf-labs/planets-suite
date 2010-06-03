package eu.planets_project.tb.api.model;


public interface ExperimentReport {
	
	/*public String getExecutionMetadata();*/

	public void setHeader(String text);
	public String getHeader();
	
	public void setBodyText(String text);
	public String getBodyText();
	
	/*public List<String> getGeneralBenchmarkGoals();
	public List<String> getBenchmarkGoalsForFiles();*/

}
