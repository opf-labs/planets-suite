/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.HashMap;

import eu.planets_project.tb.api.model.User;

/**
 * @author alindley
 *
 */
public class UserManager implements eu.planets_project.tb.api.UserManager {
	
	private static UserManager instance;
	
	private UserManager(){
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
	public long[] getAllUserIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getAllUsers()
	 */
	public User[] getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getNewUserBean()
	 */
	public User getNewUserBean() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#getUser(long)
	 */
	public User getUser(long userID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#registerUser(eu.planets_project.tb.api.model.User)
	 */
	public void registerUser(User userBean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.UserManager#removeUser(long)
	 */
	public void removeUser(long userID) {
		// TODO Auto-generated method stub

	}

}
