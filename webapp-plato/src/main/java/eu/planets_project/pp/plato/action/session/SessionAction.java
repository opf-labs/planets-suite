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
package eu.planets_project.pp.plato.action.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import eu.planets_project.pp.plato.action.project.LoadPlanAction;
import eu.planets_project.pp.plato.model.Role;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.util.PlanetsUserManager;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Session action component. Performs logout and session clean up in case of an
 * unexpected exception.
 *
 * @author Hannes Kulovits
 */
@Scope(ScopeType.SESSION)
@Name("sessionManager")
public class SessionAction implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2557502776113858435L;

    private static final Log log = PlatoLogger.getLogger(SessionAction.class);
    
    @In
    EntityManager em;

    @In
    private FacesContext facesContext;

    /**
     * Number of times the session has been refreshed, e.g. by the ajax poller
     */
    private int timesRefreshed = 0;

    @Out(required = false)
    private User user;
    
    @In(required = false)
    private LoadPlanAction loadPlan;
    
    /**
     * Used if User-Agent is IE, because some features do not work with IE
     */
    @Out(scope = ScopeType.SESSION)
    boolean microsoft = false;
    
    @Create
    public void onCreate() {
        initUser();
    }
    
    /**
     * stores the fact that the user has read and accepted the microsoft "issue" -
     * not persistent in the database, but for this session he is not being bothered
     * anymore.
     * @return null
     */
    public String acceptMicrosoft()  {
        log.info("Microsoft warning read and accepted");
        microsoft = false;
        return null;
    }
    
    /**
     * Initializes session variable 'user' with logged in user. The logged in user is
     * determined by interoperability framework.
     *
     * @see PlanetsUserManager
     */
    @Factory("user")
    public void initUser() {

        PlanetsUserManager userManager = PlanetsUserManager.createUserManager();
        
        this.user = alignWithUserInDB(userManager.getLoggedInUser());
        
        Map<String,String> headers = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
        String userAgent =  headers.get("user-agent");
        log.debug("userAgent is: " + userAgent);
        if(userAgent != null && (userAgent.indexOf("IE") != -1 || userAgent.indexOf("Microsoft") != -1)){
            this.microsoft = true;
        }
        
        if (userAgent == null) {
            userAgent = "Unknown";
        }
        
        String id = "";
        try {
            id = ((HttpServletRequest) facesContext.getExternalContext()
                    .getRequest()).getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }
        
        log.info("User '" + this.user.getUsername() + "' logged in using browser: '" + userAgent + "'. SessionID: " + id);
    }
    
    /**
     * User role associations may change over time. This method checks if anything
     * has changed and updates our user-role mappings.
     * @param loggedinUser
     */
    private void alignRoles(User loggedinUser) {
        
        List<Role> completeRoles = new ArrayList<Role>();
        
        // we fetch all roles from the database
        List<Role> rolesInPlato = em.createQuery("select r from Role r").getResultList();
        
        // add all roles to our user that are already in the database (in table ROLE)
        for (Role r : rolesInPlato) {
            if (loggedinUser.hasRole(r.getName())) {
                // user still has role, everything fine, add role to complete list
                completeRoles.add(r);
            } 
        }
        
        // Now completeRoles contains all roles that we already have 
        // and that are still valid (i.e. the user still has them)
        
        // Now we add all roles to our database (table ROLE)
        // that our user has but are not yet stored in the database
        for (Role r : loggedinUser.getRoles()) {
            if (!rolesInPlato.contains(r)) {
                Role newRole = new Role();
                newRole.setName(r.getName());
                completeRoles.add(newRole);
            }
        }
        loggedinUser.setRoles(completeRoles);
        
    }
    
    /**
     * this method takes a "planets" user and checks if the plato-specific entries
     * are present and complete in the database. That means we check the user entry and
     * its associated roles and 
     * @param loggedInUser
     * @return
     */
    private User alignWithUserInDB(User loggedInUser) {
        User u = null;
        try {
            u = (User) em.createQuery("select u from User u where u.username = :user_name")
                .setParameter("user_name", loggedInUser.getUsername())
                .getSingleResult();
        } catch (NoResultException e) {
            log.info("User "+loggedInUser+ " logged into Plato the first time.");
            // no user with this username in database, so lets add him:
            u = loggedInUser.clone();
        }
        
        u.setFirstName(loggedInUser.getFirstName());
        u.setLastName(loggedInUser.getLastName());
        u.setEmail(loggedInUser.getEmail());
        u.setRoles(loggedInUser.getRoles());
        
        alignRoles(u);
        // persist all new roles
        for (Role r : u.getRoles()) {
            if (r.getId() == 0) {
                em.persist(r);
            }
        }
        em.persist(u);
        em.flush();
        return u ;
        
    }
    
    /**
     * Logs out the user neatly, with unlocking the project and invalidating
     * seam session.
     */
    public String logout() {
        if (loadPlan != null) {
            loadPlan.unlockProject();
        }
        String username = (user == null ? "" : user.getUsername());

        log.info("User "  + username
                + " logging out, Session is being invalidated.");
        endSession();
        return "logout";
    }

    /**
     * Ends the session in case of an error. Retrieves the occurred exception
     * from Seam context and logs it.
     */
    @Observer("exceptionHandled")
    public String endSession() {
        String id = "";
        try {
            id = ((HttpServletRequest) facesContext.getExternalContext()
                    .getRequest()).getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }

        try {
            log.debug("Ending Session "+id);
            Context session = Contexts.getSessionContext();
            if (loadPlan != null) {
                loadPlan.unlockProject();
            }

            session.remove("user");
            session.remove("selectedPlan");
            session.set("changed", "");
            org.jboss.seam.web.Session.instance().invalidate();
        } catch (RuntimeException e) {
            return "login";
        } 
        return null;
    }

    public void keepAlive() {
        timesRefreshed ++;
    }

    @Destroy
    @Remove
    public void destroy() {

    }
}