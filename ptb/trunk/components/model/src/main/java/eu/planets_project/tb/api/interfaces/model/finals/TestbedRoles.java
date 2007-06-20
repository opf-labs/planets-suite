/**
 * 
 */
package eu.planets_project.TB.api.interfaces.model.finals;

import java.util.Iterator;

/**
 * @author alindley
 * 
 */
public interface TestbedRoles {

	// Available Context of TestbedRoles
	public final int CONTEXT_GENERAL_ROLES = 100;

	public final int CONTEXT_EXPERIMENT_ROLES = 200;

	// general Testbed roles
	public final int TESTBED_READER = 0;

	public final int TESTBED_PLANETS_USER = 1;

	public final int TESTBED_EXTERNAL_USER = 2;

	public final int TESTBED_ADMINISTRATOR = 3;

	// in the context of an experiment
	public final int TESTBED_EXPERIMENTER = 5;

	public final int TESTBED_EXPERIMENT_INVOLVED = 6;

	/**
	 * This method returns the role's names. e.g. "TESTBED_READER" (String) for
	 * a given context See final int statements CONTEXT_GENERAL_ROLES and
	 * CONTEXT_EXPERIMENT_ROLES
	 * 
	 * @param iContext
	 *            context can either be 100 for "general Testbed roles" or 200
	 *            for all provided roles in an experiment context
	 * @return
	 */
	public Iterator<String> getAlLAvailableTestbedRoles(int iContext);

	/**
	 * This method returns the role's names. e.g. "TESTBED_READER" (String)
	 * 
	 * @param iContext
	 *            context can either be 100 for "general Testbed roles" or 200
	 *            for all provided roles in an experiment context
	 * @return
	 */
	public Iterator<String> getAllAvailableTestbedRoles();

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
