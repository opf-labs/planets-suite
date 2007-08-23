/**
 * 
 */
package eu.planets_project.tb.api.model;

import java.util.Hashtable;
import java.util.Vector;

import eu.planets_project.tb.api.model.benchmark.Objective;

/**
 * The use of an Objective object will normally be split up into two stages. 
 * 1. First in the ExperimentSetup a set of given objectives are choosen and each's focus for this 
 *    experiment is chosen
 * 2. In the ExperimentAnalysis these selected benchmarks are filled in with values. 
 * 
 * Note: It still needs clarification whether additional benchmarking objectives may get added in the analysis phase
 * or if the set should be final after the setup phase.
 * @author alindley
 * 
 * Note: please not the ExperimentObjectives does not only hold the pointer but holds the Objective object itself
 * As Objectives may vary over time - this solution is used to have the information attached through the ExperimentObjectives
 *
 */
public interface ExperimentObjectives{
	
	
	/**
	 * Adds a given Objective to the ones used within this Experiment (=ExperimentObjectives)
	 */
	public void setObjectivesAsSelected(Objective objectiveD);
	public void setObjectivesAsSelected(Vector<Objective> objectives);
	
	public void addObjective(Objective objective);
	
	/**
	 * Sets the given objectives (ID) with its value and weight
	 * "set" always overrides "add".
	 * @param Objectives
	 */
	public void addObjectives(Vector<Objective> Objectives);
	
	public void removeObjective(long sObjectiveID);
	public void removeObjective(Objective Objective);
	public void removeObjectives(Vector<Objective> Objectives);
	
	public Objective getObjective(long sObjectiveID);
	
	/**
	 * Returns all added objectives.
	 * @return Objective
	 */
	public Vector<Objective> getAllAddedObjectives();
	
	/**
	 * Adds a given objective to the set of used ExperimentObjectives and sets the objective's focus
	 * @param sObjectiveID
	 * @param iFocus focus can be [important]1..5[not very important]
	 */
	public void addObjective(Objective objective, String sValue, String sWeight);
	
	/**
	 * Note: It still needs clarification whether additional benchmarking objectives may get added in the analysis phase
	 * or if the set should be final after the setup phase.
	 * <p>
	 * With this method 
	 * This method does not take a boolean as input, as this flag should be irreversible 
	 */
	public void setObjectiveListFinal();
	
	/**
	 * Mainly will be used internaly by methods within this class, but not only.
	 * @return
	 * @see setObjectiveListFinal();
	 */
	public boolean isObjectiveListFinal();
	
	/**
	 * Objectives that are handed over by the BenchmarkObjectiveHandle do contain every property except it's actual value and its weight within an Experiment
	 * @param sValue set objective's value according to its description and unit
	 */
	public void setValue(Objective Objective, String sValue);
	public String getValue(Objective Objective);
	/**
	 * Precondition: the Objective already needs to be set with "setObjectiveAsSelected" to execute this method
	 * @param lObjectiveID
	 * @param sValue
	 */
	public void setValue(Long lObjectiveID, String sValue);
	public String getValue(Long lObjectiveID);
	
	/**
	 * Objectives that are handed over by the BenchmarkObjectiveHandle do contain every property except it's actual value and its weight within an Experiment
	 * When no sWeight is set a default value is chosen.
	 * @param sWeight
	 */
	public void setWeight(Objective objective, String sWeight);
	public String getWeight(Objective Objective);
	/**
	 * Precondition: the Objective already needs to be set with "setObjectiveAsSelected" to execute this method
	 * @param lObjectiveID
	 * @param sValue
	 */
	public void setWeight(Long lObjectiveID, String sValue);
	public String getWeight(Long lObjectiveID);
	

}
