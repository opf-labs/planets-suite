/**
 * 
 */
package eu.planets_project.tb.impl;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;


import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.mail.PlanetsMailMessage;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.impl.system.BackendProperties;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * @author alindley
 *
 */
public class AdminManagerImpl implements AdminManager {
    private static PlanetsLogger log = PlanetsLogger.getLogger(AdminManagerImpl.class, "testbed-log4j.xml");

	private static AdminManagerImpl instance;
	
    public static final String IDENTIFY = "identify";
    public static final String VALIDATE = "validate";
    public static final String CHARACTERISE = "characterise";
    public static final String MIGRATE = "migrate";
    public static final String EMULATE = "migrate";
	
	//e.g. key:"identify" -> "Identify"
	private static HashMap<String,String> hmExperimentTypes;
	// No longer read from XML, by statically coded:
	static {
	    hmExperimentTypes = new HashMap<String,String>();
        hmExperimentTypes.put(IDENTIFY, "Identify");
        hmExperimentTypes.put(MIGRATE, "Migrate");
        //hmExperimentTypes.put(VALIDATE, "Validate");
        //hmExperimentTypes.put(CHARACTERISE, "Characterise");
        //hmExperimentTypes.put(EMULATE, "View in Emulator");
	}

    private static HashMap<String,String> hmOldExperimentTypes;
    static {
        hmOldExperimentTypes = new HashMap<String,String>();
        hmOldExperimentTypes.put("experimentType.simpleMigration","simple migration");
        hmOldExperimentTypes.put("experimentType.simpleCharacterisation", "simple characterisation");
    }

	private AdminManagerImpl(){
		// Also read basic properties:
		BackendProperties bp = new BackendProperties();
		APPROVAL_THRESHOLD_NUMBER_OF_INPUTS = bp.getExpAdminNoInputs();
		log.info("Set number of inputs before admin approval required at: "+APPROVAL_THRESHOLD_NUMBER_OF_INPUTS);
    }
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized AdminManagerImpl getInstance(){
		if (instance == null){
			instance = new AdminManagerImpl();
		}
		return instance;
	}
	
	/* ----------------------------------------------------------- */
	
	public Collection<String> getExperimentTypeIDs() {
		return hmExperimentTypes.keySet();
	}

	public Collection<String> getExperimentTypesNames() {
		return hmExperimentTypes.values();
	}
	
    public Map<String, String> getExperimentTypeIDsandNames() {
        return hmExperimentTypes;
    }

	public String getExperimentTypeID(String expTypeName) {
        if(hmExperimentTypes.containsValue(expTypeName)) {
            return getKeyFromHashMap(expTypeName, hmExperimentTypes);
        }
        if(hmOldExperimentTypes.containsValue(expTypeName)) {
            return getKeyFromHashMap(expTypeName, hmOldExperimentTypes);
        }
		return null;
	}
	
	public String getExperimentTypeName(String typeID) {
        if(hmExperimentTypes.containsKey(typeID)){
            return hmExperimentTypes.get(typeID);
        }
        if(hmOldExperimentTypes.containsKey(typeID)){
            return hmOldExperimentTypes.get(typeID);
        }
		return null;
	}

    private String getKeyFromHashMap( String expTypeName, HashMap<String,String> experimentTypes ) {
        Iterator<String> itKeys = experimentTypes.keySet().iterator();
        while(itKeys.hasNext()){
            String sKey = itKeys.next();
            if(experimentTypes.get(sKey).equals(expTypeName)){
                return sKey;
            }
        }
        return null;
    }
    
    /**
     * @param etype
     * @return
     */
    public boolean isDeprecated(String typeID) {
        if(hmOldExperimentTypes.containsKey(typeID)){
            return true;
        }
        return false;
    }
    


	/**
	 * Code for Experiment Approval:
	 */
	
    /**
     * Threshold for the number of inputs before approval is required.
     * Can be overridden from BackendProperties. See constructor.
     */
    public static int APPROVAL_THRESHOLD_NUMBER_OF_INPUTS = 0;
    
    /**
     * Decision flags:
     */
    public static final String APPROVAL_DECISION_AWAITING = null;
    public static final String APPROVAL_DECISION_APPROVED = "Approved";
    public static final String APPROVAL_DECISION_DENIED = "Denied";
    public static final String APPROVAL_AUTOMATIC_USER = "{automatic}";

