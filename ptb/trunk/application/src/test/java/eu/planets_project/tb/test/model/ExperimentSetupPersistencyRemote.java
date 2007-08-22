package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.ExperimentSetupImpl;

@Remote
public interface ExperimentSetupPersistencyRemote {
	
	public long persistExperimentSetup(ExperimentSetupImpl expSetup);
	public ExperimentSetupImpl findExperimentSetup(long id);
	
	public void updateExperimentSetup(ExperimentSetupImpl expSetup);
	public void deleteExperimentSetup(long id);
	public void deleteExperimentSetup(ExperimentSetupImpl expSetup);
}
