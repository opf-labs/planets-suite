/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.HashMap;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.User;

/**
 * @author alindley
 *
 */
//@Entity
public class UserManager implements eu.planets_project.tb.api.UserManager {
	
	//@Id
	//@GeneratedValue
	private long UserManagerID;
	private HashMap<Long,User> hmUserIDMapping;

	private static UserManager instance;
	
	private UserManager(){
		hmUserIDMapping = new HashMap<Long,User>();
	}
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized UserManager getInstance(){
		if (instance == null){
			instance = new UserManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getAllUserIDs()
	 */
	public Vector<Long> getAllUserIDs() {
		Vector<Long> vRet = new Vector<Long>();
		vRet.addAll(hmUserIDMapping.keySet());
		return vRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getAllUsers()
	 */
	public Vector<User> getAllUsers() {
		Vector<User> vRet = new Vector<User>();
		vRet.addAll(this.hmUserIDMapping.values());
		return vRet;
	}

	/* (non-Javadoc)
	 * A user retrieved through this method is already automatically registered.
	 * @see eu.planets_project.tb.api.UserManager#getNewUserBean(Vector<Integer>)
	 */
	public User getNewUserBean(Vector<Integer> vRoles) {
		eu.planets_project.tb.impl.model.User u1 = new eu.planets_project.tb.impl.model.User(vRoles);
		//TODO: A problem could be that the user has not been assigned a ID through EJB
		this.hmUserIDMapping.put(u1.getUserID(), u1);
		return u1;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getUser(long)
	 */
	public User getUser(long userID) {
		boolean bContains = this.hmUserIDMapping.containsKey(userID);
		if(bContains){
			return this.hmUserIDMapping.get(userID);
		}else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#registerUser(eu.planets_project.tb.api.model.User)
	 */
	public void registerUser(User userBean) {
		boolean bContains = this.hmUserIDMapping.containsKey(userBean.getUserID());
		if(bContains)
			this.hmUserIDMapping.remove(userBean.getUserID());
		this.hmUserIDMapping.put(userBean.getUserID(),userBean);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#removeUser(long)
	 */
	public void removeUser(long userID) {
		this.hmUserIDMapping.remove(userID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#updateUser(long)
	 */
	public void updateUser(User user) {
		boolean bContains = this.hmUserIDMapping.containsKey(user.getUserID());
		if(bContains)
			this.hmUserIDMapping.remove(user.getUserID());
		this.hmUserIDMapping.put(user.getUserID(),user);
	}

}
