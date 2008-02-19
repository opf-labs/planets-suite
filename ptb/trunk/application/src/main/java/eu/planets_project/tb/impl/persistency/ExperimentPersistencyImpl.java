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

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

@Stateless
public class ExperimentPersistencyImpl implements ExperimentPersistencyRemote{
	
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentPersistencyImpl.class);
    
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
	    log.debug("Persisting experiment: " + experiment.getExperimentSetup().getBasicProperties().getExperimentName() );
		manager.persist(experiment);
		return experiment.getEntityID();
	}

	public void updateExperiment(Experiment experiment) {
        log.debug("Updating experiment: " + experiment.getExperimentSetup().getBasicProperties().getExperimentName() );
		manager.merge(experiment);
	}

	public List<Experiment> queryAllExperiments() {
		Query query = manager.createQuery("from ExperimentImpl");
		return query.getResultList();
	}

	public boolean queryIsExperimentNameUnique(String expName) {
	    log.debug("Checking uniqueness of exp. name: " + expName );
		Query query = manager.createQuery("SELECT sExpName FROM BasicPropertiesImpl WHERE LOWER(sExpName)=LOWER(:expname)");
		query.setParameter("expname", expName);
		List<String> results = (List<String>) query.getResultList();
		if(results.size()==0)
			return true;
		
		return false;
	}
	
	public List<Experiment> searchAllExperiments( String toFind ) {
        // Not case sensitive, wildcarded:
	    toFind = "%"+toFind.toLowerCase()+"%";
        log.debug("Searching for experiments that match: " + toFind );
        Query query = manager.createQuery("FROM ExperimentImpl AS e WHERE LOWER(e.expSetup.basicProperties.sExpName) LIKE :toFind OR LOWER(e.expSetup.basicProperties.sSummary) LIKE :toFind");
        query.setParameter("toFind", toFind);
        return query.getResultList();
	}
	
}
