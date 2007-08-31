package eu.planets_project.tb.api.model;

import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.model.finals.ExperimentTypes;

/**
 * @author alindley
 *
 */
public interface BasicProperties{
	
	public void setExperimentName(String sName);
	public String getExperimentName();
	public boolean checkExperimentNameUnique(String sExpName);
	
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
	
	public void setExternalReferences(List<String> sRefNames);
	public void addExternalReference(String sRefName);
	public void removeExternalReference(String sRefName);
	public List<String> getExternalReferences();
	
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
	 * The Object Type  will e.g. specify a experiment on "jpeg" images – but does not contain
	 * any reference to the actual data which is part of the Design Experiment stage.
	 * 
	 * @param sMimeType: formating string/string is checked
	 */
	public void setExperimentedObjectType(String sMimeType);

	public void setExperimentedObjectTypes(List<String> sMimeTypes);
	public List<String> getExperimentedObjectTypes();
	
	public void setFocus(String sFocus);
	public String getFocus();

	public void setScope(String sScope);
	public String getScope();

	public void setExperimenter(String sUserID);
	public String getExperimenter();
	
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
	 * e.g. ExperimentTypes.EXPERIMENT_TYPE_SIMPLEMIGRATION 
	 * @param iID ExperimentApproach ID.
	 * @return "migration", "emulation" or null
	 */
	public String getExperimentApproachName(int iID);
	
	public void setConsiderations(String sConsid);
	public String getConsiderations();
	
	/**
	 * An experiment may either be formal or informal.  
	 * A formal experiment is available for other users 
	 * whereas an informal experiment will only be visible to the owner.
	 * @param bFormal
	 */
	public void setExperimentFormal(boolean bFormal);
	public boolean isExperimentFormal();
	public boolean isExperimentInformal();
	
	/**
	 * This method is used to specify an external ID for the experiments.  
	 * Note that the Testbed will automatically generate a Testbed specific ID for each experiment 
	 * so this method should only be used if one wishes to tie an experiment to an external reference/system. 
	 * @param sRefName
	 */
	public void setExternalReferenceID(String sRefName);
	public String getExternalReferenceID();
	
	/**
	 * If the experiment references any papers, books or web pages you can add references to them here.
	 * @param sTitle
	 * @param URI
	 */
	public void addLiteratureReference(String sTitle, String URI);
	public void removeLiteratureReference(String sTitle, String URI);
	public void setLiteratureReference(List<String[]> references);
	/**
	 * @return List.get(0)=Title; List.get(1) = URI;
	 */
	public List<List<String>> getAllLiteratureReferences();
	
	/**The Tool Type will specify for example a "jpeg2pdfMigration" experiment – but does not contain
	 * any reference to actual tools instances, which is part of the Design Experiment stage.
	 * @param toolTypes: requires to be in the format which is accepted and known by the service registry
	 **/
	public void setToolTypes(List<String> toolTypes);
	public void addToolType(String toolType);
	public void removeToolType(String toolType);
	public List<String> getToolTypes();
	
}
