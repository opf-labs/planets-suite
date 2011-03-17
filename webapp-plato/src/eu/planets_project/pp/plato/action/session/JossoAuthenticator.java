package eu.planets_project.pp.plato.action.session;

import java.io.Serializable;

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
/**
 * This an authenticator for Seam that lets Seam grab signon
 * credentials from JOSSO.
 * 
 * The authenticator obtains the JOSSO session id, and attempts
 * to obtain the active JOSSO session. If an active session is
 * found it sets the username, and the roles given to this
 * user into Seam context.
 * 
 * @author <a href="mailto:kurt.stam@osconsulting.org">kurt.stam&064;osconsulting.org</a>
 * 
 
@Name("jossoAuthenticator")*/
public class JossoAuthenticator implements Serializable {
   private static final long serialVersionUID = 1L;
/*    
    @Logger
    Log log;
    
    @In 
    FacesContext facesContext;

    @In
    Identity identity;
    
    public void checkLogin() {
        final boolean isLoggedIn = identity.isLoggedIn();
        // user may already be logged in - check
        if (isLoggedIn) {
          return;
        }
        authenticate();
    }
    
    public boolean authenticate() 
    {
        Map map = facesContext.getExternalContext().getRequestCookieMap();
        String sessionId=null;
        if (map.containsKey(Constants.JOSSO_SINGLE_SIGN_ON_COOKIE)) {
            sessionId = ((Cookie) map.get(Constants.JOSSO_SINGLE_SIGN_ON_COOKIE)).getValue();
        }
        try {
            if (sessionId != null && !"".equals(sessionId)) {
                SSOAgent jossoAgent = Lookup.getInstance().lookupSSOAgent();
                SSOSession session = jossoAgent.getSSOSessionManager().getSession(sessionId);
                String username = session.getUsername();
                identity.setUsername(username);
                identity.setPassword(username);
                log.info( "User " + username + " logged into Seam via JossoAuthenticator module.");
                SSORole[] roles = jossoAgent.getSSOIdentityManager().findRolesBySSOSessionId( sessionId );
                for (int i=0; i<roles.length; i++) {
                    String role = roles[i].getName();
                    log.info( "User " + username + " adding role " + role);
                    identity.addRole(role);
                }
                return true;
            } else {
                log.error("No JOSSO session found: " + sessionId + ". User not authenticated.");
            }
        } catch (NoSuchSessionException e) {
            log.error("NoSuchSessionException : " + sessionId + ". User not authenticated.");
        } catch (Exception e) {
            log.error(e.getMessage() + ". User not authenticated.", e);
        }
        return false;
    }
*/
}
