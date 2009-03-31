package eu.planets_project.tb.gui.backing;

import javax.faces.context.FacesContext;

import eu.planets_project.ifr.core.common.mail.PlanetsMailMessage;
import eu.planets_project.tb.gui.backing.admin.RegisterTBServices;
import eu.planets_project.tb.gui.backing.admin.ManagerTBServices;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.gui.backing.exp.AutoBMGoalEvalUserConfigBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andrew Lindley, ARC
 * TestbedManager class
 *  - init all backing beans
 *  - get handle on registered managed beans
 */

public class Manager {
    
	private Log log = LogFactory.getLog(Manager.class);
    
    public Manager() {
    }
    
    public String initExperimentAction() {
		ExperimentBean expBean = new ExperimentBean();
		// Put Bean into Session; accessible later as #{ExperimentBean}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	    
		//every new experiment requires a new ontologyDnDBean
		this.initOntologDnDBean();
		
		return "success";
    }
    
    
    /**
     * @author alindley
     * (re)Init and register an admin backing bean (scope = session) for the 
     * wizzard: register_TBServices
     */
    public String initRegisterTBService(){
    	RegisterTBServices regTBSer = new RegisterTBServices();
		// Put Bean into Session; accessible later as #{ExperimentBean}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("RegisterTBServiceBean", regTBSer);
	    return "register_TBservices";
    }
    
    /**
     * @author alindley
     * (re)Init and render the content of the TBServiceRegistry for displaying 
     * all registered services and their metadata on the screen
     * overview_registeredTBServices.xhtml
     * @return
     */
    public String initTBServiceDisplayer(){
    	ManagerTBServices renderer = new ManagerTBServices();
		// Put Bean into Session; accessible later as #{TBServiceRenderer}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("TBServiceRenderer", renderer);
	    return "render_TBservices";
    }
    
	/**
	 * @author alindley
	 * Helper to fetch the current WSClientBean from the session
	 * @return
	 */
	public WSClientBean getCurrentWSClientBean(){
		return (WSClientBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("WSClientBean"); 
	}
	
	
    /**
     * @author alindley
     * (re)Init and manage the content of the TBServiceRegistry for removing
     * services and rendering their metadata on the screen
     * remove_registeredTBServices.xhtml
     * @return
     */
    public String initTBServiceManager(){
    	ManagerTBServices manager = new ManagerTBServices();
		// Put Bean into Session; accessible later as #{TBServiceRenderer}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("TBServiceManager", manager);
	    return "manage_TBservices";
    }
    
    /**
     * @author alindley
     * (re)Init and manage the content of the TBServiceRegistry for removing
     * services and rendering their metadata on the screen
     * remove_registeredTBServices.xhtml
     * @return
     */
    public String initTBServiceImporter(){
    	ManagerTBServices manager = new ManagerTBServices();
		// Put Bean into Session; accessible later as #{TBServiceRenderer}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("TBServiceManager", manager);
	    return "import_TBservices";
    }
    
    /**
     * Used to reinit the session-scoped managed object with a specific AutoBMGoalEvalUserConfigBean
     * @param evalConfBean
     */
    public void reinitAutoBMGoalEvalUserConfigBean(AutoBMGoalEvalUserConfigBean evalConfBean){
		//BenchmarkBean contains the BMGoal+EvaluationSerTemplate to configure
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("AutoEvalSerUserConfigBean", evalConfBean);
    }
    
    /**
     * Used to (re)init the session-scoped managed object simpleTreeDnDBean for the ontology browser
     * @return
     */
    public static String initOntologDnDBean(){
    	FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("simpleTreeDndBean", new PropertyDnDTreeBean());
		return "browse_ontology";
    }
    
    /* --- Test user email sending... */
    
    private String emailTestAddress = "???";
    private String emailTestSubject = "This is a test email.";
    private String emailTestMessage = "This is a test email, send from the Testbed.";
    private String testEmailResult = "";

    /**
     * @return the emailTestAddress
     */
    public String getEmailTestAddress() {
        return emailTestAddress;
    }

    /**
     * @param emailTestAddress the emailTestAddress to set
     */
    public void setEmailTestAddress(String emailTestAddress) {
        this.emailTestAddress = emailTestAddress;
    }

    /**
     * @return the emailTestSubject
     */
    public String getEmailTestSubject() {
        return emailTestSubject;
    }

    /**
     * @param emailTestSubject the emailTestSubject to set
     */
    public void setEmailTestSubject(String emailTestSubject) {
        this.emailTestSubject = emailTestSubject;
    }

    /**
     * @return the emailTestMessage
     */
    public String getEmailTestMessage() {
        return emailTestMessage;
    }

    /**
     * @param emailTestMessage the emailTestMessage to set
     */
    public void setEmailTestMessage(String emailTestMessage) {
        this.emailTestMessage = emailTestMessage;
    }
    
    /**
     * @return the testEmailResult
     */
    public String getTestEmailResult() {
        return testEmailResult;
    }

    /**
     * @param testEmailResult the testEmailResult to set
     */
    public void setTestEmailResult(String testEmailResult) {
        this.testEmailResult = testEmailResult;
    }

    /**
     * 
     */
    public String sendTestEmail() {
        log.info("Testing email configuration, with an email to: "+this.emailTestAddress);
        /*
        UserManager um = UserBean.getUserManager();
        um.sendUserMessage(username, this.emailTestSubject, this.emailTestMessage)
        */
        try {
            // Send a message.
            PlanetsMailMessage mailer = new PlanetsMailMessage();
            //mailer.setSender("noreply@planets-project.eu");
            mailer.setSubject(this.emailTestSubject);
            mailer.setBody(this.emailTestMessage);
            mailer.addRecipient(this.emailTestAddress);
            mailer.send();
            this.setTestEmailResult("You email appears to have been successfully sent. Please check the logs.");
            log.info("Email sent successfully.");
        } catch( Exception e ) {
            this.setTestEmailResult("Sending email failed, with exception: "+e);
            log.info("Email sending failed.");
        }
        return "success";
    }

}
