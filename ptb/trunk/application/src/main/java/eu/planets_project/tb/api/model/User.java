/**
 * This class is built upon the assumption that a given User may have multiple (overlapping) roles.
 */
package eu.planets_project.tb.api.model;

import java.util.Vector;

/**
 * @author alindley
 *
 */
public interface User{
	
	public void setUserDetails(String sForename, String Surname);
	public String getForename();
	public String getSurname();
	public String getName();
	
	public void setContactInformation(String sEmail, String sTelNr, String sAddress);
	public String getEmail();
	public String getTelNr();
	public String getAddress();
	
	/**
	 *
	 * @see eu.planets_project.TB.interfaces.model.finals.TestbedRoles
	 */
	public void setRole(int iRole);
	public void setRoles(Vector<Integer> iRoles);
	public void addRoles(Vector<Integer> iRoles);
	public void removeRoles(Vector<Integer> iRoles);
	public Vector<String> getRoleNames();
	public Vector<Integer> getRolesIDs();
	
	public boolean isExperimenter();
	public boolean isAdministrator();
	public boolean isReader();
	
	public void setInstitution(Institution inst);
	public Institution getInstitution();
	

	
	/**
	 * Used to declare if this user is Planets internal or not.
	 * @param internal planets-internal=true, external=false
	 */
	public void setPlanetsInternalUser(boolean internal);
	public boolean isPlanetsInternalUser();
	
	public void setPassword(String sPassword);
	public String getPassword();
	public boolean checkPassword(String sPassword);
	public void setPasswordRetrievalHint(String sQuestion, String sAnswer);
	
	public long getUserID();
}
