package eu.planets_project.ifr.core.users.gui;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;
import eu.planets_project.ifr.core.security.impl.model.AddressImpl;
import eu.planets_project.ifr.core.security.impl.model.UserImpl;
import eu.planets_project.ifr.core.security.impl.services.UserManagerImpl;

/**
 * This is the controller class for the rewritten User Administration JSF web application.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class UserBackingBean {
	private static Log log = LogFactory.getLog(UserBackingBean.class);

	private User user = null;
	private List<User> allUsers = null;
	private String originalPassword = null;
	private String userPassword = null;
	private String confirmPassword = null;
	private boolean isAdmin = false;
	private boolean newUser = false;
	private boolean editProfile = true;
	private boolean disableEditName = false;

	/**
	 * Constructor for the UseBackingBean, this populates the user manager and user members
	 * 
	 * @throws UserNotFoundException When the user is not in the database.
	 */
	public UserBackingBean() throws UserNotFoundException {
		log.info("UserBackingBean::UserBackingBean()-Constructor");
		String username;
		if (null != this.getExternalContext().getRemoteUser()) {
			username = this.getExternalContext().getRemoteUser();
			log.info("username retrieved from context: " + username);
			this.loadUser(username);
			log.info("Checking if an admin user");
			if (this.isAdmin) {
				log.info("User is admin so loading all users");
				this.loadAllUsers();
			}
		} else {
			log.info("External context returned null, user not logged in");
		}
	}

	/**
	 * Returns the user object
	 * 
	 * @return The currently loaded user object
	 */
	public User getUser() {
		log.info("UserBackingBean::getUser() returning user");
		log.info("Name of user is " + user.getUsername());
		return this.user;
	}

	/**
	 * Setter for the user object
	 * 
	 * @param value
	 */
	public void setUser(User value) {
		log.info("UserBackingBean::setUser() called");
		log.info("Name of user is " + value.getUsername());
		this.user = value;
	}

	/**
	 * 
	 * @return all users as a List of User interface refs
	 */
	public List<User> getAllUsers() {
		log.info("UserBackingBean::getAllUsers() called");
		log.info("There are currently " + this.allUsers.size() + " users in the all user list");
		return this.allUsers;
	}
	/**
	 * 
	 * @param value
	 */
	public void setAllUsers(List<User> value) {
		log.info("UserBackingBean()::setAllUsers()");
		log.info("There are " + value.size()+ " members in received list");
		this.allUsers = value;
	}

	/**
	 * The cancel method simply handles the pressing of the cancel button by the user
	 * when editing a profile.  We simply reload the user and redisplay
	 * 
	 * @throws UserNotFoundException When the user cannot be found in the database
	 */
	public String cancel() throws UserNotFoundException {
		log.info("UserBackingBean::cancel()");
		log.info("Re-loading user " + this.user.getUsername());
		this.loadUser(this.user.getUsername());
		log.info("returning cancel");
		return "cancel";
	}

	/**
	 * 
	 * @return the cancelnewuser status
	 */
	public String cancelNewUser() {
		log.info("UserBackingBean::cancel()");
		log.info("Creating new user object");
		this.user = new UserImpl();
		log.info("Setting new empty address");
		this.user.setAddress(new AddressImpl());
		log.info("returning cancelnewuser");
		return "cancelnewuser";
	}

	/**
	 * Getter for the original password
	 * 
	 * @return	The original password from the user loaded at construction
	 */
	public String getOriginalPassword() {
		log.info("UserBackingBean::getOriginalPassword()");
		return this.originalPassword;
	}

	/**
	 * 
	 * @param password
	 */
	public void setOriginalPassword(String password) {
		log.info("UserBackingBean::setOriginalPassword()");
		this.originalPassword = password;
	}

	/**
	 * Getter for the confirm password member (used to handle the hashing).
	 * 
	 * @return the confirm password value from the web form
	 */
	public String getConfirmPassword() {
		log.info("UserBackingBean::getConfirmPassword()");
		return this.confirmPassword;
	}

	/**
	 * Setter for the confirm password
	 * 
	 * @param password
	 */
	public void setConfirmPassword(String password) {
		log.info("UserBackingBean::setConfirmPassword()");
		this.confirmPassword = password;
	}

	/**
	 * Getter for the confirm password member (used to handle the hashing).
	 * 
	 * @return the confirm password value from the web form
	 */
	public String getUserPassword() {
		log.info("UserBackingBean::getUserPassword()");
		return this.userPassword;
	}

	/**
	 * Setter for the confirm password
	 * 
	 * @param password
	 */
	public void setUserPassword(String password) {
		log.info("UserBackingBean::setUserPassword()");
		this.userPassword = password;
	}

	/**
	 * 
	 * @return true if loaded user is in the admin role
	 */
	public boolean getIsAdmin() {
		log.info("UserBackingBean::getIsAdmin()");
		log.info("getIsAdmin() = " + (isAdmin ? "true" : "false"));
		return this.isAdmin;
	}

	/**
	 * 
	 * @param _isAdmin
	 */
	public void setIsAdmin(boolean _isAdmin) {
		log.info("UserBackingBean::setIsAdmin()");
		this.isAdmin = _isAdmin;
	}

	public boolean getNewUser() {
		log.info("UserBackingBean::getNewUser()");
		this.editProfile = false;
		return this.newUser;
	}

	public void setNewUser(boolean val) {
		log.info("UserBackingBean::setNewUser()");
		this.newUser = val;
	}

	public boolean getEditProfile() throws UserNotFoundException {
		log.info("UserBackingBean::getEditProfile()");
		log.info("loading user:" + this.user.getUsername());
		this.loadUser(this.user.getUsername());
		log.info("Setting editProfile true");
		this.editProfile = true;
		log.info("Setting newUser false");
		this.newUser = false;
		return this.editProfile;
	}

	public void setEditProfile(boolean val) {
		log.info("UserBackingBean::setEditProfile() to " + val);
		this.editProfile = val;
	}

	public boolean getDisableEditName() {
		log.info("UserBacingBean::getDisableEditName");
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
		log.info("UserBackingBean::Save()");
		// The user has changed the password so we need to change the user bean 
		// password with the hashed password value
		log.info("Checking password change");
		if (this.passwordChanged()) {
			log.info("Password has changed so checking confirmed");
			// the user has changed the password, need to check the confirmed version
			if (!this.passwordConfirmed()) {
				log.info("Password not properly confirmed so getting out");
				// If it's not confirmed properly then go to confirm outcome
				return this.cancel();
			}

			// We're confirmed so let's populate the user bean with the hash
			log.info("Now adding new password hash to the user object");
			this.user.hashPassword(userPassword);
			log.info("resetting convoluted password vars");
			this.userPassword = this.user.getPassword();
			this.confirmPassword = this.userPassword;
			this.originalPassword = this.userPassword;
		}
		log.info("Getting a UserManager instance");
		UserManager userManager = UserManagerImpl.getPlanetsUserManager();
		log.info("Calling UserManager saveUser() for user " + this.user.getUsername());
		userManager.saveUser(this.user);
		log.info("Now reloading user " + this.user.getUsername());
		this.loadUser(user.getUsername());
		log.info("returning saveuser");
		return "saveuser";
	}

	/**
	 * 
	 * @return
	 * @throws UserNotValidException
	 * @throws UserNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public String addUser() throws UserNotValidException, UserNotFoundException, NoSuchAlgorithmException {
		log.info("UserBackingBean::addUser()");
		// The user has changed the password so we need to change the user bean 
		// password with the hashed password value
		log.info("Checking password confirmed properly");
		if (!this.passwordConfirmed()) {
			log.info("Not confirmed properly so bailing");
			// If it's not confirmed properly then go to confirm outcome
			return this.cancel();
		}

		// We're confirmed so let's populate the user bean with the hash
		log.info("We're password confirmed so let's set the user password hash");
		this.user.hashPassword(userPassword);
		log.info("Now the dreary password internal vars");
		this.userPassword = this.user.getPassword();
		this.confirmPassword = this.userPassword;
		this.originalPassword = this.userPassword;
		log.info("Get a UserManager instance");
		UserManager userManager = UserManagerImpl.getPlanetsUserManager();
		// Check user name is still available
		log.info("Checking user name availability for " + user.getUsername());
		if (!userManager.isUsernameAvailable(user.getUsername())) {
			log.info("UserManager says name " + user.getUsername() + " is not available so bailing");
			return this.cancel();
		}
		log.info("Calling UserManager.addUser() for user " + this.user.getUsername());
		userManager.addUser(this.user);
		log.info("Now reloading user " + this.user.getUsername());
		this.loadUser(user.getUsername());
		log.info("returning adduser");
		return "adduser";
	}

	private boolean passwordChanged() {
		log.info("UserBackingBean::passwordChanged()");
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
		log.info("UserBackingBean::passwordConfirmed()");
		log.info("userPassword = " + this.userPassword);
		log.info("confirmPassword = " + this.confirmPassword);
		log.info(this.confirmPassword.equals(this.userPassword));
		return (this.confirmPassword.equals(this.userPassword));
	}

	private void loadUser(String name)  throws UserNotFoundException {
		log.info("UserbackingBean::loadUser()");
		log.info("Getting a UserManagerInstance");
		UserManager userManager = UserManagerImpl.getPlanetsUserManager();
		log.info("Now retrieving user " + name);
		this.user = userManager.getUserByUsername(name);
		log.info("Setting the dreary password variables");
		this.userPassword = this.confirmPassword = this.originalPassword = this.user.getPassword();
		log.info("Checking if user is admin");
		this.isAdmin = this.getExternalContext().isUserInRole("admin");
		log.info("Setting flags");
		this.newUser = false;
		this.disableEditName = true;
	}

	private void loadAllUsers() {
		log.info("UserBackingBean::loadAllUsers()");
		log.info("Chjecking external context");
		if (null != this.getExternalContext().getRemoteUser()) {
			log.info("Context OK so getting UserManager instance");
			UserManager userManager = UserManagerImpl.getPlanetsUserManager();
			log.info("Calling UserManager.getUsers()");
			this.allUsers = userManager.getUsers();
		} else {
			log.info("No context so user not logged in");
		}
	}

	/**
	 * 
	 * @return
	 */
	public String prepareNewUser() {
		log.info("UserBackingBean()::prepareNewUser()");
		log.info("Creating new UserImpl object");
		this.user = new UserImpl();
		log.info("Setting address for new UserImple");
		this.user.setAddress(new AddressImpl());
		log.info("Setting some dreary flags");
		this.newUser = true;
		this.editProfile = false;
		this.disableEditName = false;
		log.info("returning newuser");
		return "newuser";
	}

	/**
	 * 
	 * @return
	 * @throws UserNotFoundException
	 */
	public String editUserByName() throws UserNotFoundException {
		log.info("EditUserByName");
		log.info("Getting faces context");
		FacesContext context = FacesContext.getCurrentInstance();
		log.info("checking for userToEdit parameter from parameterMap");
		Object value = context.getExternalContext().getRequestParameterMap().get("userToEdit");
		log.info("Retrieved value");
		String userToEdit = value.toString();
		log.info("Set userToEdit to " + userToEdit + ". Now loading that user.");
		this.loadUser(userToEdit);
		log.info("Deary flag setting");
		this.newUser = false;
		this.editProfile = true;
		this.disableEditName = true;
		log.info("returning editexistinguser");
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
		log.info("UserBackingBean.validateUsername()");
		String username = (String) value;
		log.info("validating user name:" + username);
		log.info("First getting a UserManager");
		UserManager userManager = UserManagerImpl.getPlanetsUserManager();
		log.info("Testing if username " + username + " is available.");
		boolean avail =  userManager.isUsernameAvailable(username);
		log.info("user:" + username + " return = " + avail);

		if (avail != true) {
			log.info("avail is false so setting setValid to false");
			((UIInput)toValidate).setValid(false);
			log.info("setting faces message");
			FacesMessage message = new FacesMessage("Username not available.");
			log.info("adding faces message");
			context.addMessage(toValidate.getClientId(context), message);
		} else {
			log.info("user " + username + " is available, hoorah");
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
		log.info("UserBackingBean::validateEmail()");
		String email = (String) value;
		log.info("Doing regexp magic");
		Pattern patt = Pattern.compile("^[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})$");
		Matcher match = patt.matcher(email);

		log.info("Checking against regexp");
		if(!match.find()) {
			log.info("Invalid email address");
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage("Invalid email address.");
			context.addMessage(toValidate.getClientId(context), message);
		} else {
			log.info("email is valid hoorah");
		}

	}
}
