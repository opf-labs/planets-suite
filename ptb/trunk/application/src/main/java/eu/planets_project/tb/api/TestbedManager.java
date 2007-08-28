package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.Set;
import eu.planets_project.tb.api.model.Experiment;

public interface TestbedManager {
	
	//Manages Experiments
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
	public Collection<Experiment> getAllExperimentsOfUsers(String sUserID);
	public Collection<Experiment> getAllExperimentsOfType(int iTypeID);
	public Set<Long> getAllExperimentIDs();
	public boolean containsExperiment(long expID);
	/**
	 * Using this convenience method for creating a new Experiment object
	 * @return
	 */
	public Experiment createNewExperiment();
	

	// Comment Management
	public CommentManager getCommentManagerInstance();
	
	//Manages ..

}
