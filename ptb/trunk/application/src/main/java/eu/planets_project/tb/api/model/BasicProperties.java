package eu.planets_project.tb.api.model;

import java.util.Hashtable;
import java.util.Iterator;

import eu.planets_project.tb.api.model.finals.PlanetsInstitutions;

/**
 * @author alindley
 *
 */
public interface BasicProperties extends PlanetsInstitutions{
	
	public void setExperimentName(String sName);
	public String getExperimentName();
	
	
	/**
	 * Allows to specify experiments that were an influence, starting point, etc. for this current one.
	 * @param sRefIDs experimentID
	 */
	public void setExperimentReferences(long[] sRefIDs);
	public void setExperimentReference(long sRefID);
	public void setExperimentReference(Experiment refExp);
	public void setExperimentReferences(Experiment[] refExps);
	public Iterator getExperimentReferences();
	public Experiment[] getReferencedExperiments();
	public long[] getReferencedExperimentIDs();
	
	public void setSummary(String sSummary);
	public String getSummary();
	
	/**
	 * Should default to the current user's information
	 * @param sName
	 * @param sMail
	 * @param sAddress
	 */
	public void setContact(String sName, String sMail, String sTel, String sAddress);
	public void setContact(User bean);
	public String getContactName();
	public String getContactMail();
	public String getContactTel();
	public String getContactAddress();
	
	public void setPurpose(String sPurpose);
	public String getPurpose();
	
	public void setSpecificFocus(String sFocus);
	public void getSpecificFocus();
	
	public void setIndication(String sDescription);
	public String getIndication();
	
	public void setExperimentedObjectType(String sMimeType);
	public void setExperimentedObjectTypes(String[] sMimeTypes);
	public String getExperimentedObjectType();
	public Iterator getExperimentedObjectTypes();
	
	public void setFocus(String sFocus);
	public String getFocus();

	public void setScope(String sScope);
	public String getScope();
	
	public void setExperimenter(User experimenter);
	public void setExperimenter(long lUserID);
	
	/**
	 * If no roles in the experiment specific context are set
	 * @param users
	 * @see setInvolvedUsers(Hashtable<userID,Role>)
	 */
	public void setInvolvedUsers(User[] users);
	public void setInvolvedUsers(long lUserID);
	/**
	 * A user may take a seperate role (besides his overall Testbed role) for an
	 * experiment. E.g. he/she could beReader, but within the given context of a certain
	 * Experiment he/she may act as Experimenter.
	 * @param hUserIDsAndExperimentRoles Hashtable<userID,roleID>
	 * @see eu.planets_project.TB.data.model.finals.TestbedRoles
	 */
	public void setInvolvedUsers(Hashtable<Long,Integer> hUserIDsAndExperimentRoles);
	
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
