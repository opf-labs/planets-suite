/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;

/**
 * @author alindley
 *
 */
@Entity
public class TestbedManagerImpl 
	implements eu.planets_project.tb.api.TestbedManager, java.io.Serializable{

	@Id
	@GeneratedValue
	private long lTestbedManagerID;
	private static TestbedManagerImpl instance;
	private HashMap<Long,eu.planets_project.tb.api.model.Experiment> hmAllExperiments;
	
	//PortableRemoteObject and Context required for EJB persistence
	//private Context jndiContext;
	//private ExperimentPersistencyRemote dao_r;
	

	public long getID(){
		return this.lTestbedManagerID;
	}
	
	public void setID(long lID){
		this.lTestbedManagerID = lID;
	}
	
	
	/**
	 * This Class implements the Java singleton pattern and therefore the constructor should be private
	 * However due to requirements in the front-end Bean it is set public at the moment.
	 */
	public TestbedManagerImpl(){
		hmAllExperiments = new HashMap<Long,eu.planets_project.tb.api.model.Experiment>();
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
	public Collection<eu.planets_project.tb.api.model.Experiment> getAllExperiments() {
		return this.hmAllExperiments.values();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getCommentManager()
	 */
	public eu.planets_project.tb.api.CommentManager getCommentManager() {
		//get Singleton: TestbedManager
		CommentManagerImpl commentManager = eu.planets_project.tb.impl.CommentManagerImpl.getInstance();
		return commentManager;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getExperiment(long)
	 */
	public eu.planets_project.tb.api.model.Experiment getExperiment(long expID) {
		return this.hmAllExperiments.get(expID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#createNewExperiment()
	 */
	public eu.planets_project.tb.api.model.Experiment createNewExperiment() {
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
		System.out.println("ExpID: manager: "+lExpID);
		ExperimentImpl exp = (ExperimentImpl)dao_r.findExperiment(lExpID);
		System.out.println("lookup: manager: "+exp.getEntityID());
		this.hmAllExperiments.put(exp.getEntityID(), exp);
		System.out.println("hmContains? "+this.hmAllExperiments.containsKey(exp.getEntityID()));
	    
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
					jndiContext.lookup("ExperimentPersistency/remote"), ExperimentPersistencyRemote.class);
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




}
