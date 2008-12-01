/**
 * 
 */
package eu.planets_project.ifr.core.security.api.services;

import java.util.List;

import eu.planets_project.ifr.core.security.api.model.User;


/**
 * <h2>The Planets User Manager</h2>
 * 
 * <p>
 * This interface specifies how to interact with the User database.  
 * A UserManagerImpl is created by the Planets IF and exports this interface over EJB.
 * A User can use this API to modify their own data, and to look up some basic data about other Users.
 * A User with the 'admin' role can view and modify all user data.
 * </p>
 * 
 * <p>
 * The user accounts were originally handled by the Administration component: {@link eu.planets_project.ifr.core.admin.api.service.UserManager}.
 * However, the code was not suitable for accessing from other components over EJB.
 * Therefore, the persistence code was re-written using the JPA, but accessing exactly the same tables as used by the Admin component.
 * </p>
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface UserManager {

    /**
     * Gets users information based on user id.
     * @param userId the user's id
     * @return user populated user object
     */
    public User getUser(Long userId);

    /**
     * Gets users information based on login name.
     * 
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws UserNotFoundException when user with username cannot be retrieved from the database
     */
    public User getUserByUsername(String username) throws UserNotFoundException;
    
    /**
     * Checks for username availability.
     * @param username
     * @return true if the name is not taken.
     */
    public boolean isUsernameAvailable(String username );
    
    /**
     * Gets a list of all users.
     *
     * @return List populated list of all the users
     */
    public List<User> getUsers();
    
    /**
     * Gets a list of all usernames.
     *
     * @return List populated list of all the usernamess
     */
    public List<String> getUsernames();
    
    /**
     * Creates a new User, thowing an Exception if this fails:
     * @param user
     * @throws UserNotValidException
     */
    public void addUser(User user) throws UserNotValidException;
    
    /**
     * Saves a user's information
     * @param user the object to be saved
     */
    public void saveUser(User user);

    /**
     * Removes a user from the database by id.
     * @param userId the user's id
     */
    public void removeUser(Long userId);
 
    /**
     * This is a simple wrapper that makes is simple to send a
     * message to a user.  
     * Currently just sends an email - could queue messages via 
     * the Notification API later on.
     * If the username is not know/valid, no message will be sent.
     * 
     * @param username The username to send to. 
     * @param subject The subject for the message.
     * @param body The message body.
     */
    public void sendUserMessage(String username, String subject, String body);
    
    /**
     * Get a list of all the roles.
     * @return All of the visible roles, as a String array.
     */
    public String[] listRoles();
    
    /**
     * Helper to list all the users in a particular role.
     * @param role
     * @return An array of Users matching that role.
     */
    public List<User> listUsersInRole( String role );

    /**
     * Assign a role to a user
     * @param user The user to bless.
     * @param role The role the user should be blessed with.
     */
    public void assignRoleToUser( User user, String role );
    
    /**
     * Un-assign a role from a user.
     * @param user The user to be rebuffed
     * @param role The role of which the user should be firmly denied.
     */
    public void revokeRoleFromUser( User user, String role );
    
    /**
     * Exception thrown when a username cannot be found.
     * @author AnJackson
     *
     */
    public class UserNotFoundException extends Exception {
        static final long serialVersionUID = 3243243223432432l;
    }
    
    /**
     * Exception thrown when a user cannot be stored as it is not valid.
     * @author AnJackson
     *
     */
    public class UserNotValidException extends Exception {
        static final long serialVersionUID = 9243232234324432l;
    }
    
}
