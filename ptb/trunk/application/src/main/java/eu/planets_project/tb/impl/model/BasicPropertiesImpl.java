/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;


/**
 * @author alindley
 *
 */
@Entity
public class BasicPropertiesImpl 
implements eu.planets_project.tb.api.model.BasicProperties, java.io.Serializable {
	
	// TODO:
	// 2.WAS SOLL IM COMMENTAR DER KLASSE STEHEN?
	// 3.set/getExperimentApproach
	
	@Id
	@GeneratedValue
	private long id;
	private String sConsiderations, sContaectAddress, sContactMail, sContactName, sContactTel;
	private String sExpName, sFocus, sIndication, sPurpose, sScope, sSpecificFocus, sSummary;
	private String sExperimenterID, sExternalReferenceID;
	private boolean bFormal;
	private int iExperimentApproach;
	
	private Vector<String> vExpObjectTypes, vExpToolTypes;
	private Vector<Long> vRefExpIDs;
	private Vector<String> vExternalReferences, vInvolvedUsers;
	private Vector<List<String>> vLiteratureReference;
	private HashMap<Long,Vector<Integer>> hmInvolvedUserSpecialExperimentRoles;
	
	public BasicPropertiesImpl(){
		
		vRefExpIDs			= new Vector<Long>();
		vExpObjectTypes		= new Vector<String>();
		vExpToolTypes		= new Vector<String>();
		vInvolvedUsers		= new Vector<String>();
		vExternalReferences = new Vector<String>();
		vLiteratureReference = new Vector<List<String>>();
		hmInvolvedUserSpecialExperimentRoles = new HashMap<Long,Vector<Integer>>();
	}
	
	public long getId(){
		return this.id;
	}
	
	private void setId(long id){
		this.id=id;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getConsiderations()
	 */
	public String getConsiderations() {
		return this.sConsiderations;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getContactAddress()
	 */
	public String getContactAddress() {
		return sContaectAddress;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getContactMail()
	 */
	public String getContactMail() {
		return sContactMail;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getContactName()
	 */
	public String getContactName() {
		return sContactName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getContactTel()
	 */
	public String getContactTel() {
		return sContactTel;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentApproach()
	 */
	public int getExperimentApproach() {
		return this.iExperimentApproach;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentApproach(int)
	 */
	public String getExperimentApproachName(int iid) {
		ExperimentTypesImpl expTypes = new ExperimentTypesImpl();
		return expTypes.getExperimentTypeName(iid);
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentName()
	 */
	public String getExperimentName() {
		return sExpName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentReferences()
	 */
	public List<Long> getExperimentReferences() {
		return this.vRefExpIDs;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentedObjectTypes()
	 */
	public List<String> getExperimentedObjectTypes() {
		return this.vExpObjectTypes;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getFocus()
	 */
	public String getFocus() {
		return sFocus;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getIndication()
	 */
	public String getIndication() {
		return sIndication;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getPurpose()
	 */
	public String getPurpose() {
		return sPurpose;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getReferencedExperimentIDs()
	 */
	public List<Long> getReferencedExperimentIDs() {
		return this.vRefExpIDs;
	}

	/* 
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getReferencedExperiments()
	 */
	public Map<Long,Experiment> getReferencedExperiments() {
		HashMap<Long,eu.planets_project.tb.api.model.Experiment> hmRet = new HashMap<Long,eu.planets_project.tb.api.model.Experiment>();
		Enumeration<Long> enumExpRefs = this.vRefExpIDs.elements();
		while(enumExpRefs.hasMoreElements()){
			long lExpRefId = enumExpRefs.nextElement();
			//get Singleton: TestbedManager
			TestbedManagerImpl testbedManager = eu.planets_project.tb.impl.TestbedManagerImpl.getInstance();
			ExperimentImpl experiment = (ExperimentImpl)testbedManager.getExperiment(lExpRefId);
			hmRet.put(experiment.getEntityID(), experiment);
		}
		return hmRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getScope()
	 */
	public String getScope() {
		return sScope;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getSpecificFocus()
	 */
	public String getSpecificFocus() {
		return sSpecificFocus;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getSummary()
	 */
	public String getSummary() {
		return this.sSummary;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setConsiderations(java.lang.String)
	 */
	public void setConsiderations(String consid) {
		this.sConsiderations = consid;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setContact(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setContact(String name, String mail, String tel, String address) {
		this.sContactName = name;
		this.sContactMail = mail;
		this.sContaectAddress = address;
		this.sContactTel = tel;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentApproach(int)
	 */
	public void setExperimentApproach(int iid) {
		//ExperimentApproach must lay between 0..1
		if(iid>=0&&iid<=1){
			this.iExperimentApproach = iid;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentName(java.lang.String)
	 */
	public void setExperimentName(String name) {
		this.sExpName = name;
	}

	/* (non-Javadoc)
	 * Please note: SetReference always overrides existing entries - add,remove to modify
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReference(long)
	 */
	public void setExperimentReference(long refID) {
		//set always overrides existig entries - add,remove to modify
		this.vRefExpIDs = new Vector<Long>();
		this.vRefExpIDs.addElement(refID);
	}

	/* (non-Javadoc)
	 * Please note: SetReference always overrides existing entries - add,remove to modify
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReference(eu.planets_project.tb.api.model.Experiment)
	 */
	public void setExperimentReference(Experiment refExp) {
		this.vRefExpIDs = new Vector<Long>();
		ExperimentImpl exp = (ExperimentImpl) refExp;
		this.vRefExpIDs.addElement(exp.getEntityID());	
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReferences(java.util.List)
	 */
	public void setExperimentReferences(List<Long> refIDs) {
		this.vRefExpIDs = new Vector<Long>();
		Iterator<Long> itElements = refIDs.iterator();
		while(itElements.hasNext()){
			long lRef = itElements.next();
			this.vRefExpIDs.addElement(lRef);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReferences(eu.planets_project.tb.api.model.Experiment[])
	 */
	public void setExperimentReferences(Experiment[] refExps) {
		this.vRefExpIDs = new Vector<Long>();
		for (int i=0;i<refExps.length;i++){
			ExperimentImpl exp = (ExperimentImpl)refExps[i];
			this.vRefExpIDs.addElement(exp.getEntityID());
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addExperimentReference(long)
	 */
	public void addExperimentReference(long refID) {
		if(!this.vRefExpIDs.contains(refID))
			this.vRefExpIDs.addElement(refID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeExperimentReference(long)
	 */
	public void removeExperimentReference(long refID) {
		this.vRefExpIDs.remove(refID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentedObjectType(java.lang.String)
	 */
	public void setExperimentedObjectType(String mimeType) {
		//parse input string if it is in the format String/String
		StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
		if (tokenizer.countTokens()==3){
			this.vExpObjectTypes = new Vector<String>();
			this.vExpObjectTypes.addElement(mimeType);
		}
	}
	
	public void addExperimentedObjectType(String mimeType) {
		//parse input string if it is in the format String/String
		StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
		if (tokenizer.countTokens()==3){
			if(!this.vExpObjectTypes.contains(mimeType))
				this.vExpObjectTypes.addElement(mimeType);	
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentedObjectTypes(java.lang.String[])
	 */
	public void setExperimentedObjectTypes(List<String> mimeTypes) {
		this.vExpObjectTypes = new Vector<String>();
		Iterator<String> itTypes = mimeTypes.iterator();
		while(itTypes.hasNext()){
			addExperimentedObjectType(itTypes.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimenter(long)
	 */
	public void setExperimenter(String userID) {
		this.sExperimenterID = userID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimenter()
	 */
	public String getExperimenter() {
		return this.sExperimenterID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setFocus(java.lang.String)
	 */
	public void setFocus(String focus) {
		this.sFocus = focus;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setIndication(java.lang.String)
	 */
	public void setIndication(String description) {
		this.sIndication = description;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addInvolvedUsersWithSpecialExperimentRole(java.util.Hashtable)
	 */
/*	public void addInvolvedUsersWithSpecialExperimentRole(
			HashMap<Long, Vector<Integer>> hmUserIDsAndExperimentRoles) {
		Iterator<Long> keys = hmUserIDsAndExperimentRoles.keySet().iterator();
		while(keys.hasNext()){
			long lUserID = keys.next();
			Vector<Integer> vRoles = hmUserIDsAndExperimentRoles.get(lUserID);
			boolean bContains = this.vInvolvedUsers.contains(lUserID);
			if(!bContains){
				//User has not been added until now
				this.vInvolvedUsers.addElement(lUserID);
				this.hmInvolvedUserSpecialExperimentRoles.put(lUserID, vRoles);
			}
			else{
				//update existing user with new special Roles for the Experiment
				Vector<Integer> vExistingRoles = this.hmInvolvedUserSpecialExperimentRoles.get(lUserID);
				Iterator<Integer> itRoles = vRoles.iterator();
				while(itRoles.hasNext()){
					int iNewRole = itRoles.next();
					boolean bContainsRole = vExistingRoles.contains(iNewRole);
					if(!bContainsRole){
						//add new Role
						vExistingRoles.addElement(iNewRole);
					}
				}
				//rewrite bean entry
				this.hmInvolvedUserSpecialExperimentRoles.put(lUserID, vExistingRoles);
				//end update
			}
		}	
	}
*/	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUsersAndSpecialExperimentRole(java.util.Hashtable)
	 */
/*	public void removeInvolvedUsersAndSpecialExperimentRole(
			HashMap<Long, Vector<Integer>> hmUserIDsAndExperimentRoles) {
		Iterator<Long> keys = hmUserIDsAndExperimentRoles.keySet().iterator();
		while(keys.hasNext()){
			long lUserID = keys.next();
			Vector<Integer> vRoles = hmUserIDsAndExperimentRoles.get(lUserID);
			boolean bContains = this.vInvolvedUsers.contains(lUserID);
			if(bContains){
				//User has been added already - update his experiment specific roles
				//update existing user with new special Roles for the Experiment
				Vector<Integer> vExistingRoles = this.hmInvolvedUserSpecialExperimentRoles.get(lUserID);
				Iterator<Integer> itRoles = vRoles.iterator();
				while(itRoles.hasNext()){
					int iNewRole = itRoles.next();
					boolean bContainsRole = vExistingRoles.contains(iNewRole);
					if(bContainsRole){
						//add new Role
						vExistingRoles.removeElement(iNewRole);
					}
				}
				//rewrite bean entry
				this.hmInvolvedUserSpecialExperimentRoles.put(lUserID, vExistingRoles);
				//end update
			}
			else{
				//error user has never been added to this experiment before
				//just for safety reasons...
				this.hmInvolvedUserSpecialExperimentRoles.remove(lUserID);
		
			}
		}

	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setPurpose(java.lang.String)
	 */
	public void setPurpose(String purpose) {
		this.sPurpose = purpose;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setScope(java.lang.String)
	 */
	public void setScope(String scope) {
		this.sScope = scope;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setSpecificFocus(java.lang.String)
	 */
	public void setSpecificFocus(String focus) {
		this.sSpecificFocus = focus;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setSummary(java.lang.String)
	 */
	public void setSummary(String summary) {
		this.sSummary = summary;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addInvolvedUsers(java.util.List)
	 */
	public void addInvolvedUsers(List<String> usersIDs) {
		Iterator<String> itUserIDs = usersIDs.iterator();
		while(itUserIDs.hasNext()){
			String sUserID = itUserIDs.next();
			//check to avoid duplicates
			if(!this.vInvolvedUsers.contains(sUserID))
				this.vInvolvedUsers.addElement(sUserID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUsers(java.util.List)
	 */
	public void removeInvolvedUsers(List<String> userIDs) {
		this.vInvolvedUsers.removeAll(userIDs);
		//and also remove special roles for a given User within this experiment
		Iterator<String> itUserIDs = userIDs.iterator();
		while(itUserIDs.hasNext()){
			this.hmInvolvedUserSpecialExperimentRoles.remove(itUserIDs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getInvolvedUserIds()
	 */
	public List<String> getInvolvedUserIds() {
		return this.vInvolvedUsers;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getInvolvedUsers()
	 */
/*	public Vector<User> getInvolvedUsers() {
		Vector<User> vRet = new Vector<User>();
	
		TestbedManager tbmanager = TestbedManager.getInstance();
		UserManager usermanager = (UserManager)tbmanager.getUserManager();
		Iterator<Long> itAllUserIDs = this.vInvolvedUsers.iterator();
		while(itAllUserIDs.hasNext()){
			vRet.addElement(usermanager.getUser(itAllUserIDs.next()));
		}
		return vRet;
	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addInvolvedUser(long)
	 */
	public void addInvolvedUser(String userID) {
		if(!this.vInvolvedUsers.contains(userID))
			this.vInvolvedUsers.addElement(userID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUser(long)
	 */
	public void removeInvolvedUser(String userID) {
		this.vInvolvedUsers.removeElement(userID);
		//and also remove special roles for a given User within this experiment
		this.hmInvolvedUserSpecialExperimentRoles.remove(userID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addExternalReference(java.lang.String)
	 */
	public void addExternalReference(String refName) {
		if(!this.vExternalReferences.contains(refName))
			this.vExternalReferences.add(refName);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExternalReferences()
	 */
	public List<String> getExternalReferences() {
		return this.vExternalReferences;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#isExperimentFormal()
	 */
	public boolean isExperimentFormal() {
		if (this.bFormal==true)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#isExperimentInformal()
	 */
	public boolean isExperimentInformal() {
		if (this.bFormal==false)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeExternalReference(java.lang.String)
	 */
	public void removeExternalReference(String refName) {
		if (this.vExternalReferences.contains(refName))
			this.vExternalReferences.remove(refName);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentFormal(boolean)
	 */
	public void setExperimentFormal(boolean formal) {
		this.bFormal = formal;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExternalReferences(java.util.List)
	 */
	public void setExternalReferences(List<String> refNames) {
		this.vExternalReferences = new Vector<String>();
		Iterator<String> itNames = refNames.iterator();
		while(itNames.hasNext()){
			addExternalReference(itNames.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#checkExperimentNameUnique(java.lang.String)
	 */
	public boolean checkExperimentNameUnique(String expName) {
		TestbedManager tbManager = TestbedManagerImpl.getInstance();
		return tbManager.isExperimentNameUnique(expName);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExternalReferenceID()
	 */
	public String getExternalReferenceID() {
		return this.sExternalReferenceID;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getAllLiteratureReferences()
	 */
	public List<List<String>> getAllLiteratureReferences() {
		return this.vLiteratureReference;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getToolTypes()
	 */
	public List<String> getToolTypes() {
		return this.vExpToolTypes;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExternalReferenceID(java.lang.String)
	 */
	public void setExternalReferenceID(String refName) {
		this.sExternalReferenceID = refName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addLiteratureReference(java.lang.String, java.lang.String)
	 */
	public void addLiteratureReference(String title, String URI) {
		Iterator<List<String>> itElements = this.vLiteratureReference.iterator();
		while(itElements.hasNext()){
			List<String> element = itElements.next();
			int i = 0;
			if(!element.contains(title)&&!element.contains(URI)){
				i++;
			}
			if(i == this.vLiteratureReference.size()){
				//was not found, so let's add it:
				List<String> vAdd = new Vector<String>();
				vAdd.add(0, title);
				vAdd.add(1, URI);
				this.vLiteratureReference.add(vAdd);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeLiteratureReference(java.lang.String, java.lang.String)
	 */
	public void removeLiteratureReference(String title, String URI) {
		Iterator<List<String>> itElements = this.vLiteratureReference.iterator();
		while(itElements.hasNext()){
			List<String> element = itElements.next();
			if(element.contains(title)&&element.contains(URI)){
				this.vLiteratureReference.remove(element);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setLiteratureReference(java.util.List)
	 */
	public void setLiteratureReference(List<String[]> references){
		this.vLiteratureReference = new Vector<List<String>>();
		Iterator<String[]> itRefs = references.iterator();
		while(itRefs.hasNext()){
			String[] lit = itRefs.next();
			addLiteratureReference(lit[0],lit[1]);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setToolTypes(java.util.List)
	 */
	public void setToolTypes(List<String> toolTypes) {
		this.vExpToolTypes = new Vector<String>();
		for(int i=0;i<toolTypes.size();i++){
			addToolType(toolTypes.get(i));
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addToolType(java.lang.String)
	 */
	public void addToolType(String toolType){
		if(!this.vExpToolTypes.contains(toolType))
			this.vExpToolTypes.add(toolType);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeToolType(java.lang.String)
	 */
	public void removeToolType(String toolType){
		if(this.vExpToolTypes.contains(toolType))
			this.vExpToolTypes.remove(toolType);
	}

}
