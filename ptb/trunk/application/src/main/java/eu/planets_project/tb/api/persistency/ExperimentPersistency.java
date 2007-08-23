package eu.planets_project.tb.api.persistency;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

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
		//DELTE
		ExperimentImpl exp = manager.find(ExperimentImpl.class, id);
		if(exp==null){
			System.out.println("find: null");
		}else{
			System.out.println("find not null");
			System.out.println("Found ID: "+exp.getEntityID());
			System.out.println("Found state: "+exp.getState());
			System.out.println("OBj: "+exp);
		}
		//END DELTE
		return manager.find(ExperimentImpl.class, id);
	}

	public long persistExperiment(Experiment experiment) {
		manager.persist(experiment);
		return experiment.getEntityID();
	}

	public void updateExperiment(Experiment experiment) {
		manager.merge(experiment);
	}

}
