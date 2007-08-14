/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.User;

/**
 * @author alindley
 *
 */
@Entity
public class BasicProperties implements 
				eu.planets_project.tb.api.model.BasicProperties, 
				java.io.Serializable {
	
	// TODO:
	// 2.WAS SOLL IM COMMENTAR DER KLASSE STEHEN?
	// 3.set/getExperimentApproach
	
	@Id
	@GeneratedValue
	private long id;
	private String sConsiderations, sContaectAddress, sContactMail, sContactName, sContactTel;
	private String sExpName, sFocus, sIndication, sPurpose, sScope, sSpecificFocus, sSummary;
	private long lExperimenterID;
	
	private int iExperimentApproach;
	
	private Vector<Long> expRefs;
	private Vector<String> vExpObjectTypes;
	private Vector<Long> vRefExpIDs;
	private Vector<Long> vInvolvedUsers;
	private HashMap<Long,Vector<Integer>> hmInvolvedUserSpecialExperimentRoles;
	
	public BasicProperties(){
		
		vRefExpIDs			= new Vector<Long>();
		vExpObjectTypes		= new Vector<String>();
		vInvolvedUsers		= new Vector<Long>();
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
	public Iterator<Long> getExperimentReferences() {
		return this.expRefs.iterator();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#getExperimentedObjectTypes()
	 */
	public Iterator<String> getExperimentedObjectTypes() {
		return this.vExpObjectTypes.iterator();
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
	public Iterator<Long> getReferencedExperimentIDs() {
		return this.vRefExpIDs.iterator();
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
	}

	/* (non-Javadoc)
	 * Sets name, mail and snail mail
	 * @see eu.planets_project.tb.api.model.BasicProperties#setContact(eu.planets_project.tb.api.model.User)
	 */
	public void setContact(User bean) {
		this.sContactName = bean.getForename()+" "+ bean.getSurname();
		this.sContactMail = bean.getEmail();
		this.sContaectAddress = bean.getAddress();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentApproach(int)
	 */
	public void setExperimentApproach(int iid) {
		this.iExperimentApproach = iid;
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
		this.vExpObjectTypes.removeAllElements();
		this.vExpObjectTypes.addElement(mimeType);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimentedObjectTypes(java.lang.String[])
	 */
	public void setExperimentedObjectTypes(Vector<String> mimeTypes) {
		this.vExpObjectTypes.removeAllElements();
		this.vExpObjectTypes.addAll(mimeTypes);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimenter(eu.planets_project.tb.api.model.User)
	 */
	public void setExperimenter(User experimenter) {
		this.lExperimenterID = experimenter.getUserID();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setExperimenter(long)
	 */
	public void setExperimenter(long userID) {
		this.lExperimenterID = userID;
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
	 * @see eu.planets_project.tb.api.model.BasicProperties#setInvolvedUsers(eu.planets_project.tb.api.model.User[])
	 */
	public void setInvolvedUsers(Vector<User> users) {
		this.vInvolvedUsers.removeAllElements();
		//also remove special roles for this experiment
		this.hmInvolvedUserSpecialExperimentRoles = new HashMap<Long,Vector<Integer>>();
		Iterator<User> itUsers = users.iterator();
		Vector<Long> vUserIDs = new Vector<Long>();
		while(itUsers.hasNext()){
			vUserIDs.addElement(itUsers.next().getUserID());
		}
		//and now add allUserIDs
		this.vInvolvedUsers.addAll(vUserIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#setInvolvedUsers(long)
	 */
	public void setInvolvedUsers(User user) {
		this.vInvolvedUsers.removeAllElements();
		//also remove special roles for this experiment
		this.hmInvolvedUserSpecialExperimentRoles = new HashMap<Long,Vector<Integer>>();
		this.vInvolvedUsers.addElement(user.getUserID());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#addInvolvedUsersWithSpecialExperimentRole(java.util.Hashtable)
	 */
	public void addInvolvedUsersWithSpecialExperimentRole(
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
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUsersAndSpecialExperimentRole(java.util.Hashtable)
	 */
	public void removeInvolvedUsersAndSpecialExperimentRole(
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
	public void addInvolvedUsers(Vector<Long> usersIDs) {
		this.vInvolvedUsers.addAll(usersIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.BasicProperties#removeInvolvedUsers(java.util.Vector)
	 */
	public void removeInvolvedUsers(Vector<Long> userIDs) {
		this.vInvolvedUsers.removeAll(userIDs);
		//and also remove special roles for a given User within this experiment
		Iterator<Long> itUserIDs = userIDs.iterator();
		while(itUserIDs.hasNext()){
			this.hmInvolvedUserSpecialExperimentRoles.remove(itUserIDs.next());
		}
	}


}