    /**
     * Does this experiment require administrator approval?
     * @param exp The Experiment to evaluate.
     * @return TRUE if experimental approval is required to execute it, false in all other cases.
     */
    public static boolean experimentRequiresApproval(Experiment exp) {
        if( exp == null ) return false;
        if( exp.getExperimentExecutable() == null ) return false;
        
        // Go thru reasons for requiring approval:
        if( exp.getExperimentExecutable().getInputData() != null && 
                exp.getExperimentExecutable().getInputData().size() > APPROVAL_THRESHOLD_NUMBER_OF_INPUTS ) {
            // Requires Approval:
            return true;
        }
        // Otherwise, approve the experiment:
        return false;
    }
    
    public static void requestExperimentApproval(Experiment exp) {
        // Check if approval is require:
        if( ! experimentRequiresApproval(exp) ) return;

        // Store the 'Asking for approval' status as a NULL decision:
        exp.getExperimentApproval().setDecision(APPROVAL_DECISION_AWAITING);
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.updateExperiment(exp);
        
        // Mail the administrator:
        sendApprovalRequest(exp);
        log.info("The experiment '"+exp.getExperimentSetup().getBasicProperties().getExperimentName()+"' requires administrator approval.");
    }
    
    public static void approveExperimentAutomatically(Experiment exp) {
        if( exp == null ) return;
        if( exp.getExperimentExecutable() == null );
        
        exp.getExperimentApproval().setExplanation("Experiment was approved automatically.");
        exp.getExperimentApproval().setDecision(APPROVAL_DECISION_APPROVED);
        exp.getExperimentApproval().setGo(true);
        exp.getExperimentApproval().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
        exp.getExperimentApproval().addApprovalUser(APPROVAL_AUTOMATIC_USER);
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.updateExperiment(exp);
        
        log.info("The experiment '"+exp.getExperimentSetup().getBasicProperties().getExperimentName()+"' was automaticallu approved for execution.");
    }
    
    public static void approveExperimentManually(Experiment exp) {
        if( exp == null ) return;
        if( exp.getExperimentExecutable() == null ) return;

        // Find out who is responsible:
        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
        
        // Approve it
        if( exp.getExperimentApproval().getExplanation() == null ||
                "".equals(exp.getExperimentApproval().getExplanation()))
            exp.getExperimentApproval().setExplanation("Experiment was approved for execution.");
        exp.getExperimentApproval().setDecision(APPROVAL_DECISION_APPROVED);
        exp.getExperimentApproval().setGo(true);
        exp.getExperimentApproval().addApprovalUser(currentUser.getUserid());
        exp.getExperimentApproval().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.updateExperiment(exp);
        
        // Mail the user:
        sendApprovalNotice(exp);
        log.info("The experiment '"+exp.getExperimentSetup().getBasicProperties().getExperimentName()+"' was approved for execution.");
    }
    
    public static void denyExperimentManually(Experiment exp) {
        if( exp == null ) return;
        if( exp.getExperimentExecutable() == null ) return;
        
        // Find out who is responsible:
        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
        
        // Deny approval
        if( exp.getExperimentApproval().getExplanation() == null ||
                "".equals(exp.getExperimentApproval().getExplanation()))
            exp.getExperimentApproval().setExplanation("Experiment was denied approval for execution.");
        exp.getExperimentApproval().setDecision(APPROVAL_DECISION_DENIED);
        exp.getExperimentApproval().setGo(false);
        exp.getExperimentApproval().addApprovalUser(currentUser.getUserid());
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.updateExperiment(exp);
        
        // Mail the user:
        sendDenialNotice(exp);
        log.info("The experiment '"+exp.getExperimentSetup().getBasicProperties().getExperimentName()+"' was denied approval for execution.");
    }
    
