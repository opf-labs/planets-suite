/**
 * 
 */
package eu.planets_project.tb.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
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
	//e.g. key:"experimentType.simpleMigration" value:"simple migration"
	private HashMap<String,String> hmExperimentTypes;
	
	private AdminManagerImpl(){
		// Read properties file.
		hmExperimentTypes = readExperimentTypes();
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
	
	
	/**
	 * Fetches the BackendResources.properties file to read all supported "ExperimentTypes".
	 * @return
	 */
	private HashMap<String,String> readExperimentTypes(){
		HashMap<String,String> hmRet = new HashMap<String,String>();
		Properties properties = new Properties();
	    try {
	        java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
	        properties.load(ResourceFile); 
	        
	        Iterator<Object> itKeys = properties.keySet().iterator();
	        while(itKeys.hasNext()){
	        	String key = (String)itKeys.next();
	        	if(key.startsWith("experimentType")){
	        		//e.g. key: "experimentType.simpleMigration" value: "simple migration"
	        		hmRet.put(key, properties.getProperty(key));
	        	}
	        }
	        
	        ResourceFile.close();
	        
	    } catch (IOException e) {
	    	//TODO add logg statement
	    	System.out.println("readExperimentTypes BackendResources failed!");
	    }
	    return hmRet;
	}

	public Collection<String> getExperimentTypeIDs() {
		return this.hmExperimentTypes.keySet();
	}

	public Collection<String> getExperimentTypesNames() {
		return this.hmExperimentTypes.values();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.AdminManager#getExperimentTypeID(java.lang.String)
	 */
	public String getExperimentTypeID(String expTypeName) {
		if(this.hmExperimentTypes.containsValue(expTypeName)){
			Iterator<String> itKeys = this.hmExperimentTypes.keySet().iterator();
			while(itKeys.hasNext()){
				String sKey = itKeys.next();
				if(this.hmExperimentTypes.get(sKey).equals(expTypeName)){
					return sKey;
				}
			}
		}
		return null;
	}

	public String getExperimentTypeName(String typeID) {
		if(this.hmExperimentTypes.containsKey(typeID)){
			return this.hmExperimentTypes.get(typeID);
		}
		return null;
	}

	public Map<String, String> getExperimentTypeIDsandNames() {
		return this.hmExperimentTypes;
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
        // FIXME Alert the admin user.
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
        // FIXME Alert the user.
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
        // FIXME Alert the user.
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

}
