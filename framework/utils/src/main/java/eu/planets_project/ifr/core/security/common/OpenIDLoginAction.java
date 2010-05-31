/**
 * 
 */
package eu.planets_project.ifr.core.security.common;

import org.apache.log4j.Logger;
import org.josso.gateway.signon.LoginAction;
import org.josso.auth.Credential;
import org.josso.auth.exceptions.SSOAuthenticationException;
import org.josso.gateway.SSOGateway;

import javax.servlet.http.HttpServletRequest;

/**
 * @author AnJackson
 *
 */
public class OpenIDLoginAction extends LoginAction {

    /**
     * Request parameter containing user password.
     * Value : sso_password
     */
    public static final String PARAM_OPENID_USERNAME="openid_username";

    /*
     * A logger, using Planets':
     */
    private static final Logger log = Logger.getLogger(OpenIDLoginAction.class.getName());
    
    /**
     * Creates credentials for an OpenID.
     * 
     * Performs the OpenID credential check,
     * creates a local user for that OpenID if necessary,
     * and creates a random password for the user.
     * 
     * These credentials are then passed to the login 
     * framework to log the user in through JOSSO.
     * 
     * If credentials are to be propagated to other services, 
     * the hook should be called here. 
     * @see eu.planets_project.ifr.core.security.common.SingleSignOnPropagatorValve
     */
    protected Credential[] getCredentials(HttpServletRequest request) throws SSOAuthenticationException {

        // Fire up the gateway and determine the username:
        SSOGateway g = getSSOGateway();
        Credential username = g.newCredential("basic-authentication", "username", request.getParameter(PARAM_OPENID_USERNAME));
        log.debug("Initiating OpenID login for '" + username + "'");
        
        // Map to local username:

        // Create account or update password to a new random password:
        
        // Create a JOSSO credential matching this local account:
        Credential password = g.newCredential("basic-authentication", "password", "RandomPassword");
        Credential[] c = {username, password};
        
        // Note that we may or may not be able to get an email address from the OpenID
        // So, we might need to pass the user onto another page to get their email 
        // address before logging in fully?
        
        // As this security issue is not clear, I am halting development of the OpenID login.

        // Return to JOSSO and let the sign-on continue?
        return c;
    }
    
}
