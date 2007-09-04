package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.Set;
import eu.planets_project.tb.api.model.Experiment;

public interface TestbedManager {
	
	//TODO weiteren Kommentar zu registerExpeirment (in Bezug auf getExperiment) schreiben
	/**
	 * Registers an Experiment within the TestbedManager
	 * It's not possible to register a previously registered experiment as the container cannot inject a valid ID
	 * @param experimentBean
	 * @return the Experiments entityID; -1 if Experiment was already previously registered
	 */
	public long registerExperiment(Experiment experimentBean);
	/**
	 * This method looks-up the experimentBean in the manager and if it was already contained
	 * a merge between the old and the new instance is performed.
	 * @param experimentBean
	 */
	public void updateExperiment(Experiment experimentBean);
	public Experiment getExperiment(long lExpID);
	public void removeExperiment(long lExpID);
	public Collection<Experiment> getAllExperiments();
	
	/**
	 * Returns a collection of experiments
	 * when bIsExperimenter=true: where the provided userID is experimenter
	 * when bIsExperimenter=false: where the provided userID is InvolvedUser
	 * @param sUserID
	 * @return
	 */
	public Collection<Experiment> getAllExperimentsOfUsers(String sUserID, boolean bIsExperimenter);
	public Collection<Experiment> getAllExperimentsOfType(int iTypeID);
	public Set<Long> getAllExperimentIDs();
	public boolean containsExperiment(long expID);
	public boolean isExperimentNameUnique(String sExpName);
	public boolean isRegistered(long lExpID);
	public boolean isRegistered(Experiment experiment);
	
	public int getNumberOfExperiments();

	/**
	 * Returns the number of experiments for a certain given user
	 * @param sUserID
	 * @param bExperimenter: true: where user is Experimenter; false: where user is Involved
	 * @return
	 */
	public int getNumberOfExperiments(String sUserID, boolean bExperimenter);
	
	/**
	 * Using this convenience method for creating a new Experiment object
	 * @return
	 */
	public Experiment createNewExperiment();
	

	// Comment Management
	public CommentManager getCommentManagerInstance();
	
	//Manages ..

}
