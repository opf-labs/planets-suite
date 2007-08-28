package eu.planets_project.tb.impl.persistency;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.model.ExperimentImpl;

@Stateless
public class ExperimentPersistencyImpl implements ExperimentPersistencyRemote{
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	public void deleteExperiment(long id) {
		ExperimentImpl t_helper = manager.find(ExperimentImpl.class, id);
		manager.remove(t_helper);
	}

	public void deleteExperiment(Experiment experiment) {
		ExperimentImpl t_helper = manager.find(ExperimentImpl.class, experiment.getEntityID());
		manager.remove(t_helper);
	}

	public Experiment findExperiment(long id) {
		return manager.find(ExperimentImpl.class, id);
	}

	public long persistExperiment(Experiment experiment) {
		manager.persist(experiment);
		return experiment.getEntityID();
	}

	public void updateExperiment(Experiment experiment) {
		manager.merge(experiment);
	}

	public List<Experiment> queryAllExperiments() {
		Query query = manager.createQuery("from ExperimentImpl");
		return query.getResultList();
	}

}
