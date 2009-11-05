/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.faces.context.FacesContext;


import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.backing.exp.ExpBeanReqManager;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;
import eu.planets_project.tb.impl.system.BackendProperties;
import eu.planets_project.tb.impl.system.ServiceExecutionHandlerImpl;
import eu.planets_project.tb.impl.system.batch.backends.tbown.TestbedBatchProcessor;
import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.system.ServiceExecutionHandler;

/**
 * @author alindley
 *
 */
public class TestbedManagerImpl 
	implements eu.planets_project.tb.api.TestbedManager, java.io.Serializable{

    private static Log log = PlanetsLogger.getLogger(TestbedManagerImpl.class);
	
	private long lTestbedManagerID;
	private static TestbedManagerImpl instance;
	//e.g. used within the serviceTemplate importer and exporter
	ExperimentPersistencyRemote edao;
	
	// The version number of the Testbed.  Can be overridden in BackendResources.properties.
	private String tbVersion = "1.0.0";
	
    // Look for the batch system... 
    TestbedBatchProcessor tbp = (TestbedBatchProcessor)JSFUtil.getManagedObject("TestbedBatchProcessor");
	
	/**
	 * This Class implements the Java singleton pattern and therefore the constructor should be private
	 * However due to requirements of JSF managed beans it is set public (at the moment).
	 */
	public TestbedManagerImpl(){
	    log.info("Constructing...");
	    // Look up the experiment persistency handler:
	    edao = ExperimentPersistencyImpl.getInstance();
		// Update the version number, if set in the properties:
        BackendProperties bp = new BackendProperties();
		String version = bp.getTestbedVersion();
		if( version != null && ! "".equals(version)) tbVersion = version;
        log.info("Constructed.");
	}
	
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized TestbedManagerImpl getInstance(){
		if (instance == null){
			instance = (TestbedManagerImpl) JSFUtil.getManagedObject("TestbedManager");
		}
		return instance;
	}
	
	
	/**
	 * Use this method in the backend to retrieve the instance of this class.
	 * @param bUpdate indicates if the TB manager's content shall be updated (true) from the  persistency unit again
	 * @return
	 */
	public static synchronized TestbedManagerImpl getInstance(boolean bUpdate){
		if (!bUpdate){
			return getInstance();
		}else{
			instance = (TestbedManagerImpl) JSFUtil.getManagedObject("TestbedManager");
			return instance;
		}
	}
	
	
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.TestbedManager#getExperimentPersistencyRemote()
     */
    public ExperimentPersistencyRemote getExperimentPersistencyRemote() {
        return edao;
    }


    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentIDs()
	 */
	public Set<Long> getAllExperimentIDs() {
		return this.queryAllExperiments().keySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperiments()
	 */
	public Collection<Experiment> getAllExperiments() {
		return this.queryAllExperiments().values();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getCommentManager()
	 */
	public CommentManager getCommentManagerInstance() {
		//get Singleton: TestbedManager
		return CommentManagerImpl.getInstance();
	}
	
	public AdminManager getAdminManagerInstance() {
		//get Singleton: AdminManager
		return AdminManagerImpl.getInstance();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getExperiment(long)
	 */
	public Experiment getExperiment(long expID) {
		return this.edao.findExperiment(expID);

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#createNewExperiment()
	 */
	public Experiment createNewExperiment() {
		ExperimentImpl exp = new ExperimentImpl();
	  //Should this be added in a transaction?
		long lExpID = edao.persistExperiment(exp);
		
		//should now already contain a container injected EntityID;
		exp = (ExperimentImpl)edao.findExperiment(lExpID);

		//now register experimentRefID in Phases
		this.setExperimentRefInPhase(exp);

	  //End Transaction
		return exp;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#registerExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public long registerExperiment(Experiment experimentBean)
	{	
	  //Should this be added in a transaction?
		//check if really a detached entity
		if(experimentBean.getEntityID()!=-1){
			//it's not possible to register a previously registered experiment
			//as the container cannot inject a valid ID
			return -1;
		}else{
			long lExpID = edao.persistExperiment(experimentBean);
			ExperimentImpl exp = (ExperimentImpl)edao.findExperiment(lExpID);
			
			//now register experimentRefID in Phases
			this.setExperimentRefInPhase(exp);
			
			//finally return the entityID
			return exp.getEntityID();
		}
	   //End Transaction
	}
	
	/**
	 * Precondition: Experiment must already be known by the EntityManager, i.e. have an EntityID assigned
	 * @param exp
	 */
	private void setExperimentRefInPhase(Experiment exp){
		//set the reference pointers for the stages:
		((ExperimentSetupImpl)exp.getExperimentSetup()).setExperimentRefID(exp.getEntityID());
		((ExperimentApprovalImpl)exp.getExperimentApproval()).setExperimentRefID(exp.getEntityID());
		((ExperimentExecutionImpl)exp.getExperimentExecution()).setExperimentRefID(exp.getEntityID());
		((ExperimentEvaluationImpl)exp.getExperimentEvaluation()).setExperimentRefID(exp.getEntityID());
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#updateExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public void updateExperiment(Experiment experiment) {
	    log.debug("Updating experiment.");
		if( edao.findExperiment(experiment.getEntityID()) != null ){
		  //Should this be added in a transaction?
		    edao.updateExperiment(experiment);
			ExperimentImpl exp = (ExperimentImpl)edao.findExperiment(experiment.getEntityID());
		    // Also update the Experiment backing bean to reflect the changes:
            ExpBeanReqManager.putExperimentIntoSessionExperimentBean( exp );
            
          //End Transaction
		} else {
		    log.error("updateExperiment Failed: No Entity ID for experiment: "+experiment.getExperimentSetup().getBasicProperties().getExperimentName());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#removeExperiment(long)
	 */
	public void removeExperiment(long expID) {
	    log.info("Removing experiment "+expID);
        if( edao.findExperiment(expID) != null ){
			edao.deleteExperiment(expID);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#containsExperiment(long)
	 */
	public boolean containsExperiment(long expID) {
		return ( edao.findExperiment(expID) != null );
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsOfType(int)
	 */
	public Collection<Experiment> getAllExperimentsOfType(String sExperimentTypeID) {
		Vector<Experiment> vRet = new Vector<Experiment>();
		Collection<Experiment> itExpIDs = this.getAllExperiments();
		for(Experiment exp : itExpIDs ){
			if (exp.getExperimentSetup().getExperimentTypeID().equals(sExperimentTypeID)){
				vRet.add(exp);
			}
		}
		return vRet;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsOfUsers(java.lang.String)
	 */
	public Collection<Experiment> getAllExperimentsOfUsers(String userID, boolean bIsExperimenter) {
		Vector<Experiment> vRet = new Vector<Experiment>();
        Collection<Experiment> itExpIDs = this.getAllExperiments();
        
        for(Experiment exp : itExpIDs ){
			//if type involved user (bIsExperimenter=false) is requested
			if(!bIsExperimenter){
				List<String> involvedUsers = exp.getExperimentSetup().getBasicProperties().getInvolvedUserIds();
				if (involvedUsers.contains(userID)){
					vRet.add(exp);
				}
			}else{
			//if type experimenter user (bIsExperimenter=true) is requested
				String sExperimenterID = exp.getExperimentSetup().getBasicProperties().getExperimenter();
				if (sExperimenterID.equals(userID)){
					vRet.add(exp);
				}
			}
		}
		return vRet;
	}
	
	
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsAtPhase(int)
     */
    public Collection<Experiment> getAllExperimentsAtPhase(int phaseID) {
        Vector<Experiment> vRet = new Vector<Experiment>();
        Collection<Experiment> itExpIDs = this.getAllExperiments();
        for(Experiment exp : itExpIDs ){
            if (exp.getCurrentPhasePointer() == phaseID){
                vRet.add(exp);
            }
        }
        return vRet;
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsAwaitingApproval()
     */
    public Collection<Experiment> getAllExperimentsAwaitingApproval() {
        Vector<Experiment> vRet = new Vector<Experiment>();
        Collection<Experiment> itExpIDs = this.getAllExperiments();
        for(Experiment exp : itExpIDs ){
            if ( exp.isAwaitingApproval() ){
                vRet.add(exp);
            }
        }
        return vRet;
    }


    /**
	 * This private helper method is used to query the EntityManager (via the ExperimentPersistency) interface
	 * to retrieve all Experiments in the data store and builds up the HashMap<ExpID,Experiment> which is used due
	 * to performance reasons within this class.
	 * @return
	 */
	private HashMap<Long,Experiment> queryAllExperiments(){
		HashMap<Long,Experiment> hmRet = new HashMap<Long,Experiment>();
		ExperimentPersistencyRemote dao_r = ExperimentPersistencyImpl.getInstance();
		List<Experiment> list = dao_r.queryAllExperiments();
		Iterator<Experiment> itList = list.iterator();
		while(itList.hasNext()){
			Experiment exp = itList.next();
			hmRet.put(exp.getEntityID(), exp);
		}
		
		return hmRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#isExperimentNameUnique(java.lang.String)
	 */
	public boolean isExperimentNameUnique(String expName) {
		ExperimentPersistencyRemote dao_r = ExperimentPersistencyImpl.getInstance();
		return dao_r.queryIsExperimentNameUnique(expName);
	}


	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.TestbedManager#searchAllExperiments(java.lang.String)
     */
    public List<Experiment> searchAllExperiments(String toFind) {
        ExperimentPersistencyRemote dao_r = ExperimentPersistencyImpl.getInstance();
        return dao_r.searchAllExperiments(toFind);
    }


    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#isRegistered(long)
	 */
	public boolean isRegistered(long expID) {
		ExperimentPersistencyRemote dao_r = ExperimentPersistencyImpl.getInstance();
		if(dao_r.findExperiment(expID)!=null)
			return true;
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#isRegistered(eu.planets_project.tb.api.model.Experiment)
	 */
	public boolean isRegistered(Experiment experiment) {
		ExperimentPersistencyRemote dao_r = ExperimentPersistencyImpl.getInstance();
		if(dao_r.findExperiment(experiment.getEntityID())!=null)
			return true;
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getNumberOfExperiments()
	 */
	public int getNumberOfExperiments() {
		return this.edao.getNumberOfExperiments();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getNumberOfExperiments(java.lang.String, boolean)
	 */
	public int getNumberOfExperiments(String userID, boolean bWhereExperimenter) {
		int iCount = 0;
        Collection<Experiment> itExpIDs = this.getAllExperiments();
        for(Experiment exp : itExpIDs ){
			
			//a)check bWhereExperimenter=true=Experimenter
			if(bWhereExperimenter){
				if(exp.getExperimentSetup().getBasicProperties().
						getExperimenter().equals(userID)){
					iCount++;
				}
			}
			//b)check bWhereExperimenter=true=Experimenter
			else{
				if(exp.getExperimentSetup().getBasicProperties().
						getInvolvedUserIds().contains(userID)){
					iCount++;
				}
			}
		}
		
		return iCount;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getBenchmarkGoalHandler()
	 */
	public BenchmarkGoalsHandler getBenchmarkGoalHandler() {
		return BenchmarkGoalsHandlerImpl.getInstance();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getServiceTemplateRegistry()
	 */
	public ServiceTemplateRegistry getServiceTemplateRegistry() {
		return ServiceTemplateRegistryImpl.getInstance();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#executeExperiment()
	 */
	public void executeExperiment(Experiment exp){
		ServiceExecutionHandler invoHandler = new ServiceExecutionHandlerImpl();
		invoHandler.executeExperiment(exp);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getCurrentTBVersionNumber()
	 */
	public String getCurrentTBVersionNumber(){
		return this.tbVersion;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#executeAutoEvaluationWf(eu.planets_project.tb.api.model.Experiment)
	 */
	public void executeAutoEvaluationWf(Experiment exp) {
		ServiceExecutionHandler invoHandler = new ServiceExecutionHandlerImpl();
		invoHandler.executeAutoEvalServices(exp);
	}


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.TestbedManager#getPagedExperiments()
     */
    public List<Experiment> getPagedExperiments( int firstRow, int numberOfRows, String sortField, boolean descending ) {
        return edao.getPagedExperiments(firstRow, numberOfRows, sortField, descending);
    }
    
    /** FIXME Add a deep copy... */
    public static long copyToNewExperiment( TestbedManager tbm, ExperimentImpl src ) {
        Experiment ne = src; // FIXME Serialise...???
        ne.getEntityID();
        ne.getExperimentApproval().getEntityID();
        ne.getExperimentEvaluation().getEntityID();
        ne.getExperimentExecutable();
        ne.getExperimentExecutable();
        ne.getExperimentExecution().getEntityID();
        ne.getExperimentSetup().getEntityID();
        ne.getExperimentSetup().getBasicProperties();
        long eid = tbm.registerExperiment(ne);
        return eid;
    }

}
