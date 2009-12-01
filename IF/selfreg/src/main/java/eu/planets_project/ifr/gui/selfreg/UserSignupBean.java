package eu.planets_project.ifr.gui.selfreg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.common.mail.PlanetsMailMessage;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.SelfRegistrationManager;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotFoundException;
import eu.planets_project.ifr.core.security.api.services.UserManager.UserNotValidException;
import eu.planets_project.ifr.core.security.impl.model.SelfAddressImpl;
import eu.planets_project.ifr.core.security.impl.model.SelfUserImpl;
import eu.planets_project.ifr.core.security.impl.services.SelfRegistrationManagerImpl;
/**
 * Simple bean backing self sign-up pages
 * @author Klaus Rechert
 *
 */
public class UserSignupBean 
{
	private static Logger log = LogFactory.getLog(UserSignupBean.class.getName());
	
	
	private SelfRegistrationManager selfRegManager = new SelfRegistrationManagerImpl();
	
	private static File cachedir = new File(System.getProperty("java.io.tmpdir"), 
		"planets-tmp-user-cache/");

	private User user;
	private String password;
	private String confirmPassword;

	public UserSignupBean() {
		log.info("UserSignupBean.UserSignupBean()");
		this.user = new SelfUserImpl("");
		this.user.setAddress(new SelfAddressImpl());
		this.user.setAccountEnabled(false);
		password = "";
		confirmPassword = "";
	}

	public User getUser()
	{
		return this.user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * The cancel method simply handles the pressing of the cancel button by the user.
	 * 
	 * @return the canceledit JSF outcome
	 */
	public String cancel() {
		this.user = null;
		return "success";
	}

	/**
	 * Getter for the password
	 * 
	 * @return The password 
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Getter for the confirm password member 
	 * 
	 * @return the confirm password value from the web form
	 */
	public String getConfirmPassword() {
		return this.confirmPassword;
	}

	/**
	 * Setter for the confirm password
	 * 
	 * @param password
	 */
	public void setConfirmPassword(String password) {
		this.confirmPassword = password;
	}

	private String getValidationURL()
	{
		HttpServletRequest request = (HttpServletRequest)
			FacesContext.getCurrentInstance().getExternalContext().getRequest();
		StringBuffer url = new StringBuffer();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		String scheme = request.getScheme();
		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getContextPath());
		return (url.toString() + "/validate.faces");
	}

	private boolean saveUserObject(String uuid)
	{
		log.info("UserSignupBean.saveUserObject()");
		log.info("SAVE USER");
		log.info(user.getUsername());
		try {
			log.info("UserSignupBean.signup() adding the user");
			selfRegManager.addUser(this.user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if (!cachedir.exists()) {
			if (!cachedir.mkdirs()) {
				log.error("failed to create caching dir: " + cachedir);
				return false;
			}
		}

		try 
		{
			File userfile = new File(cachedir, uuid);
			userfile.deleteOnExit();
			FileOutputStream stream = new FileOutputStream(userfile);
			BufferedOutputStream buffer = new BufferedOutputStream(stream);
			ObjectOutputStream objOut = new ObjectOutputStream(buffer);
			objOut.writeObject(user);
			objOut.close();
			log.info("Saved user details to "  + userfile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Method to save the user details
	 * 
	 * @return	An outcome string for use in navigation
	 * 			saved - user was successfully saved
	 * 
	 * @throws	NoSuchAlgorithmException
	 * @throws  UserNotFoundException
	 */
	public String signup() throws NoSuchAlgorithmException 
	{
		log.info("UserSignupBean.signup()");
		this.user.hashPassword(password);
		String uuid = UUID.randomUUID().toString();
		try {
			log.info("UserSignupBean.signup() adding the user");
			selfRegManager.addUser(this.user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!saveUserObject(uuid))
			return "error";

		String subject = "Confirm your PLANETS user account";
		String body = "Please click on the following link to validate your PLANETS user account\n";
		body += getValidationURL() + "?uid="+uuid;
		
		log.info("Sending email to: " + user.getEmail());
		log.info("Subject: " + subject);
		log.info(body);

		PlanetsMailMessage mailer = new PlanetsMailMessage();
                mailer.setSubject(subject);
                mailer.setBody(body);
                mailer.addRecipient(user.getEmail());
                //mailer.send();

		return "success";
	}

	public String getValidationResult()
	{
		String uid = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap().get("uid"); 

		try {
			File userfile = new File(cachedir, uid);
			if(!userfile.exists())
				return "An error occured! Please try again or contact the administrator.";

			FileInputStream stream = new FileInputStream(userfile);
			BufferedInputStream buffer = new BufferedInputStream(stream);
			ObjectInputStream objIn = new ObjectInputStream(buffer);
			user = (User)objIn.readObject();
			objIn.close();
			userfile.delete();
			log.info("validated user " + user.getUsername());
			selfRegManager.addUser(this.user);

			//XXX: send mail to admin?

		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
		return "Your request was verified. Your account will be activated soon!";
	}

	/**
	 * Tests whether the user's password and confirm password are equal
	 * 
	 * @return true if password == confirm password, otherwise false
	 */
	public boolean passwordConfirmed() {
		return (this.confirmPassword.equals(this.password));
	}
		
	/**
	 * 
	 * @param context
	 * @param toValidate
	 * @param value
	 */
	public void validateUsername(FacesContext context, 
			UIComponent toValidate,
			Object value) 
	{
		log.info("UserSignupBean.validateUsername()");
		String username = (String) value;
		boolean avail =  selfRegManager.isUsernameAvailable(username);
		if (avail != true) {
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage("Username not available.");
			context.addMessage(toValidate.getClientId(context), message);
		}
	}

	/**
	 * 
	 * @param context
	 * @param toValidate
	 * @param value
	 */
	public void validateEmail(FacesContext context, 
			UIComponent toValidate,
			Object value) {
		String email = (String) value;
		Pattern patt = Pattern.compile("^[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})$");
		Matcher match = patt.matcher(email);
		if(!match.find()) {
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage("Invalid email address.");
			context.addMessage(toValidate.getClientId(context), message);
		}
	}
}