    public static boolean experimentAwaitingApproval( Experiment exp ) {
        if( exp.getCurrentPhasePointer() != ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        if( exp.getExperimentApproval().getDecision() == null ) return true;
        if( "".equals(exp.getExperimentApproval().getDecision()) ) return true;
        return false;
    }
    
    public static boolean experimentWasApproved( Experiment exp ) {
        if( exp.getCurrentPhasePointer() <= ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        if( APPROVAL_DECISION_APPROVED.equals( exp.getExperimentApproval().getDecision()) ) return true;
        return false;
    }
    
    public static boolean experimentWasDenied( Experiment exp ) {
        if( exp.getCurrentPhasePointer() != ExperimentPhase.PHASE_EXPERIMENTAPPROVAL ) return false;
        return !experimentWasApproved(exp);
    }
    
    public static void toEditFromDenied(Experiment exp) {
        if( exp == null ) return;
        if( exp.getExperimentExecutable() == null );
        
        exp.getExperimentApproval().setExplanation("");
        exp.getExperimentApproval().setDecision("");
        exp.getExperimentApproval().setGo(false);
        exp.getExperimentSetup().setState(Experiment.STATE_IN_PROGRESS);
        exp.getExperimentSetup().setSubStage(ExperimentSetup.SUBSTAGE3);
        exp.getExperimentApproval().setState(Experiment.STATE_NOT_STARTED);
        exp.getExperimentExecution().setState(Experiment.STATE_NOT_STARTED);
        List<String> approvalUsers = exp.getExperimentApproval().getApprovalUsersIDs();
        // Need to clone to avoid a 'java.util.ConcurrentModificationException':
        List<String> usersToRemove = new ArrayList<String>();
        for( String user : approvalUsers ) usersToRemove.add(user);
        exp.getExperimentApproval().removeApprovalUsers(usersToRemove);
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.updateExperiment(exp);
        
        log.info("The experiment '"+exp.getExperimentSetup().getBasicProperties().getExperimentName()+"' was made editable again.");
    }
    
    /**
     * Code for emails related to Approval.
     * 
     */
    private static void sendNotification(String username, String templateName, Experiment exp ) {
        
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("velocimacro.library", "");
        try {
            velocityEngine.init(props);
        } catch( Exception e ) {
            log.error("Failed to initialise the Velocity engine. :: " + e );
        }
        
        // Look up the user.
        User user = UserBean.getUser(username);
        if( user == null ) return;
        
        // Create a message:
        PlanetsMailMessage message = new PlanetsMailMessage();
        
        // Add the recipient in properly.
        message.addRecipient(user.getFullName() + "<" + user.getEmail() + ">");
        
        // Determine the Testbed URL:
        String testbedURL = "http://"+AdminManagerImpl.getAuthority()+"/testbed/";

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("user", user);
        model.put("exp", exp);
        model.put("expName", exp.getExperimentSetup().getBasicProperties().getExperimentName());
        model.put("applicationURL", testbedURL);
        
        VelocityContext velocityContext;
        StringWriter result = new StringWriter();
        try {
            velocityContext = new VelocityContext(model);
            velocityEngine.mergeTemplate("eu/planets_project/tb/"+templateName+".vm", velocityContext, result);
        } catch  (VelocityException ex) {
            log.error("Mailing failed! :: "+ex);
            return;
        } catch  (RuntimeException ex) {
            log.error("Mailing failed! :: "+ex);
            return;
        } catch  (Exception ex) {
            log.error("Mailing failed! :: "+ex);
            return;
        }
        message.setSubject(velocityContext.get("subject").toString());
        message.setBody(result.toString());
        
        try {
            message.send();
        } catch( Exception e ) {
            log.error("An error occured while trying to send an email to "+user.getFullName()+"! :: "+e);
            e.printStackTrace();
        }
    }
    
    private static void sendApprovalRequest(Experiment exp) {
        sendNotification("admin", "RequestApproval", exp);
    }

    private static void sendApprovalNotice(Experiment exp) {
        if( exp.getExperimentSetup().getBasicProperties().getInvolvedUserIds() == null ) return;
        String username = exp.getExperimentSetup().getBasicProperties().getInvolvedUserIds().get(0);
        if( username == null ) return;
        sendNotification(username, "ApprovalGranted", exp );
    }

    private static void sendDenialNotice(Experiment exp) {
        if( exp.getExperimentSetup().getBasicProperties().getInvolvedUserIds() == null ) return;
        String username = exp.getExperimentSetup().getBasicProperties().getInvolvedUserIds().get(0);
        if( username == null ) return;
        sendNotification(username, "ApprovalDenied", exp);
    }

    /**
     * Helper function that looks up the actual authority for this server.
     * Could also be done via the DR I think.
     * @return The authority in the form 'server:port'.
     */
    public static String getAuthority() {
        return PlanetsServerConfig.getHostname() + ":" + PlanetsServerConfig.getPort();
    }

    /**
     * @param selectedExperiment
     * @return
     */
    public static boolean isExperimentDeprecated(Experiment exp) {
        log.info("Checking if "+exp.getExperimentSetup().getExperimentTypeID()+" is a deprecated type");
        if( AdminManagerImpl.hmOldExperimentTypes.containsKey(
                exp.getExperimentSetup().getExperimentTypeID() ) ) {
            return true;
        }
        return false;
    }

}
