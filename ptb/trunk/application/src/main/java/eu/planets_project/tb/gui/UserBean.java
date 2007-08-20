package eu.planets_project.tb.gui;

import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

/**
 * UserBean.java serves as the POJO for storing information about a Testbed User.
 */


public class UserBean 
{
	private Log log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");
  private String firstname;
  private String lastname;
  private String email;
  private String userid;
  private String password;
  private boolean isLoggedIn;


  public UserBean()
  {
  }

  public UserBean(String firstName, String lastName, String email, String userid, String password)
  {
    this.setFirstname(firstName);
    this.setLastname(lastName);
    this.setEmail(email);
    this.setUserid(userid);
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
  }

  public String getUserid() {
    return userid;
  }
  
  public boolean isAdmin() {
  	FacesContext context = FacesContext.getCurrentInstance();
  	Object request = context.getExternalContext().getRequest();
  	boolean result = false;
		result=((HttpServletRequest)request).isUserInRole("TestbedAdmin");
		System.out.println("user " + ((HttpServletRequest)request).getRemoteUser() +" is admin? - " + result);
		log.debug("user " + ((HttpServletRequest)request).getRemoteUser() +" is admin? - " + result);
		return result;  
  }
  
  public boolean isExperimenter() {
  	FacesContext context = FacesContext.getCurrentInstance();
  	Object request = context.getExternalContext().getRequest();
  	boolean result = false;
		result=((HttpServletRequest)request).isUserInRole("Experimenter");
		System.out.println("user " + ((HttpServletRequest)request).getRemoteUser() + " is experimenter? - " + result);
		log.debug("user " + ((HttpServletRequest)request).getRemoteUser() + " is experimenter? - " + result);
		return result;  
  }
  
  public boolean isReader() {
	  	FacesContext context = FacesContext.getCurrentInstance();
	  	Object request = context.getExternalContext().getRequest();
	  	boolean result = false;
			result=((HttpServletRequest)request).isUserInRole("TestbedReader");
			System.out.println("user " + ((HttpServletRequest)request).getRemoteUser() +" is reader? - " + result);
			log.debug("user " + ((HttpServletRequest)request).getRemoteUser() +" is reader? - " + result);
			return result;  
  }  
  
}
