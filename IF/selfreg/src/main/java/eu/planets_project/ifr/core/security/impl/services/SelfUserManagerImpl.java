/**
 * 
 */
package eu.planets_project.ifr.core.security.impl.services;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;
import javax.security.auth.Subject;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.common.mail.PlanetsMailMessage;
import eu.planets_project.ifr.core.security.api.model.Role;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.impl.model.SelfRoleImpl;
import eu.planets_project.ifr.core.security.impl.model.SelfUserImpl;

/**
 * This is the concrete implementation of the User Manager.
 * It conforms to a single interface, the UserManagerInterface.
 * It exports this interface locally, remotely, and (eventually) over web services too.
 * Modelled on the DataManager more or less.
 * 
 * TODO Add some tests.
 * TODO Lock down user info access programmatically. Users can only get usernames?
 * 
 * @author AnJackson
 *
 */
@Stateless(mappedName="security/LocalSelfUserManager")
@Local(UserManager.class)
@Remote(UserManager.class)
@LocalBinding(jndiBinding="planets-project.eu/SelfUserManager/local")
@RemoteBinding(jndiBinding="planets-project.eu/SelfUserManager/remote")
@SecurityDomain("PlanetsRealm")
public class SelfUserManagerImpl implements UserManager {
	private static Logger log = Logger.getLogger(SelfUserManagerImpl.class.getName());

