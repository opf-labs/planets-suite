package eu.planets_project.tb.api.model.benchmark;

/**
 * @author alindley
 * This class is the model representation of a for a certain 
 * For the handle on the XML benchmark objective file see BenchmarkObjectiveHandle
 * <p>
 * 
 * Use BenchmarkObjectivesHandle for reading the XML file and retrieving available Objectives
 * Objectives is the Java model representation of an objective; containing all static attributes. 
 * ExperimentObjectives represents a container object. There a selection of objectives that are relevant and that should be added for evaluation to an experiment are added.
 * The ExperimentObjectives is also the class that holds state (for value and weight)
 * 
 * <p>
 * Note: If you want to use the same Objective twice (within an experiment or in 1..n experiments) it is not necessary to retrieve a new object from the BenchmarkObjectivesHandle as this Object does not contain state (and therefore is unique within the context of the Testbed).
 * e.g. two Objective objects describing the same content would have the same IDs.
 * 
 */
public interface Objective {
	
	public long getObjectiveID();
	public String getObjectiveName();
	public String getObjectiveDescription();
	/**
	 * @param sPath absolute navigation path. e.g. root/performance/perf123 
	 */
	public String getObjectivePath();
	
	public void setVersion(String sVersion);
	public String getVersion();

}
