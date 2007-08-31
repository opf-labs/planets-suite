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
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;

/**
 * @author alindley
 *
 */
public class TestbedManagerImpl 
	implements eu.planets_project.tb.api.TestbedManager, java.io.Serializable{

	
	private long lTestbedManagerID;
	private static TestbedManagerImpl instance;
	private HashMap<Long,eu.planets_project.tb.api.model.Experiment> hmAllExperiments;
	
	//PortableRemoteObject and Context required for EJB persistence
	//private Context jndiContext;
	//private ExperimentPersistencyRemote dao_r;
	
	
	/**
	 * This Class implements the Java singleton pattern and therefore the constructor should be private
	 * However due to requirements in the front-end Bean it is set public at the moment.
	 */
	public TestbedManagerImpl(){
		//hmAllExperiments = new HashMap<Long,eu.planets_project.tb.api.model.Experiment>();
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
		CommentManagerImpl commentManager = CommentManagerImpl.getInstance();
		return commentManager;
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
		long lExpID = dao_r.persistExperiment(experimentBean);
		ExperimentImpl exp = (ExperimentImpl)dao_r.findExperiment(lExpID);
		this.hmAllExperiments.put(exp.getEntityID(), exp);
	    
		return exp.getEntityID();
	   //End Transaction
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
	public Collection<Experiment> getAllExperimentsOfType(int typeID) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getAllExperimentsOfUsers(java.lang.String)
	 */
	public Collection<Experiment> getAllExperimentsOfUsers(String userID) {
		// TODO Auto-generated method stub
		return null;
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

	public boolean isExperimentNameUnique(String expName) {
		ExperimentPersistencyRemote dao_r = this.createPersistencyHandler();
		return dao_r.queryIsExperimentNameUnique(expName);
	}

}
