/**
 * The use of either TESTBED_ROLE_PLANETS_USER or TESTBED_ROLE_PLANETS_EXTERNAL_USER is mandatory
 */
package eu.planets_project.tb.api.model.finals;

import java.util.List;

/**
 * @author alindley
 * 
 */
public interface TestbedRoles {
	
	//TODO: Final set of initial TestbedRoles need to be configured

	// Available Context of TestbedRoles: keyword: CONTEXT
	public final int CONTEXT_GENERAL_ROLE 			= 200;
	public final int CONTEXT_EXPERIMENT_ROLE 		= 300;

	// general Testbed roles: keyword: TESTBED_ROLE
	public final int TESTBED_ROLE_READER 					= 0;
	public final int TESTBED_ROLE_PLANETS_USER 				= 1;
	public final int TESTBED_ROLE_PLANETS_EXTERNAL_USER 	= 2;
	public final int TESTBED_ROLE_ADMINISTRATOR 			= 3;
	public final int TESTBED_ROLE_APPROVER					= 4;

	// in the context of an experiment
	public final int TESTBED_ROLE_EXPERIMENTER 			= 5;
	public final int TESTBED_ROLE_EXPERIMENT_INVOLVED 	= 6;
	
	// mapping of Context_Type_Roles: keyword: MAPPING_+TESTBED_ROLE
	public final int MAPPING_TESTBED_ROLE_READER 				= CONTEXT_GENERAL_ROLE;
	public final int MAPPING_TESTBED_ROLE_PLANETS_USER			= CONTEXT_GENERAL_ROLE;
	public final int MAPPING_TESTBED_ROLE_EXTERNAL_ROLE_USER 	= CONTEXT_GENERAL_ROLE;
	public final int MAPPING_TESTBED_ROLE_ADMINISTRATOR  		= CONTEXT_GENERAL_ROLE;
	public final int MAPPING_TESTBED_ROLE_APPROVER				= CONTEXT_GENERAL_ROLE;
	public final int MAPPING_TESTBED_ROLE_EXPERIMENTER 			= CONTEXT_EXPERIMENT_ROLE;
	public final int MAPPING_TESTBED_ROLE_EXPERIMENT_INVOLVED 	= CONTEXT_EXPERIMENT_ROLE;
	

	/**
	 * This method returns the role's names. e.g. "CONTEXT_GENERAL_TESTBED_READER" (String) for
	 * a given context See final int statements CONTEXT_GENERAL_ROLE and
	 * CONTEXT_EXPERIMENT_ROLES
	 * 
	 * @param iContext
	 *            context can either be 100 for "general Testbed roles" or 200
	 *            for all provided roles in an experiment context
	 * @return
	 */
	public List<String> getAlLAvailableRoleNames(int iContext);

	/**
	 * This method returns the role's names. e.g. "TESTBED_READER" (String)
	 * 
	 * @param iContext
	 *            context can either be 100 for "general Testbed roles" or 200
	 *            for all provided roles in an experiment context
	 * @return
	 */
	public List<String> getAllAvailableRoleNames();
	
	/**
	 * This method returns a list with all available role IDs
	 * @return
	 */
	public List<Integer> getAllAvailableRoleIDs();
	
	/**
	 * This method can be used to check if a given RoleID is a valid Role
	 * @param iInputRoleID
	 * @return
	 */
	public boolean checkRoleIDisValid(int iInputRoleID);
	
	/**
	 * This method can be used to check if a given RoleID is a valid Role for a given Context
	 * @param iInputRoleID
	 * @param icontext
	 * @return
	 */
	public boolean checkRoleIDisValid(int iInputRoleID, int icontext);
	
	/**
	 * @return
	 */
	public List<String> getAllAvailableContextNames();
	
	public List<Integer> getAllAvailableContextIDs();

	/**
	 * @param sRoleName
	 *            full corresponding variable name. e.g. TESTBED_ADMINISTRATOR
	 * @return the role's ID.
	 */
	public int getRoleID(String sRoleName);

	/**
	 * @param iRoleID
	 * @return full corresponding variable name. e.g. TESTBED_ADMINISTRATOR
	 */
	public String getRoleName(int iRoleID);
	
	
}
