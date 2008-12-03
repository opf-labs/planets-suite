package eu.planets_project.ifr.core.security.api.services;

import java.util.List;

import eu.planets_project.ifr.core.security.api.model.Role;

public interface RoleManager {
	/**
	 * 
	 * @param name the name of the role to be retrieved
	 * @return the Role identified by the name
	 * @throws RoleNotFoundException
	 */
	public Role getRoleByName(String name) throws RoleNotFoundException;

    /**
     * Get a list of all of the roles
     * @return All of the visible roles as a List
     */
    public List<Role> getAllRoles();
    

	/**
     * Exception thrown when a role name cannot be found.
     * @author CWilson
     *
     */
    public class RoleNotFoundException extends Exception {
        static final long serialVersionUID = 3243243223432332l;
    }
}
