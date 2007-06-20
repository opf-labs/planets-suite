package eu.planets_project.TB.api.interfaces;

import eu.planets_project.TB.api.interfaces.model.User;

/**
 * This component is responsible for managing all aspects of user data, rights, etc.
 * @author alindley
 *
 */
public interface UserManager {
	
	public void registerUser(User userBean);
	public User getUser(long lUserID);
	public void removeUser(long lUserID);
	public User[] getAllUsers();
	public long[] getAllUserIDs();
	
	public User getNewUserBean();

}
