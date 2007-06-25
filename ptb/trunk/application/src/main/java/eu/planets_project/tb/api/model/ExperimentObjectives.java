/**
 * 
 */
package eu.planets_project.tb.api.model;

import java.util.Hashtable;

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
 */
public interface ExperimentObjectives{
	
	
	/**
	 * Sets the given objectives<objective,[value,weight]> with their ID and their value. 
	 * "set" always overrides "add".
	 * @param objectives
	 * @see setObjectiveValue is the same for 1 instead of 1..n objectives
	 */
	public void setSelectedObjectives(Hashtable<Objective,String[]> objectivesAndValues);
	
	/**
	 * 
	 * @param sObjectiveID
	 * @param value
	 */
	public void setSelectedObjectiveValue(String sObjectiveID, String value, String weight);
	public void setSelectedObjectiveValue(Objective objective, String value, String weight);
	
	public void addObjective(String sObjectiveID);
	public void addObjective(Objective objective);
	
	public void addObjectives(String[] sObjectiveIDs);
	public void addObjectives(Objective[] objectives);
	
	public void removeObjective(String sObjectiveID);
	public void removeObjective(Objective Objective);
	public void removeObjectives(String[] sObjectiveIDs);
	public void removeObjectives(Objective[] objectives);
	
	/**
	 * Returns all added objectives.
	 * @return Objective
	 */
	public Objective[] getAllAddedObjectives();
	
	/**
	 * Adds a given objective to the set of used ExperimentObjectives and sets the objective's focus
	 * @param sObjectiveID
	 * @param iFocus focus can be [important]1..5[not very important]
	 */
	public void addObjective(String sObjectiveID, String sValue, String sWeight);
	
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
	

}
