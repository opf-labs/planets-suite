package eu.planets_project.ifr.core.security.api.services;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;

public interface SelfRegistrationManager {
    /**
     * Checks for username availability.
     * @param username
     * @return true if the name is not taken.
     */
    public boolean isUsernameAvailable(String username );
	public void addUser(User user) throws UserNotValidException;
}
