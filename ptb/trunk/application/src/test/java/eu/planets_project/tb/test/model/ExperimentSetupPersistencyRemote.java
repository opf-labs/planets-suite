package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.ExperimentSetup;

@Remote
public interface ExperimentSetupPersistencyRemote {
	
	public long persistExperimentSetup(ExperimentSetup expSetup);
	public ExperimentSetup findExperimentSetup(long id);
	
	public void updateExperimentSetup(ExperimentSetup expSetup);
	public void deleteExperimentSetup(long id);
	public void deleteExperimentSetup(ExperimentSetup expSetup);
}
