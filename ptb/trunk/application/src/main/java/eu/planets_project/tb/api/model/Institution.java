package eu.planets_project.tb.api.model;

public interface Institution {
	
	public long getInstitutionID();
	public int getInstitutionTypeID();
	/**
	 * e.g. PLANETS_TYPE_LIBRARY
	 * @return the institution's type as defined in model.finals.PlanetsInstitutions
	 */
	public String getInstitutionTypeName();
	public void setInstitutionType(int iTypeID);
	
	public void setInstitution(int iInstID, int iInstTypeID);
	public void setInstitution(int iInstID);

	public boolean checkInstitutionAllowed(int iInstID);
	public boolean checkInstitutionTypeAllowed(int typeID); 
	
	public String getInstitutionName();
	public String getInstitutionAddress();
	
	public User getPrimaryContact();
	public void setPrimaryContact(User user);
	public void setPrimaryContact(long lUserID);


}
