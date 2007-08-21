package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.Set;
import eu.planets_project.tb.api.model.Experiment;

public interface TestbedManager {
	
	//Manages Experiments
	public void registerExperiment(Experiment experimentBean);
	public Experiment getExperiment(long lExpID);
	public void removeExperiment(long lExpID);
	public Collection<Experiment> getAllExperiments();
	public Set<Long> getAllExperimentIDs();
	public boolean containsExperiment(long expID);
	/**
	 * Using this convenience method for creating a new Experiment object
	 * @return
	 */
	public Experiment createNewExperiment();
	

	// Comment Management
	public CommentManager getCommentManager();
	
	//Manages ..

}
