/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;

/**
 * @author alindley
 *
 */
public class TestbedManagerImpl 
	implements eu.planets_project.tb.api.TestbedManager, java.io.Serializable{

	
	private long lTestbedManagerID;
	private static TestbedManagerImpl instance;
	private HashMap<Long,Experiment> hmAllExperiments;
	
	
	/**
	 * This Class implements the Java singleton pattern and therefore the constructor should be private
	 * However due to requirements in the front-end Bean it is set public at the moment.
	 */
	public TestbedManagerImpl(){
		hmAllExperiments = this.queryAllExperiments();
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
	}
	
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized TestbedManagerImpl getInstance(){
		if (instance == null){
			instance = new TestbedManagerImpl();
		}
		return instance;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentIDs()
	 */
	public Set<Long> getAllExperimentIDs() {
		return this.hmAllExperiments.keySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperiments()
	 */
	public Collection<Experiment> getAllExperiments() {
		return this.hmAllExperiments.values();
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
		return this.hmAllExperiments.get(expID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#createNewExperiment()
	 */
	public Experiment createNewExperiment() {
		ExperimentImpl exp = new ExperimentImpl();
	  //Should this be added in a transaction?
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		long lExpID = dao_r.persistExperiment(exp);
		
		//should now already contain a container injected EntityID;
		exp = (ExperimentImpl)dao_r.findExperiment(lExpID);
		
		//now register experimentRefID in Phases
		this.setExperimentRefInPhase(exp);
		
		this.hmAllExperiments.put(exp.getEntityID(), exp);
		
	  //End Transaction
		return exp;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#registerExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public long registerExperiment(Experiment experimentBean)
	{	
	  //Should this be added in a transaction?
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		//check if really a detached entity
		if(experimentBean.getEntityID()!=-1){
			//it's not possible to register a previously registered experiment
			//as the container cannot inject a valid ID
			return -1;
		}else{
			long lExpID = dao_r.persistExperiment(experimentBean);
			ExperimentImpl exp = (ExperimentImpl)dao_r.findExperiment(lExpID);
			
			//now register experimentRefID in Phases
			this.setExperimentRefInPhase(exp);
			
			this.hmAllExperiments.put(exp.getEntityID(), exp);
			
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
		((ExperimentSetupImpl)exp.getExperimentSetup()).setExpeirmentRefID(exp.getEntityID());
		((ExperimentApprovalImpl)exp.getExperimentApproval()).setExpeirmentRefID(exp.getEntityID());
		((ExperimentExecutionImpl)exp.getExperimentExecution()).setExpeirmentRefID(exp.getEntityID());
		((ExperimentEvaluationImpl)exp.getExperimentEvaluation()).setExpeirmentRefID(exp.getEntityID());
		
		//As the Experiment's attributes have been modified we must call update
		this.updateExperiment(exp);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#updateExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public void updateExperiment(Experiment experimentBean) {
		boolean bContains = this.hmAllExperiments.containsKey(experimentBean.getEntityID());
		if(bContains){
		  //Should this be added in a transaction?
			ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
			dao_r.updateExperiment(experimentBean);
			ExperimentImpl exp = (ExperimentImpl)dao_r.findExperiment(experimentBean.getEntityID());
			this.hmAllExperiments.put(exp.getEntityID(), exp);
		  //End Transaction
		}
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#removeExperiment(long)
	 */
	public void removeExperiment(long expID) {
		boolean bContains = this.hmAllExperiments.containsKey(expID);
		if(bContains){
		  //Should this be added in a transaction?
			ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
			dao_r.deleteExperiment(expID);
			this.hmAllExperiments.remove(expID);
		  //End Transaction
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#containsExperiment(long)
	 */
	public boolean containsExperiment(long expID) {
		return this.hmAllExperiments.containsKey(expID);
	}
	
	private ExperimentPersistencyRemote createPersistencyHandler(){
		try{
			Context jndiContext = getInitialContext();
			ExperimentPersistencyRemote dao_r = (ExperimentPersistencyRemote) PortableRemoteObject.narrow(
					jndiContext.lookup("ExperimentPersistencyImpl/remote"), ExperimentPersistencyRemote.class);
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
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsOfType(int)
	 */
	public Collection<Experiment> getAllExperimentsOfType(String sExperimentTypeID) {
		Vector<Experiment> vRet = new Vector<Experiment>();
		Iterator<Long> itExpIDs = this.hmAllExperiments.keySet().iterator();
		while(itExpIDs.hasNext()){
			long helper = itExpIDs.next();
			Experiment exp = this.hmAllExperiments.get(helper);
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
		Iterator<Long> itExpIDs = this.hmAllExperiments.keySet().iterator();

		while(itExpIDs.hasNext()){
			Experiment exp = this.hmAllExperiments.get(itExpIDs.next());
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
	
	/**
	 * This private helper method is used to query the EntityManager (via the ExperimentPersistency) interface
	 * to retrieve all Experiments in the data store and builds up the HashMap<ExpID,Experiment> which is used due
	 * to performance reasons within this class.
	 * @return
	 */
	private HashMap<Long,Experiment> queryAllExperiments(){
		HashMap<Long,Experiment> hmRet = new HashMap<Long,Experiment>();
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
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
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		return dao_r.queryIsExperimentNameUnique(expName);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#isRegistered(long)
	 */
	public boolean isRegistered(long expID) {
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		if(dao_r.findExperiment(expID)!=null)
			return true;
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#isRegistered(eu.planets_project.tb.api.model.Experiment)
	 */
	public boolean isRegistered(Experiment experiment) {
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		if(dao_r.findExperiment(experiment.getEntityID())!=null)
			return true;
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getNumberOfExperiments()
	 */
	public int getNumberOfExperiments() {
		return this.hmAllExperiments.size();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getNumberOfExperiments(java.lang.String, boolean)
	 */
	public int getNumberOfExperiments(String userID, boolean bWhereExperimenter) {
		int iCount = 0;
		Iterator<Long> itKeys = this.hmAllExperiments.keySet().iterator();
		while(itKeys.hasNext()){
			Experiment exp = this.hmAllExperiments.get(itKeys.next());
			
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
	 * @see eu.planets_project.tb.api.TestbedManager#getWorkflowHandler()
	 */
	public WorkflowHandler getWorkflowHandler() {
		return WorkflowHandlerImpl.getInstance();
	}


}
