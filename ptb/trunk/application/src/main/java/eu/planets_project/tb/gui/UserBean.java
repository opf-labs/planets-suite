package eu.planets_project.tb.gui;

import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.*;

/**
 * UserBean.java serves as the POJO for storing information about a Testbed User.
 */


public class UserBean 
{
    private static Log log = LogFactory.getLog(UserBean.class);
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
        User u = getUser(userid);
        // If we succeeded:
        if( u != null ) {
            this.firstname = u.getFirstName();
            this.lastname = u.getLastName();
            this.fullname = u.getFullName();
            this.email = u.getEmail();
            this.telephone = u.getPhoneNumber();
            
            // For now, we must format the address manually.
            // FIXME The Address entity should do this.
            this.address = "";
            
            if( u.getAddress().getAddress() != null && ! "".equals( u.getAddress().getAddress().trim() ) ) 
                this.address += u.getAddress().getAddress() + ",\n";
            
            if( u.getAddress().getCity() != null && ! "".equals( u.getAddress().getCity().trim() ) ) 
                this.address += u.getAddress().getCity() + ",\n";
            
            if( u.getAddress().getProvince() != null && ! "".equals( u.getAddress().getProvince().trim() ) ) {
                this.address += u.getAddress().getProvince();
                if( u.getAddress().getPostalCode() != null && ! "".equals( u.getAddress().getPostalCode().trim() ) )
                    this.address += " ";
            }
            
            if( u.getAddress().getPostalCode() != null && ! "".equals( u.getAddress().getPostalCode().trim() ) ) 
                this.address += u.getAddress().getPostalCode();
            
            if( (u.getAddress().getProvince() != null && ! "".equals( u.getAddress().getProvince().trim() )) || 
                   (u.getAddress().getPostalCode() != null && ! "".equals( u.getAddress().getPostalCode().trim() ) ) ) 
                this.address += ",\n";
            
            if( u.getAddress().getCountry() != null && ! "".equals( u.getAddress().getCountry().trim() ) )
                this.address += u.getAddress().getCountry();
            
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
    public static UserManager getUserManager(){
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
     * Look up a User, by username:
     */
    public static User getUser(String username) {
        
        UserManager um = UserBean.getUserManager();
        if( um == null ) {
            log.error("Could not get the User Manager!");
            return null ;
        }
        User u = null;
        try {
            u = um.getUserByUsername(username);
        } catch( UserNotFoundException e ){
            log.error("Exception while attempting to load the User details for '"+username+"': "+e);
            if( log.isDebugEnabled() ) e.printStackTrace();
        }
        
        return u;
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
