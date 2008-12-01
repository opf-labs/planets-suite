/**
 * 
 */
package eu.planets_project.ifr.core.security.api.services;

import java.util.List;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.model.Resource;

/**
 * This manager looks after any resources, like Data Registries, 
 * that may require a secure login. 
 * 
 * This is an early draft, as secured access to resources 
 * is not yet to be implemented.
 * 
 * @author AnJackson
 *
 */
interface ResourceManager {

    /**
     * List all available resources.
     * @return A list of Resource objects
     */
    public List<Resource> getAvailableResources();
        
    /**
     * Attempt to use the user credentials to log in to 
     * any and all other resources known to this User Manager.
     * 
     * TODO Consider moving to OAuth.
     * @param user the user object
     * @param password The users password, in clear text.
     */
    public void propagateCredentials( User user, String password );

}
