package eu.planets_project.tb.impl.persistency;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

@Stateless
public class ExperimentPersistencyImpl implements ExperimentPersistencyRemote {
	
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

	@SuppressWarnings("unchecked")
    public List<Experiment> queryAllExperiments() {
        log.info("Looking up all experiments.");
        // FIXME This is not needed: Exception e = new Exception("All Experiments.");
        // e.printStackTrace();
		Query query = manager.createQuery("from ExperimentImpl");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
    public boolean queryIsExperimentNameUnique(String expName) {
	    log.info("Checking uniqueness of exp. name: " + expName );
		Query query = manager.createQuery("SELECT sExpName FROM BasicPropertiesImpl WHERE LOWER(sExpName)=LOWER(:expname)");
		query.setParameter("expname", expName);
		List<String> results = (List<String>) query.getResultList();
		if(results.size()==0)
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
    public List<Experiment> searchAllExperiments( String toFind ) {
        // Not case sensitive, wildcarded:
        log.info("Searching for experiments that match: " + toFind );
        // TODO Augment this with more fields.
        Query query = manager.createQuery("FROM ExperimentImpl AS e WHERE LOWER(e.expSetup.basicProperties.sExpName) LIKE :toFindLike OR LOWER(e.expSetup.basicProperties.sSummary) LIKE :toFindLike");
        // This should work, but does not.  It is unclear why. the type of the toFind should not be String.
        // OR :toFind MEMBER OF e.expSetup.basicProperties.vInvolvedUsers");
        //query.setParameter("toFind", toFind );
        query.setParameter("toFindLike", "%"+toFind.toLowerCase()+"%");
        return query.getResultList();
	}
	
    /**
     * A Factory method to build a reference to this interface.
     * @return
     */
	public static ExperimentPersistencyRemote getInstance(){
		try{
			Context jndiContext = getInitialContext();
			ExperimentPersistencyRemote dao_r = (ExperimentPersistencyRemote) PortableRemoteObject.narrow(
					jndiContext.lookup("testbed/ExperimentPersistencyImpl/remote"), ExperimentPersistencyRemote.class);
			return dao_r;
		}catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Failure in getting PortableRemoteObject: "+e.toString());
			return null;
		}
	}
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}
	
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote#getPagedExperiments(int, int, java.lang.String, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<Experiment> getPagedExperiments(int firstRow, int numberOfRows,
            String sortField, boolean descending) {
        log.info("Searching for experiments that match: " +firstRow+","+numberOfRows+" : "+sortField+" : "+descending );
        // Execute the query:
        Query query = manager.createQuery("FROM ExperimentImpl AS e " + createOrderBy(sortField, descending));
        query.setFirstResult(firstRow);
        query.setMaxResults(numberOfRows);
        List<Experiment> resultList = query.getResultList();
        log.info("Search complete.");
        return resultList;
        
        /*
        String propertyName;
        if( "experimenter".equals(sortField) ) {
//            this.findExperiment(1).getExperimentSetup().getBasicProperties().getExperimenter();
            propertyName = "expSetup.basicProperties.sExpName";
        } else {
//          this.findExperiment(1).getExperimentSetup().getBasicProperties().getExperimentName();
            propertyName = "expSetup.basicProperties.sExpName";
        }
        Order order;
        if( descending ) {
        //    query.setParameter("deasc", "descending");
            order = Order.asc(propertyName);
        } else {
        //    query.setParameter("deasc", "ascending");
            order = Order.desc(propertyName);
        }
        
        Session hibernateSession = null;
        if (manager.getDelegate() instanceof org.hibernate.ejb.HibernateEntityManager) {
            hibernateSession = ((org.hibernate.ejb.HibernateEntityManager) manager
                    .getDelegate()).getSession();
        } else {
            hibernateSession = (org.hibernate.Session) manager.getDelegate();
        }

        return hibernateSession.createCriteria(ExperimentImpl.class)
        .addOrder( order )
        .setFirstResult(firstRow)
        .setMaxResults(numberOfRows)
        .list();
        */
    }
    
    private String createOrderBy( String sortField, boolean descending ) {
        String orderby, deasc;
        if( "experimenter".equals(sortField) ) {
            orderby = "e.expSetup.basicProperties.sExperimenterID";
        } else if( "type".equals(sortField) ) {
            orderby = "e.expSetup.basicProperties.sExperimentApproach";
        } else if( "startDate".equals(sortField) ) {
            orderby = "e.startDate";
        } else if( "execDate".equals(sortField) ) {
            orderby = "e.executable.execEndDate";
//        } else if( "currentStage".equals(sortField) ) {  // FIXME Current stage NOT STORED IN DATABASE!
//            orderby = "e.???";
        } else {
            orderby = "e.expSetup.basicProperties.sExpName";
        }
        if( descending ) {
          deasc = "DESC";
        } else {
          deasc = "ASC";
        }
        return "ORDER BY "+orderby+" "+deasc;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote#getNumberOfExperiments()
     */
    public int getNumberOfExperiments() {
        log.info("Counting the experiments...");
        int ne = ( (Long) manager.createQuery("SELECT COUNT(e.id) FROM ExperimentImpl AS e").getSingleResult() ).intValue();
        log.info("There are "+ne+" experiments.");
        return ne;
    }
	
    
    @SuppressWarnings("unchecked")
    public List<ServiceRecordImpl> getServiceRecords() {
        List<ServiceRecordImpl> list = new ArrayList<ServiceRecordImpl>();
//        Query query = manager.createQuery("SELECT s.serviceRecord FROM ExperimentImpl AS e INNER JOIN e.executable.executionRecords AS x INNER JOIN x.runs AS r INNER JOIN r.stages AS s");
        List<Experiment> experiments = this.queryAllExperiments();
        for( Experiment exp : experiments ) {
            if( exp.getExperimentExecutable() != null && exp.getExperimentExecutable().getBatchExecutionRecords() != null ) {
                List<BatchExecutionRecordImpl> batches = exp.getExperimentExecutable().getBatchExecutionRecords();
                for( BatchExecutionRecordImpl batch : batches ) {
                    if( batch != null && batch.getRuns() != null ) {
                        for( ExecutionRecordImpl run : batch.getRuns() ) {
                            for( ExecutionStageRecordImpl stage: run.getStages() ) {
                                if( stage.getServiceRecord() != null ) list.add( stage.getServiceRecord() );
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    
    // FIXME This does not work.
    
    //@SuppressWarnings("unchecked")
    public ServiceRecordImpl findServiceRecordByHashcode( String serviceHash ) {
        Query query = manager.createQuery("SELECT s.serviceRecord FROM ExperimentImpl AS exp LEFT JOIN exp.executable.executionRecords AS rec LEFT JOIN rec.runs AS run LEFT JOIN run.stages AS stage WHERE stage.serviceHash = :hash");
        query.setParameter("hash", serviceHash);
        List<ServiceRecordImpl> srs = query.getResultList();
        if( srs == null ) return null;
        if( srs.size() == 0 ) return null;
        return srs.get(0);
        /*
        */
        /*
        for( ServiceRecordImpl record : this.getServiceRecords() ) {
            if( serviceHash.equals( record.getServiceHash()) ) return record;
        }
        return null;
        */
    }
    

}
