/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.impl.CommentManager;
import eu.planets_project.tb.api.UserManager;
import eu.planets_project.tb.impl.model.Experiment;

/**
 * @author alindley
 *
 */
//@Entity
public class TestbedManager implements eu.planets_project.tb.api.TestbedManager{

	//@Id
	//@GeneratedValue
	private long lTestbedManagerID;
	private static TestbedManager instance;
	private HashMap<Long,eu.planets_project.tb.api.model.Experiment> hmAllExperiments;
	
	private TestbedManager(){
		hmAllExperiments = new HashMap<Long,eu.planets_project.tb.api.model.Experiment>();
	}
	
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized TestbedManager getInstance(){
		if (instance == null){
			instance = new TestbedManager();
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
		CommentManager commentManager = eu.planets_project.tb.impl.CommentManager.getInstance();
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
		Experiment exp = new Experiment();
		this.hmAllExperiments.put(exp.getExperimentID(), exp);
		return exp;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#getUserManager()
	 */
	public eu.planets_project.tb.api.UserManager getUserManager() {
		//get Singleton: UserManager
		UserManager userManager = eu.planets_project.tb.impl.UserManager.getInstance();
		return userManager;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#registerExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public void registerExperiment(eu.planets_project.tb.api.model.Experiment experimentBean) {
		this.hmAllExperiments.put(experimentBean.getExperimentID(), experimentBean);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#removeExperiment(long)
	 */
	public void removeExperiment(long expID) {
		this.hmAllExperiments.remove(expID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.TestbedManager#containsExperiment(long)
	 */
	public boolean containsExperiment(long expID) {
		return this.hmAllExperiments.containsKey(expID);
	}

}
