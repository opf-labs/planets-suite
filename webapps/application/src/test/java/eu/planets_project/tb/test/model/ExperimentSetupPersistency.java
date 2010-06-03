package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.ExperimentSetupImpl;

@Stateless
public class ExperimentSetupPersistency implements ExperimentSetupPersistencyRemote{
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	public void deleteExperimentSetup(long id) {
		ExperimentSetupImpl t_helper = manager.find(ExperimentSetupImpl.class, id);
		manager.remove(t_helper);
		
	}

	public void deleteExperimentSetup(ExperimentSetupImpl expSetup) {
		ExperimentSetupImpl t_helper = manager.find(ExperimentSetupImpl.class, expSetup.getEntityID());
		manager.remove(t_helper);		
	}

	public ExperimentSetupImpl findExperimentSetup(long id) {
		return manager.find(ExperimentSetupImpl.class, id);
	}

	public long persistExperimentSetup(ExperimentSetupImpl expSetup) {
		manager.persist(expSetup);
		return expSetup.getEntityID();
	}

	public void updateExperimentSetup(ExperimentSetupImpl expSetup) {
		manager.merge(expSetup);
	}

}
