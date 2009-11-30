package eu.planets_project.ifr.core.security.impl.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.Util;
import org.jboss.security.auth.callback.SecurityAssociationHandler;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.SelfRegistrationManager;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;

public class SelfRegistrationManagerImpl implements SelfRegistrationManager {
	private static Log log = LogFactory.getLog(SelfRegistrationManagerImpl.class);

	/**
	 * This is the JPA entity manager declaration.
	 */
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("planetsSelfReg");
	private static EntityManager manager = emf.createEntityManager();

	public SelfRegistrationManagerImpl() {
		log.info("Here I am");
	}
	
	public boolean isUsernameAvailable(String username) {
		log.info("SelfRegistrationManagerImpl.isUsernameAvailable");
		log.info("Checking availability of " + username);
		try {
			log.info("Who you gonna call, SelfRegistrationManagerImpl.getUserByUsername for " + username);
			User user = this.getUserByUsername(username);
			log.info("Checking for null username");
			if( user != null ) {
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

	private User getUserByUsername(String username) throws UserNotFoundException {
		log.info("SelfRegistrationManagerImpl.getUserByUsername()");
		// Test programmatic some security stuff:

		// Now perform the actual getUserByUsername operation:
		User user = null;
		try {
			log.info("creating query for username " + username);
			if (manager == null) {
				log.info("manager IS NULL");
			}
			Query query = manager.createQuery("SELECT u FROM SelfUserImpl AS u WHERE u.username =:username");
			log.info("setting parameter username to " + username);
			query.setParameter("username", username);
			log.info("query.getsingleresult()");
			// Doing this slightly differently due to runtime exception when no result
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
		} catch (Exception e) {
			log.info(e);
		}
		return user;
	}

	public void addUser(User user) throws UserNotValidException {
		log.info("SelfUserManagerImpl.addUser()");
		log.info("User name = " + user.getUsername());
		if (null != user.getId()) {
			log.info("User ID = " + user.getId().toString());
		}
		log.info("SelfRegistrationManagerImpl.addUser checking useravailibility");
		if( ! isUsernameAvailable(user.getUsername())) {
			log.info("SelfRegistrationManagerImpl.addUser User NOT available");
			throw new UserNotValidException();
		}
		// Should be okay to store:
		log.info("SelfRegistrationManagerImpl.addUser persisting the user");
		manager.getTransaction().begin();
		manager.persist(user);
		manager.getTransaction().commit();
	}
}
