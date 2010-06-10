package eu.planets_project.ifr.core.security.impl.services;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.SelfRegistrationManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;

public class SelfRegistrationManagerImpl implements SelfRegistrationManager {
	private static Logger log = Logger.getLogger(SelfRegistrationManagerImpl.class.getName());

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
			log.info(e.getClass().getName()+": "+e.getMessage());
		}
		return user;
	}

	public void addUser(User user) throws UserNotValidException {
		log.info("SelfRegistrationManagerImpl.addUser checking useravailibility");
		if( ! isUsernameAvailable(user.getUsername())) {
			log.info("SelfRegistrationManagerImpl.addUser User NOT available");
			throw new UserNotValidException();
		}
		// Should be okay to store:
		log.info("SelfRegistrationManagerImpl.addUser persisting the user");
		log.info("User name = " + user.getUsername());
		if (null != user.getId()) {
			log.info("User ID = " + user.getId().toString());
		}
		manager.getTransaction().begin();
		manager.persist(user);
		manager.getTransaction().commit();
		manager.flush();
	}
}
