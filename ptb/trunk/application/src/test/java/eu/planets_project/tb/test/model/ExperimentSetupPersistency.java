package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.ExperimentSetup;

@Stateless
public class ExperimentSetupPersistency implements ExperimentSetupPersistencyRemote{
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	public void deleteExperimentSetup(long id) {
		ExperimentSetup t_helper = manager.find(ExperimentSetup.class, id);
		manager.remove(t_helper);
		
	}

	public void deleteExperimentSetup(ExperimentSetup expSetup) {
		ExperimentSetup t_helper = manager.find(ExperimentSetup.class, expSetup.getExperimentSetupID());
		manager.remove(t_helper);		
	}

	public ExperimentSetup findExperimentSetup(long id) {
		return manager.find(ExperimentSetup.class, id);
	}

	public long persistExperimentSetup(ExperimentSetup expSetup) {
		manager.persist(expSetup);
		return expSetup.getExperimentSetupID();
	}

	public void updateExperimentSetup(ExperimentSetup expSetup) {
		manager.merge(expSetup);
	}

}
