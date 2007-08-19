package eu.planets_project.tb.api;

import java.util.Vector;

import eu.planets_project.tb.api.model.User;

/**
 * This component is responsible for managing all aspects of user data, rights, etc.
 * @author alindley
 *
 */
public interface UserManager {
	
	public void registerUser(User userBean);
	public User getUser(long lUserID);
	public void removeUser(long lUserID);
	public void updateUser(User user);
	public Vector<User> getAllUsers();
	public Vector<Long> getAllUserIDs();
	
	public User getNewUserBean(Vector<Integer> vRoles);

}
