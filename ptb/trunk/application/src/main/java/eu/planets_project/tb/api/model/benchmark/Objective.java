package eu.planets_project.tb.api.model.benchmark;

/**
 * @author alindley
 * This class is the model representation of a for a certain 
 * For the handle on the XML benchmark objective file see BenchmarkObjectiveHandle
 * <p>
 * 
 * Use BenchmarkObjectivesHandle for reading the XML file and retrieving available Objectives
 * Objectives is the Java model representation of an objective; containing all attributes (inkl. value). Objectives that are handed over by the BenchmarkObjectiveHandle do contain every property except it's actual value. 
 * ExperimentObjectives represents a container object. There a selection of objectives that are relevant and that should be added for evaluation to an experiment are added. 
 * 
 * <p>
 * Note: If you want to use the same Objective twice (within an experiment or in 1..n experiments) it is necessary to retrieve a new object from the BenchmarkObjectivesHandle as this Object contains state (and therefore only is unique within the context of an Experiment).
 * e.g. two Objective objects describing the same content would have different IDs.
 * 
 */
public interface Objective {
	
	/**
	 * @param sPath absolute navigation path. e.g. root/performance/perf123 
	 */
	public String getObjectiveID();
	public String getObjectiveName();
	public String getObjectiveDescription();
	public String getObjectivePath();
	
	
	/**
	 * Objectives that are handed over by the BenchmarkObjectiveHandle do contain every property except it's actual value and its weight within an Experiment
	 * @param sValue set objective's value according to its description and unit
	 */
	public void setValue(String sValue);
	public String getValue();
	
	/**
	 * Objectives that are handed over by the BenchmarkObjectiveHandle do contain every property except it's actual value and its weight within an Experiment
	 * When no sWeight is set a default value is chosen.
	 * @param sWeight
	 */
	public void setWeight(String sWeight);
	public String getWeight();

}
