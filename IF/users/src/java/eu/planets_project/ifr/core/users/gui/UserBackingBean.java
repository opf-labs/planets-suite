package eu.planets_project.ifr.core.users.gui;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.security.api.services.RoleManager;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.model.Role;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.RoleManager.RoleNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;
import eu.planets_project.ifr.core.security.impl.model.AddressImpl;
import eu.planets_project.ifr.core.security.impl.model.UserImpl;
import eu.planets_project.ifr.core.security.impl.services.RoleManagerImpl;
import eu.planets_project.ifr.core.security.impl.services.UserManagerImpl;

/**
 * This is the controller class for the rewritten User Administration JSF web application.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class UserBackingBean {
	private static Log log = LogFactory.getLog(UserBackingBean.class);

	// The name of the currently logged in user, taken from session
	private String username;
	// The user profile to edit
	private User user = null;
	// A list of all if users used by the admin list page
	private List<User> allUsers = null;
	private String originalPassword = null;
	private String userPassword = null;
	private String confirmPassword = null;
	private boolean disableEditName = false;
	private boolean checkUser = false;
	private List<Role> allRoles = new ArrayList<Role>();
	private List<SelectItem> availableRoles = new ArrayList<SelectItem>();
	private String[] userRoles = null;
	private UserManager userManager = UserManagerImpl.getPlanetsUserManager();
	private RoleManager roleManager = RoleManagerImpl.getPlanetsRoleManager();

	/**
	 * Constructor for the UseBackingBean, this populates the user manager and user members
	 * 
	 * @throws UserNotFoundException When the user is not in the database.
	 */
	public UserBackingBean() {
		// We'll get the remote username, this doesn't change and we assume
		// it remains the same for the session
		if (null != this.getExternalContext().getRemoteUser()) {
			// Get the username
			this.username = this.getExternalContext().getRemoteUser();
			this.loadUser(username);
			// If it's an admin user we may need all of the roles
			if (this.getIsAdmin()) {
				this.allRoles = roleManager.getAllRoles();
				this.availableRoles.clear();
				for (Role role : this.allRoles) {
					this.availableRoles.add(new SelectItem(role.getName(), role.getName()));
				}
			}
		} else {
			log.info("getRemoteUser returned null, user not logged in");
		}
	}

	/**
	 * Returns the user object
	 * 
	 * @return The currently loaded user object
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Setter for the user object
	 * 
	 * @param value A User ref
	 */
	public void setUser(User value) {
		this.user = value;
	}

	/**
	 * 
	 * @return all users as a List of User interface refs
	 */
	public List<User> getAllUsers() {
		return this.allUsers;
	}
	/**
	 * 
	 * @param value A new List of User refs
	 */
	public void setAllUsers(List<User> value) {
		this.allUsers = value;
	}

	/**
	 * The cancel method simply handles the pressing of the cancel button by the user
	 * when editing a profile.  We simply reload the user and redisplay
	 * 
	 * @throws UserNotFoundException When the user cannot be found in the database
	 */
	public String cancelEdit() throws UserNotFoundException {
		this.loadUser(this.user.getUsername());
		return "canceledit";
	}

	/**
	 * 
	 * @return the cancelnewuser status
	 */
	public String cancelNewUser() {
		this.user = new UserImpl();
		this.user.setAddress(new AddressImpl());
		return "cancelnewuser";
	}

	/**
	 * Getter for the original password
	 * 
	 * @return	The original password from the user loaded at construction
	 */
	public String getOriginalPassword() {
		return this.originalPassword;
	}

	/**
	 * 
	 * @param password
	 */
	public void setOriginalPassword(String password) {
		this.originalPassword = password;
	}

	/**
	 * Getter for the confirm password member (used to handle the hashing).
	 * 
	 * @return the confirm password value from the web form
	 */
	public String getConfirmPassword() {
		return this.confirmPassword;
	}

	/**
	 * Setter for the confirm password
	 * 
	 * @param password
	 */
	public void setConfirmPassword(String password) {
		this.confirmPassword = password;
	}

	/**
	 * Getter for the confirm password member (used to handle the hashing).
	 * 
	 * @return the confirm password value from the web form
	 */
	public String getUserPassword() {
		return this.userPassword;
	}

	/**
	 * Setter for the confirm password
	 * 
	 * @param password
	 */
	public void setUserPassword(String password) {
		this.userPassword = password;
	}

	/**
	 * 
	 * @return true if loaded user is in the admin role
	 */
	public boolean getIsAdmin() {
		return this.getExternalContext().isUserInRole("admin");
	}

	public boolean getDisableEditName() {
		return this.disableEditName;
	}
	/**
	 * Method to save the user details
	 * 
	 * @return	An outcome string for use in navigation
	 * 			saved - user was successfully saved
	 * 
	 * @throws	NoSuchAlgorithmException
	 * @throws  UserNotFoundException
	 */
	public String saveUser() throws NoSuchAlgorithmException, UserNotFoundException {
		// The user has changed the password so we need to change the user bean 
		// password with the hashed password value
		if (this.passwordChanged()) {
			// the user has changed the password, need to check the confirmed version
			if (!this.passwordConfirmed()) {
				log.info("Password not properly confirmed so getting out");
				// If it's not confirmed properly then go to confirm outcome
				return "badpassword";
			}
			// We're confirmed so let's populate the user bean with the hash
			this.user.hashPassword(userPassword);
			this.userPassword = this.user.getPassword();
			this.confirmPassword = this.userPassword;
			this.originalPassword = this.userPassword;
		}
		this.arrangeUserRoles();
		userManager.saveUser(this.user);
		String retVal = this.isEditingSelf() ? "savedownprofile" : "savedotherprofile";
		this.loadUser(this.username);
		return retVal;
	}

	/**
	 * 
	 * @return
	 * @throws UserNotValidException
	 * @throws UserNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public String addUser() throws UserNotValidException, UserNotFoundException, NoSuchAlgorithmException {
		// The user has changed the password so we need to change the user bean 
		// password with the hashed password value
		if (!this.passwordConfirmed()) {
			log.info("Password not confirmed properly so bailing");
			// If it's not confirmed properly then go to confirm outcome
			return "badpassword";
		}

		// We're confirmed so let's populate the user bean with the hash
		this.user.hashPassword(userPassword);
		this.userPassword = this.user.getPassword();
		this.confirmPassword = this.userPassword;
		this.originalPassword = this.userPassword;
		// Check user name is still available
		if (!userManager.isUsernameAvailable(user.getUsername())) {
			log.info("UserManager says name " + user.getUsername() + " is not available so bailing");
			return "usernamenotavailable";
		}
		//log.info("Arranging roles for new user");
		//this.arrangeUserRoles();
		userManager.addUser(this.user);
		// A bit convoluted because of Role persistence problems
		// first copy the user roles
		String[] roleCopy = this.userRoles.clone();
		// Now load the user
		this.loadUser(this.user.getUsername());
		// Now copy back and return the roles then save the user
		this.userRoles = roleCopy.clone();
		return this.saveUser();
	}

	private boolean passwordChanged() {
		// Check that all of the password fields are the same
		//(the user hasn't changed either password field on the form
		return !(this.originalPassword.equals(this.confirmPassword)  && this.originalPassword.equals(this.userPassword));
	}

	/**
	 * Tests whether the user's password and confirm password are equal
	 * 
	 * @return true if password == confirm password, otherwise false
	 */
	public boolean passwordConfirmed() {
		return (this.confirmPassword.equals(this.userPassword));
	}

	private void loadUser(String name) {
		try {
			this.user = userManager.getUserByUsername(name);
		} catch (UserNotFoundException e) {
			log.info("user " + this.username + " tried to load user " + name);
			log.info("No record for " + name + "found in if user database");
			e.printStackTrace();
			return;
		}
		this.userPassword = this.confirmPassword = this.originalPassword = this.user.getPassword();
		this.userRoles = this.user.rolesAsStrings();
		this.disableEditName = true;
	}
	
	public boolean getRefreshUserList() {
		this.loadAllUsers();
		return true;
	}
	public void setRefreshUserList(boolean refresh) {
	}

	private void loadAllUsers() {
		this.allUsers = userManager.getUsers();
	}

	/**
	 * 
	 * @return
	 */
	public String prepareNewUser() {
		this.user = new UserImpl("");
		this.user.setAddress(new AddressImpl());
		this.user.setAccountEnabled(true);
		this.userRoles = new String[1];
		this.userRoles[0] = "user";
		this.disableEditName = false;
		this.checkUser = true;
		return "newuser";
	}

	/**
	 * 
	 * @return
	 * @throws UserNotFoundException
	 */
	public String editUserByName() throws UserNotFoundException {
		Object value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("userToEdit");
		String userToEdit = value.toString();
		this.loadUser(userToEdit);
		this.checkUser = true;
		return "editexistinguser";
	}

	private ExternalContext getExternalContext() {
		log.info("UserBackingBean::getExternalContext()");
		return FacesContext.getCurrentInstance().getExternalContext();
	}

	/**
	 * 
	 * @param context
	 * @param toValidate
	 * @param value
	 */
	public void validateUsername(FacesContext context, 
			UIComponent toValidate,
			Object value) {
		String username = (String) value;
		boolean avail =  userManager.isUsernameAvailable(username);
		if (avail != true) {
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage("Username not available.");
			context.addMessage(toValidate.getClientId(context), message);
		}
	}

	/**
	 * 
	 * @param context
	 * @param toValidate
	 * @param value
	 */
	public void validateEmail(FacesContext context, 
			UIComponent toValidate,
			Object value) {
		String email = (String) value;
		Pattern patt = Pattern.compile("^[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})$");
		Matcher match = patt.matcher(email);
		if(!match.find()) {
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage("Invalid email address.");
			context.addMessage(toValidate.getClientId(context), message);
		}
	}

	/**
	 * @param roles the roles to set
	 */
	public void setUserRoles(String[] roles) {
		this.userRoles = roles;
	}

	private void arrangeUserRoles() {
		// TODO: Look at improving this method
		// Now this is convoluted and their must be a better way of doing it
		// BUT I ran into consistent persistence problems (transaction based) when I tried to do
		// it any other way SO
		try {
			// We take a copy of the hash set (can't edit it and work on it at the same time) 
			Set<Role> oldUserRoles = new HashSet<Role>();
			for (Role role : this.user.getRoles()) {
				oldUserRoles.add(role);
			}

			// Now cycle through the users roles
			for (Role userRole : oldUserRoles) {
				boolean roleFound = false;
				// And the role names from the profile edit page
				for (String roleName : this.userRoles) {
					// Do a case insensitive check and set the roleFound flag
					if (userRole.getName().compareToIgnoreCase(roleName) == 0) {
						roleFound = true;
						break;
					}
				}
				// If the role was NOT found then remove it from the user
				if (!roleFound) {
					this.user.removeRole(userRole);
				}
			}
			
			// Now copy the remaining user Roles again
			Set<Role> newUserRoles = new HashSet<Role>();
			for (Role role :this.user.getRoles()) {
				newUserRoles.add(role);
			}
			
			// Then go through the String array from the edit profile page control
			for (String newRole : this.userRoles) {
				boolean roleFound = false;
					// Cycle through the remaining user roles
					for (Role userRole : newUserRoles) {
						// If role found set flag and break
						if (userRole.getName().compareToIgnoreCase(newRole) == 0) {
							log.info("ROLE FOUND");
							roleFound = true;
							break;
						}
					}
					// If the role wasn't found then add it to the user
					if (!roleFound) {
						Role userRole = roleManager.getRoleByName(newRole);
						this.user.addRole(userRole);
					}
			}
		} catch (RuntimeException e) {
			// Catch for Runtime Exceptions from the roleManager
			log.info("Caught exception " + e.getMessage());
			e.printStackTrace();
		} catch (RoleNotFoundException e) {
			log.info("Couldn't load Role from db");
		}
	}
	/**
	 * @return the roles
	 */
	public String[] getUserRoles() {
		return userRoles;
	}

	/**
	 * @param allRoles the allRoles to set
	 */
	public void setAllRoles(List<Role> allRoles) {
		this.allRoles = allRoles;
	}

	/**
	 * @return the allRoles
	 */
	public List<Role> getAllRoles() {
		return allRoles;
	}

	/**
	 * @param availableRoles the availableRoles to set
	 */
	public void setAvailableRoles(List<SelectItem> availableRoles) {
		this.availableRoles = availableRoles;
	}

	/**
	 * @return the availableRoles
	 */
	public List<SelectItem> getAvailableRoles() {
		return availableRoles;
	}

	/**
	 * 
	 * @return
	 */
	public String deleteUser() {
		// first check that the user is not trying to delete their own account
		if (this.isEditingSelf()) {
			log.info("user " + this.username + " tried to delete their own account");
			return "deleteself";
		}
		// OK lets use the user manager to remove this user
		this.userManager.removeUser(this.user.getId());

		this.loadUser(username);
		return "deleteuser";
	}
	
	private boolean isEditingSelf() {
		// Check to see if the user is editing their own profile
		return this.user.getUsername().equals(this.username);
	}

	/**
	 * @param checkUser the checkUser to set
	 */
	public void setCheckUser(boolean checkUser) {
		this.checkUser = checkUser;
	}

	/**
	 * @return the checkUser
	 */
	public boolean isCheckUser() {
		if ((!this.checkUser) && (!this.isEditingSelf())) {
			this.loadUser(username);
		} else {
			this.checkUser = false;
		}
		return checkUser;
	}
}
