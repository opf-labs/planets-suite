package eu.planets_project.tb.api.model;

import java.util.List;
import java.util.Map;

/**
 * @author alindley
 *
 */
public interface BasicProperties{
	
	public void setExperimentName(String sName);
	public String getExperimentName();
	
	/**
	 * Allows to specify experiments that were an influence, starting point, etc. for this current one.
	 * @param sRefIDs experimentID
	 */
	public void setExperimentReferences(List<Long> sRefIDs);
	public void setExperimentReference(long sRefID);
	public void addExperimentReference(long sRefID);
	public void removeExperimentReference(long sRefID);
	public void setExperimentReference(Experiment refExp);
	public void setExperimentReferences(Experiment[] refExps);
	public List<Long> getExperimentReferences();
	public Map<Long,Experiment> getReferencedExperiments();
	public List<Long> getReferencedExperimentIDs();
	
	public void setSummary(String sSummary);
	public String getSummary();
	
	/**
	 * Should default to the current user's information
	 * @param sName
	 * @param sMail
	 * @param sAddress
	 */
	public void setContact(String sName, String sMail, String sTel, String sAddress);
	public String getContactName();
	public String getContactMail();
	public String getContactTel();
	public String getContactAddress();
	
	public void setPurpose(String sPurpose);
	public String getPurpose();
	
	public void setSpecificFocus(String sFocus);
	public String getSpecificFocus();
	
	public void setIndication(String sDescription);
	public String getIndication();
	
	/**
	 * This method characterizes the MIME-Types this experiment is all about.
	 * @param sMimeType: format string/string is checked
	 */
	public void setExperimentedObjectType(String sMimeType);
	public void setExperimentedObjectTypes(List<String> sMimeTypes);
	public List<String> getExperimentedObjectTypes();
	
	public void setFocus(String sFocus);
	public String getFocus();

	public void setScope(String sScope);
	public String getScope();

	public void setExperimenter(String sUserID);
	
	public void addInvolvedUser(String sUserID);
	public void removeInvolvedUser(String sUserID);
	
	public void addInvolvedUsers(List<String> usersIDs);
	public void removeInvolvedUsers(List<String> userIDs);
	
	public List<String> getInvolvedUserIds();
	
	/**
	 * A user may take a seperate role (besides his overall Testbed role) for an
	 * experiment. E.g. he/she could beReader, but within the given context of a certain
	 * Experiment he/she may act as Experimenter.
	 * @param hUserIDsAndExperimentRoles Hashtable<userID,roleID>
	 * @see eu.planets_project.TB.data.model.finals.TestbedRoles
	 */
//	public void addInvolvedUsersWithSpecialExperimentRole(HashMap<Long,Vector<Integer>> hmUserIDsAndExperimentRoles);
//	public void removeInvolvedUsersAndSpecialExperimentRole(HashMap<Long,Vector<Integer>> hmUserIDsAndExperimentRoles);
	/**
	 * Sets the approache's ID.
	 * 0..for migration
	 * 1..for emulation
	 * @param iID
	 */
	public void setExperimentApproach(int iID);
	/**
	 * Returns the approache's ID.
	 * 0..for migration
	 * 1..for emulation
	 * @return
	 */
	public int getExperimentApproach();
	/**
	 * Returns the corresponding name for a given ID.
	 * 0.."migration"
	 * 1.."emulation"
	 * @param iID ExperimentApproach ID.
	 * @return "migration", "emulation" or null
	 */
	public String getExperimentApproach(int iID);
	
	public void setConsiderations(String sConsid);
	public String getConsiderations();
}
