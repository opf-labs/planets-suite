package eu.planets_project.TB.api.interfaces;

import eu.planets_project.TB.api.interfaces.model.Experiment;

public interface TestbedManager {
	
	//Manages Experiments
	public void registerExperiment(Experiment experimentBean);
	public Experiment getExperiment(long lExpID);
	public void removeExperiment(long lExpID);
	public Experiment[] getAllExperiments();
	public long[] getAllExperimentIDs();
	/**
	 * Using this convenience method for creating a new Experiment object
	 * @return
	 */
	public Experiment getNewExperiment();
	
	// User Management
	public UserManager getUserManager();
	
	// Comment Management
	public CommentManager getCommentManager();
	
	//Manages ..

}
