/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import eu.planets_project.tb.impl.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;


/**
 * @author alindley
 *
 */
@Entity
public class BasicProperties 
implements eu.planets_project.tb.api.model.BasicProperties, java.io.Serializable {
	
	// TODO:
	// 2.WAS SOLL IM COMMENTAR DER KLASSE STEHEN?
	// 3.set/getExperimentApproach
	
	@Id
	@GeneratedValue
	private long id;
	private String sConsiderations, sContaectAddress, sContactMail, sContactName, sContactTel;
	private String sExpName, sFocus, sIndication, sPurpose, sScope, sSpecificFocus, sSummary;
	private String sExperimenterID;
	
	private int iExperimentApproach;
	
	private Vector<String> vExpObjectTypes;
	private Vector<Long> vRefExpIDs;
	private Vector<String> vInvolvedUsers;
	private HashMap<Long,Vector<Integer>> hmInvolvedUserSpecialExperimentRoles;
	
	public BasicProperties(){
		
		vRefExpIDs			= new Vector<Long>();
		vExpObjectTypes		= new Vector<String>();
		vInvolvedUsers		= new Vector<String>();
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
	public String getExperimentApproach(int iid) {
		// TODO Mapping of int and ExperimentApproach
		return null;
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
	public Vector<Long> getExperimentReferences() {
		return this.vRefExpIDs;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentedObjectTypes()
	 */
	public Vector<String> getExperimentedObjectTypes() {
		System.out.println("How many items"+this.vExpObjectTypes.size());
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
	public Vector<Long> getReferencedExperimentIDs() {
		return this.vRefExpIDs;
	}

	/* 
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getReferencedExperiments()
	 */
	public HashMap<Long,Experiment> getReferencedExperiments() {
		HashMap<Long,Experiment> hmRet = new HashMap<Long,Experiment>();
		Enumeration<Long> enumExpRefs = this.vRefExpIDs.elements();
		while(enumExpRefs.hasMoreElements()){
			long lExpRefId = enumExpRefs.nextElement();
			//get Singleton: TestbedManager
			TestbedManager testbedManager = eu.planets_project.tb.impl.TestbedManager.getInstance();
			Experiment experiment = testbedManager.getExperiment(lExpRefId);
			hmRet.put(experiment.getExperimentID(), experiment);
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
		this.vRefExpIDs.removeAllElements();
		this.vRefExpIDs.addElement(refID);
	}

	/* (non-Javadoc)
	 * Please note: SetReference always overrides existing entries - add,remove to modify
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReference(eu.planets_project.tb.api.model.Experiment)
	 */
	public void setExperimentReference(Experiment refExp) {
		this.vRefExpIDs.removeAllElements();
		this.vRefExpIDs.addElement(refExp.getExperimentID());	
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReferences(java.util.Vector)
	 */
	public void setExperimentReferences(Vector<Long> refIDs) {
		this.vRefExpIDs.removeAllElements();
		Enumeration<Long> elements = refIDs.elements();
		while(elements.hasMoreElements()){
			long lRef = elements.nextElement();
			this.vRefExpIDs.addElement(lRef);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentReferences(eu.planets_project.tb.api.model.Experiment[])
	 */
	public void setExperimentReferences(Experiment[] refExps) {
		this.vRefExpIDs.removeAllElements();
		for (int i=0;i<refExps.length;i++){
			this.vRefExpIDs.addElement(refExps[i].getExperimentID());
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addExperimentReference(long)
	 */
	public void addExperimentReference(long refID) {
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
		System.out.println("Tokens: "+tokenizer.countTokens());
		if (tokenizer.countTokens()==3){
			this.vExpObjectTypes.removeAllElements();
			this.vExpObjectTypes.addElement(mimeType);
			
			System.out.println("Hier1: "+this.vExpObjectTypes.contains(mimeType));
		}
	}
	
	public void addExperimentedObjectType(String mimeType) {
		//parse input string if it is in the format String/String
		StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
		System.out.println("Tokens: "+tokenizer.countTokens());
		if (tokenizer.countTokens()==3){
			if(!this.vExpObjectTypes.contains(mimeType)){
				this.vExpObjectTypes.addElement(mimeType);
				System.out.println("Hier2: "+this.vExpObjectTypes.contains(mimeType));
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentedObjectTypes(java.lang.String[])
	 */
	public void setExperimentedObjectTypes(Vector<String> mimeTypes) {
		this.vExpObjectTypes.removeAllElements();
		Iterator<String> itTypes = mimeTypes.iterator();
		for(int i=0;i<mimeTypes.size();i++){
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
	 * @see eu.planets_project.tb.api.model.BasicProperties#addInvolvedUsers(java.util.Vector)
	 */
	public void addInvolvedUsers(Vector<String> usersIDs) {
		Iterator<String> itUserIDs = usersIDs.iterator();
		while(itUserIDs.hasNext()){
			String sUserID = itUserIDs.next();
			//check to avoid duplicates
			if(!this.vInvolvedUsers.contains(sUserID))
				this.vInvolvedUsers.addElement(sUserID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUsers(java.util.Vector)
	 */
	public void removeInvolvedUsers(Vector<String> userIDs) {
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
	public Vector<String> getInvolvedUserIds() {
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


}
