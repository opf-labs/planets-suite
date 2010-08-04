/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.util;

import java.util.Set;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;

import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.pp.plato.model.Role;
import eu.planets_project.pp.plato.model.User;

public class PlanetsUserManager implements IUserManager {

    private static PlanetsUserManager planetsUserManager = null;

    private PlanetsUserManager() {

    }

    public static PlanetsUserManager createUserManager() {
        if (planetsUserManager == null) {
            planetsUserManager = new PlanetsUserManager();
        }

        return planetsUserManager;
    }

    public String getLoggedInUserId() {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        return request.getRemoteUser();
    }

    public User getLoggedInUser() {

        UserManager um = getPlanetsUserManager();
        if(um == null) {
            return null;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        String userId = request.getRemoteUser();

        eu.planets_project.ifr.core.security.api.model.User planetsUser = null;
        try {
            planetsUser = um.getUserByUsername(userId);
        } catch(UserNotFoundException e){
            return null;
        }

        User user = new User();

        user.setUsername(planetsUser.getUsername());
        user.setFirstName(planetsUser.getFirstName());
        user.setLastName(planetsUser.getLastName());
        
        Set<eu.planets_project.ifr.core.security.api.model.Role> planetsRoles = planetsUser.getRoles();
        
        for (eu.planets_project.ifr.core.security.api.model.Role r : planetsRoles) {
            Role role = new Role();
            role.setName(r.getName());
            user.getRoles().add(role);
        }
        
        return user;
    }

    /**
     * Create a user manager:
     * @return
     */
    public static UserManager getPlanetsUserManager(){
        try{
            Context jndiContext = new javax.naming.InitialContext();
            UserManager um = (UserManager) PortableRemoteObject.narrow(jndiContext.lookup("planets-project.eu/UserManager/remote"), UserManager.class);
            return um;
        }catch (NamingException e) {
            PlatoLogger.getLogger(PlanetsUserManager.class).error(e.getMessage(),e);
            return null;
        }
    }
}
