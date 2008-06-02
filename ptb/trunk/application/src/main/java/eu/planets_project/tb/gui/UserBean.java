package eu.planets_project.tb.gui;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.*;

/**
 * UserBean.java serves as the POJO for storing information about a Testbed User.
 */


public class UserBean 
{
    private static Log log = PlanetsLogger.getLogger(UserBean.class, "testbed-log4j.xml");
    private String firstname;
    private String lastname;
    private String email;
    private String fullname;
    private String address;
    private String telephone;
    private String userid;
    private String password;
    private boolean isLoggedIn;


    public UserBean()
    {
        this.checkUser();
    }

    public UserBean(String userid )
    {
        this.setUserid(userid);
    }

    public UserBean(String firstName, String lastName, String email, String userid, String password)
    {
        this.setUserid(userid);
        this.firstname = firstName;
        this.lastname = lastName;
        this.email = email;
    }


    public String getFirstname()
    {
        checkUser();
        return firstname;
    }

    public String getLastname()
    {
        checkUser();
        return lastname;
    }

    public String getEmail()
    {
        checkUser();
        return email;
    }

    public String getPassword()
    {
        checkUser();
        return password;
    }

    /**
     * @return the fullname
     */
    public String getFullname() {
        checkUser();
        return fullname;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        checkUser();
        return address;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        checkUser();
        return telephone;
    }

    public boolean isIsLoggedIn()
    {
        return isLoggedIn;
    }

    public void setUserid(String userid) {
        this.userid = userid;
        
        log.info("Looking up user details for " + userid);
        if( userid == null ) return;
        if( "".equals(userid) ) return;
        log.info("Looking up user details for " + userid);
        
        this.userid = userid;
        // Also, when the user ID is set, look up the user details
        UserManager um = UserBean.getUserManager();
        if( um == null ) {
            log.error("Could not get the User Manager!");
            return;
        }
        User u = null;
        try {
            u = um.getUserByUsername(userid);
        } catch( UserNotFoundException e ){
            log.error("Exception while attempting to load the User details for '"+userid+"': "+e);
            if( log.isDebugEnabled() ) e.printStackTrace();
        }
        // If we succeeded:
        if( u != null ) {
            this.firstname = u.getFirstName();
            this.lastname = u.getLastName();
            this.fullname = u.getFullName();
            this.email = u.getEmail();
            this.telephone = u.getPhoneNumber();
            // For now, we must format the address manually.
            // FIXME The Address entity should do this.
            this.address = u.getAddress().getAddress() + ",\n" + 
            u.getAddress().getCity() + ",\n" +
            u.getAddress().getProvince() + " " + u.getAddress().getPostalCode() + ",\n" +
            u.getAddress().getCountry();
            log.debug("User lookup succeeded: Got details for "+u.getFullName());
        } else {
            log.error("Username '"+userid+"' not found! Returned a null User object.");
        }
    }

    public String getUserid() {
        checkUser();
        return userid;
    }
    
    public boolean isAdmin() {
        boolean result = false;
        result = getRequest().isUserInRole("testbed.admin");
        //log.debug("user " + request.getRemoteUser() +" is admin? - " + result);
        return result;  
    }

    public boolean isExperimenter() {
        boolean result = false;
        result = getRequest().isUserInRole("testbed.experimenter");
        //log.debug("user " + request.getRemoteUser() + " is experimenter? - " + result);
        return result;  
    }

    public boolean isReader() {
        boolean result = false;
        result = getRequest().isUserInRole("testbed.reader");
        // Also add reader access for administrators and experimenters:
        if( isAdmin() || isExperimenter() ) result = true;
        //log.debug("user " + request.getRemoteUser() +" is reader? - " + result);
        return result;  
    }  

    /**
     * Checks if the user information is up to date.
     */
    private void checkUser() {
        // This Bean can fail out of date, so test if we are up to date:
        if( getRequest().getRemoteUser() == null ) {
            this.setUserid(null);
            return;
        }
        if( ! getRequest().getRemoteUser().equals(userid) )
            this.setUserid(getRequest().getRemoteUser());
    }
    
    /**
     * Utility function to look up the HttpRequest
     * @return
     */
    private HttpServletRequest getRequest() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (HttpServletRequest) context.getExternalContext().getRequest();
    }
    
    /**
     * Create a user manager:
     * @return
     */
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

    /**
     * Get the initial context:
     * @return
     * @throws javax.naming.NamingException
     */
    private static Context getInitialContext() throws javax.naming.NamingException
    {
        return new javax.naming.InitialContext();
    }
    

}