	/**
	 * This is the JPA entity manager declaration.
	 */
	@PersistenceContext(unitName="planetsSelfReg", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	/**
	 * This hooks into the EJB Context.
	 */
	@Resource SessionContext ctx;

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#getUser(java.lang.Long)
	 */
	@RolesAllowed( { "user" })
	public User getUser(Long userId) {
		return manager.find(SelfUserImpl.class, userId);
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#isUsernameAvailable(java.lang.String)
	 */
	@PermitAll
	public boolean isUsernameAvailable(String username) {
		log.info("Checking availability of " + username + " in user manager");
		try {
			log.info("Who you gonna call, getUserByUsername for " + username);
			User user = getUserByUsername(username);
			log.info("Checking for null username");
			if( getUserByUsername(username) != null ) {
				log.info("Found a user :" + user.getUsername());
				log.info("username " + username + " NOT available");
				return false;
			} else {
				log.info("username " + username + " available");
				return true;
			}
		} catch (UserNotFoundException e) {
			// This is fine, if the user isn't found then it's available
			log.info("UserNotFoundException so returning true");
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#getUsers()
	 */
	@RolesAllowed( { "user" })
	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		Query query = manager.createQuery("from UserImpl");
		return (List<User>) query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#getUsernames()
	 */
	@RolesAllowed( { "user" })
	public List<String> getUsernames() {
		List<User> users = this.getUsers();
		List<String> usernames = new ArrayList<String>(users.size());
		for( User user : users ) usernames.add(user.getUsername());
		return usernames;
	}


	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#getUserByUsername(java.lang.String)
	 */
	@RolesAllowed( { "user" })
	public User getUserByUsername(String username) throws UserNotFoundException {
		log.info("UserManagerImpl.getUserByUsername()");
		// Test programmatic some security stuff:
		log.info("Attempting some security realm lookups : java:comp/env/security/subject.");
		try {
			log.info("Getting an intital context");
			InitialContext ic = new InitialContext();
			log.info("performing subject lookup");
			Subject subject = (Subject)ic.lookup("java:comp/env/security/subject");
			log.info("listing principles");
			// To list the Principals contained in the Subject...
			for( Principal p : subject.getPrincipals() ) {
				log.info("Principal (" + p.getClass().getName()
						+ ") : " + p.getName());
			}
			// To get the roles (the instance of java.security.acl.Group
			// in the list of Principals)
			log.info("going for the roles now");
			Iterator<Group> groups = subject.getPrincipals(java.security.acl.Group.class).iterator();
			if (groups.hasNext()) {
				log.info("getting role groups");
				Group roles = (Group)groups.next();
				Enumeration<?> roleEnum = roles.members();
				while (roleEnum.hasMoreElements()) {
					log.info("Role:  " + roleEnum.nextElement());
				}
			}
		} catch ( javax.naming.NamingException e ) {
			log.info("Java naming problem");
			log.info(e.getMessage());
		}

		// Now perform the actual getUserByUsername operation:
		log.info("creating query");
		Query query = manager.createQuery("SELECT u FROM UserImpl AS u WHERE u.username =:username");
		log.info("setting parameter username to " + username);
		query.setParameter("username", username);
		log.info("query.getsingleresult()");
		// Doing this slightly differently due to runtime exception when no result
		User user = null;
		try {
			log.info("In catch block to retrieve user");
			user = (User)query.getSingleResult();
		} catch (NoResultException e) {
			// Catch the runtime exception and set user to null
			log.info("User doesn't exist so setting to null");
			user = null;
		}
		log.info("testing for user == null");
		if( user == null ) throw new UserNotFoundException();
		log.info("User ID = " + user.getId().toString());
		log.info("User name = " + user.getUsername());
		return user;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#addUser(eu.planets_project.ifr.core.security.api.model.User)
	 */
	@RolesAllowed( { "admin" })
	public void addUser(User user) throws UserNotValidException {
		log.info("UserManagerImpl.addUser()");
		log.info("User name = " + user.getUsername());
		if (null != user.getId()) {
			log.info("User ID = " + user.getId().toString());
		}
		if( ! isUsernameAvailable(user.getUsername()))
			throw new UserNotValidException();
		// Should be okay to store:
		manager.persist(user);
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#removeUser(java.lang.Long)
	 */
	@RolesAllowed( { "admin" })
	public void removeUser(Long userId) {
		manager.remove(this.getUser(userId));
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#saveUser(eu.planets_project.ifr.core.security.api.model.User)
	 */
	@RolesAllowed( { "user" })
	public void saveUser(User user) {
		Principal caller = ctx.getCallerPrincipal();
		log.info("User "+caller.getName()+" is attempting to save User("+user.getUsername()+")");
		if( ctx.isCallerInRole("admin") || 
				caller.getName().equals(user.getUsername()) ) {
			log.info("User "+caller.getName()+" saved User("+user.getUsername()+")");
			manager.merge(user);
		} else {
			log.warning("User "+caller.getName()+" attempted to save User("+user.getUsername()+")");
		}
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#sendUserMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@RolesAllowed( { "user" })
	public void sendUserMessage(String username, String subject, String body) {
		// Lookup the user:
		User user = null;
		try {
			user = this.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			log.info("UserNotFoundException");
			log.warning(e.getMessage());
			return;
		}

		// Send them a message.
		PlanetsMailMessage mailer = new PlanetsMailMessage();
		mailer.setSubject(subject);
		mailer.setBody(body);
		mailer.addRecipient(user.getEmail());
		mailer.send();
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#assignRoleToUser(eu.planets_project.ifr.core.security.api.model.User, java.lang.String)
	 */
	@RolesAllowed( { "admin", "testbed.admin" })
	public void assignRoleToUser(User user, String role) {
		user.addRole(new SelfRoleImpl(role));
		saveUser(user);
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#listRoles()
	 */
	@PermitAll
	@SuppressWarnings("unchecked")
	public String[] listRoles() {
		Query query = manager.createQuery("from RoleImpl");
		List<Role> roles = (List<Role>) query.getResultList();
		String[] rolestr = new String[roles.size()];
		int index = 0;
		for (Role role : roles) {
			rolestr[index++] = role.getName();
		}
		return rolestr;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#listUsersInRole(java.lang.String)
	 */
	@RolesAllowed( { "user" })
	public List<User> listUsersInRole(String role) {
		// TODO This should list the users by role at the DB level, not filter the all-user list.
		/*
        Query query = manager.createQuery("FROM User AS u, Role as r INNER JOIN u.role == r.id WHERE r.name == :role");
        query.setParameter("role", role);
        return (List<UserImpl>) query.getResultList();
		 */
		ArrayList<User> roleUsers = new ArrayList<User>();
		for( User user : this.getUsers() ) {
			for( Role urole : user.getRoles()) {
				if( urole.getName().equals(role))
					roleUsers.add(user);
			}
		}
		return roleUsers;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.security.api.services.UserManager#revokeRoleFromUser(eu.planets_project.ifr.core.security.api.model.User, java.lang.String)
	 */
	@RolesAllowed( { "admin", "testbed.admin" })
	public void revokeRoleFromUser(User user, String role) {
		user.removeRole(new SelfRoleImpl(role));
		saveUser(user);
	}

	/**
	 * Hook up to an instance of the Planets User Manager.
	 * @return A UserManager, as discovered via JNDI.
	 */
	public static UserManager getPlanetsUserManager() {
		try{
			Context jndiContext = new javax.naming.InitialContext();
			UserManager um = (UserManager) PortableRemoteObject.narrow(
					jndiContext.lookup("planets-project.eu/selfUserManager/remote"), UserManager.class);
			return um;
		}catch (NamingException e) {
			log.severe("Failure during lookup of the UserManager PortableRemoteObject: "+e.toString());
			return null;
		}
	}

}
