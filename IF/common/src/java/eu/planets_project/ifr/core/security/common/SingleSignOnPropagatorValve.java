/**
 * 
 */
package eu.planets_project.ifr.core.security.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
//import eu.planets_project.ifr.core.storage.gui.login.LoginBean;

/**
 * @author AnJackson
 *
 * This code has been placed here so that it ends in the server/lib directory.
 * It needs to be there in order to be picked up during the initialisation of a servlet, before it's jars have been loaded.
 * It may be of general use, in setting up the DR session.
 * 
 * See the SingleSignOn Valve of inspiration:
 * @see http://svn.apache.org/repos/asf/tomcat/container/tc5.5.x/catalina/src/share/org/apache/catalina/authenticator/SingleSignOn.java
 * 
 * The OpenID login would be another example of code where we can propagate the login:
 * @see eu.planets_project.ifr.core.security.common.OpenIDLoginAction
 * 
 */
public class SingleSignOnPropagatorValve extends ValveBase {
    // A Planets Logger:
    private static Log log = LogFactory.getLog(SingleSignOnPropagatorValve.class);
    
    // The parameters that will store the username and password, usually j_username and j_password.
    private static String USER_PARAM = "josso_username";
    private static String PASS_PARAM = "josso_password";


    /* (non-Javadoc)
     * @see org.apache.catalina.Valve#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
     */
    public void invoke(Request request, Response response) throws IOException,
    ServletException { 

        // Find the username and password, if they are there...
        HttpServletRequest hRequest = (HttpServletRequest)request.getRequest();
        String username = (String)hRequest.getParameter(USER_PARAM);
        String password = (String)hRequest.getParameter(PASS_PARAM);
        // Is there a username/password combination going past?
        if( username != null && password != null ) {
            // Use it:
            log.warn("Found user:"+username+" pass:"+password);
            hRequest.getSession().setAttribute("secret_password", password);
            /*
        FacesContext context = FacesContext.getCurrentInstance();
        LoginBean lb = (LoginBean) context.getApplication().getVariableResolver().resolveVariable(context, "LoginBean");
        lb.setUsername(username);
        lb.setPassword(password);
        lb.login();
             */
        } else {
            password = (String) hRequest.getParameter("secret_password");
            if( password != null ) {
                log.warn("Found secret_pass:"+password);
            }
        }

        // Invoke the next Valve in our pipeline
        getNext().invoke(request, response);

        String stored_password = (String) hRequest.getParameter("secret_password");
        if( stored_password != null ) {
            log.warn("Found secret_pass:"+stored_password);
        } else if( username != null ){
            log.warn("Could not find secret_pass:"+password);
            hRequest.getSession().setAttribute("secret_password", password);
        }
    }



}