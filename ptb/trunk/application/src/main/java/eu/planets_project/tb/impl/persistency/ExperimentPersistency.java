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
import eu.planets_project.tb.impl.model.ExperimentImpl;

@Stateless
public class ExperimentPersistency implements ExperimentPersistencyRemote{
	
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

	public eu.planets_project.tb.api.model.Experiment findExperiment(long id) {
		System.out.println("ExpPersistency: FindExperiment: Hier1 "+id);
		ExperimentImpl exp = manager.find(ExperimentImpl.class, id);
		System.out.println("ExpPersistency: FindExperiment: Hier2 "+exp.getEntityID()+" state:"+exp.getState());

		//return manager.find(ExperimentImpl.class, id);
		return exp;
	}

	public long persistExperiment(Experiment experiment) {
		System.out.println("ExpPersistency: persistExperiment1: "+experiment.getEntityID()+" with state: "+experiment.getState());
		manager.persist(experiment);
		System.out.println("ExpPersistency: persistExperiment2: now with id: "+experiment.getEntityID()+ "state: "+experiment.getState());
		System.out.print("ExpPersistency: persistExperiment3: can I find exp here? ");
		ExperimentImpl tester = manager.find(ExperimentImpl.class, experiment.getEntityID());
		System.out.println(tester.getEntityID());
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
