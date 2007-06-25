package eu.planets_project.tb.api.model;

import eu.planets_project.tb.api.model.finals.TestbedRoles;

/**
 * @author alindley
 *
 */
public interface User extends TestbedRoles{
	
	public void setUserDetails(String sForename, String Surname);
	public String getForename();
	public String getSurname();
	
	public void setContactInformation(String sEmail, String sTelNr);
	public String getEmail();
	public String getTelNr();
	
	/**
	 *
	 * @see eu.planets_project.TB.interfaces.model.finals.TestbedRoles
	 */
	public void setRole(int iRole);
	public void setRoles(int[] iRoles);
	public void addRoles(int[] iRoles);
	public void removeRoles(int[] iRoles);
	public String[] getRoleNames();
	public int[] getRolesID();
	public boolean isExperimenter();
	
	public void setInstitution(int iInstID, int iInstTypeID);
	public int getInstitutionID();
	public String getInstitutionName();
	
	public int getInstitutionTypeID();
	/**
	 * e.g. PLANETS_TYPE_LIBRARY
	 * @return the institution's type as defined in model.finals.PlanetsInstitutions
	 */
	public String getInstitutionType();
	
	/**
	 * Used to declare if this user is Planets internal or not.
	 * @param internal planets-internal=true, external=false
	 */
	public void setPlanetsInternalUser(boolean internal);
	public boolean isPlanetsInternalUser();
	
	public void setPassword(String sPassword);
	public String getPassword();
	public boolean checkPassword(String sPassword);
	
	public long getUserID();
	/**
	 * @see ExperimentApproval
	 */
	public void setBean(User bean);
	/**
	 * @see ExperimentApproval
	 */
	public User getBean();

}
