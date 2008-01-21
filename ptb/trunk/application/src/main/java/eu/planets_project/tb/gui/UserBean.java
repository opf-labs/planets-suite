package eu.planets_project.tb.gui;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.api.UserManager;
import eu.planets_project.ifr.core.common.api.User;

/**
 * UserBean.java serves as the POJO for storing information about a Testbed User.
 */


public class UserBean 
{
    private static Log log = PlanetsLogger.getLogger(UserBean.class, "testbed-log4j.xml");
    private String firstname;
    private String lastname;
    private String email;
    private String userid;
    private String password;
    private boolean isLoggedIn;


    public UserBean()
    {
    }

    public UserBean(String userid )
    {
        this.setUserid(userid);
    }

    public UserBean(String firstName, String lastName, String email, String userid, String password)
    {
        this.setUserid(userid);
        this.setFirstname(firstName);
        this.setLastname(lastName);
        this.setEmail(email);
        this.setPassword(password);
    }


    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }


    public void setIsLoggedIn(boolean isLoggedIn)
    {
        this.isLoggedIn = isLoggedIn;
    }


    public boolean isIsLoggedIn()
    {
        return isLoggedIn;
    }

    public void setUserid(String userid) {
        this.userid = userid;
        // Also, when the user ID is set, look up the user details
        UserManager um = UserBean.getUserManager();
        User u = null;
        try {
            u = um.loadUserByUsername(userid);
        } catch( Exception e ){
            log.error("Exception while attempting to load the User details: "+e);
        }
        // If we succeeded:
        if( u != null ) {
            this.firstname = u.getFirstName();
            this.lastname = u.getLastName();
            this.email = u.getEmail();
            log.info("User lookup succeeded: Got details for "+u.getFullName());
        }
    }

    public String getUserid() {
        return userid;
    }
    
    public boolean isAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        boolean result = false;
        result = request.isUserInRole("testbed.admin");
        // TODO ANJ: Temporary override to ensure that the global administrator gets full access:
        if( request.isUserInRole("admin") ) result = true;
        log.info("user " + request.getRemoteUser() +" is admin? - " + result);
        return result;  
    }

    public boolean isExperimenter() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        boolean result = false;
        result = request.isUserInRole("testbed.experimenter");
        log.info("user " + request.getRemoteUser() + " is experimenter? - " + result);
        return result;  
    }

    public boolean isReader() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        boolean result = false;
        result = request.isUserInRole("testbed.reader");
        log.info("user " + request.getRemoteUser() +" is reader? - " + result);
        return result;  
    }  

    private static UserManager getUserManager(){
        try{
            Context jndiContext = getInitialContext();
            UserManager um = (UserManager) PortableRemoteObject.narrow(
                    jndiContext.lookup("planets-project.eu/UserManager/remote"), UserManager.class);
            return um;
        }catch (NamingException e) {
            log.error("Failure in getting PortableRemoteObject: "+e.toString());
            return null;
        }
    }

    private static Context getInitialContext() throws javax.naming.NamingException
    {
        return new javax.naming.InitialContext();
    }


}
