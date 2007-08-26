package eu.planets_project.tb.impl.persistency;

import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.tb.api.model.Experiment;

@Remote
public interface ExperimentPersistencyRemote {
	
	public long persistExperiment(Experiment experiment);
	public Experiment findExperiment(long id);
	
	public void updateExperiment(Experiment experiment);
	public void deleteExperiment(long id);
	public void deleteExperiment(Experiment experiment);
	public List<Experiment> queryAllExperiments();

}
