package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.BasicProperties;
import eu.planets_project.tb.impl.model.Comment;
import eu.planets_project.tb.impl.model.Experiment;

@Stateless
public class ExperimentPersistency implements ExperimentPersistencyRemote{
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	public void deleteExperiment(long id) {
		Experiment t_helper = manager.find(Experiment.class, id);
		manager.remove(t_helper);
	}

	public void deleteExperiment(Experiment experiment) {
		Experiment t_helper = manager.find(Experiment.class, experiment.getExperimentID());
		manager.remove(t_helper);
	}

	public Experiment findExperiment(long id) {
		return manager.find(Experiment.class, id);
	}

	public long persistExperiment(Experiment experiment) {
		manager.persist(experiment);
		return experiment.getExperimentID();
	}

	public void updateExperiment(Experiment experiment) {
		manager.merge(experiment);
	}

}
