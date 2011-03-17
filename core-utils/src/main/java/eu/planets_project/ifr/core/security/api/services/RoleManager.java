/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.ifr.core.security.api.services;

import java.util.List;

import eu.planets_project.ifr.core.security.api.model.Role;

/**
 * @author CFWilson
 *
 */
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
