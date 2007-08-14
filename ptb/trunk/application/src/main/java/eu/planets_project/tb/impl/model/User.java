/**
 * This Class is designed under the following circumstances:
 * a) A User may have one-to-many different (overlapping) roles
 */
package eu.planets_project.tb.impl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.Institution;
import eu.planets_project.tb.impl.model.finals.PlanetsInstitutions;
import eu.planets_project.tb.impl.model.finals.TestbedRoles;

/**
 * @author alindley
 *
 */
@Entity
public class User implements eu.planets_project.tb.api.model.User {

	//TODO: Check which class for password encryption can be used
	
	@Id
	@GeneratedValue
	private long lUserID;
	private Vector<Integer> vRoles;
	private String sAddress, sEmail, sForename, sSurename, sTelNr, sPassword;;
	private HashMap<String,String> hmPasswordRecovery;
	private Institution institution;
	
	public User(){
		this.vRoles = new Vector<Integer>();
		this.hmPasswordRecovery = new HashMap<String,String>();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#addRoles(java.util.Vector)
	 */
	public void addRoles(Vector<Integer> roleIDs) {
		this.vRoles.addAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#checkPassword(java.lang.String)
	 */
	public boolean checkPassword(String password) {
		if(this.sPassword.equals(password))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getAddress()
	 */
	public String getAddress() {
		return this.sAddress;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getEmail()
	 */
	public String getEmail() {
		return this.sEmail;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getForename()
	 */
	public String getForename() {
		return this.sForename;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getInstitution()
	 */
	public Institution getInstitution() {
		return this.institution;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getName()
	 */
	public String getName() {
		return this.sForename +" "+ this .sSurename;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getPassword()
	 */
	public String getPassword() {
		return this.sPassword;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getRoleNames()
	 */
	public Vector<String> getRoleNames() {
		Vector<String> vRet = new Vector<String>();
		TestbedRoles roles = new TestbedRoles();
		Iterator<Integer> itUserRoleIds = this.vRoles.iterator();
		while(itUserRoleIds.hasNext()){
			vRet.addElement(roles.getRoleName(itUserRoleIds.next()));
		}
		return vRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getRolesIDs()
	 */
	public Vector<Integer> getRolesIDs() {
		return this.vRoles;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getSurname()
	 */
	public String getSurname() {
		return this.sSurename;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getTelNr()
	 */
	public String getTelNr() {
		return this.sTelNr;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#getUserID()
	 */
	public long getUserID() {
		return this.lUserID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#isExperimenter()
	 */
	public boolean isExperimenter() {
		TestbedRoles roles = new TestbedRoles();
		int iExpID = roles.getRoleID("TESTBED_ROLE_EXPERIMENTER");
		return this.vRoles.contains(iExpID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#isPlanetsInternalUser()
	 */
	public boolean isPlanetsInternalUser() {
		boolean bRet = false;
		TestbedRoles roles = new TestbedRoles();
		int iExpID = roles.getRoleID("TESTBED_ROLE_PLANETS_USER");
		bRet= this.vRoles.contains(iExpID);
		if (!bRet){
			int iExpID2 = roles.getRoleID("TESTBED_ROLE_PLANETS_EXTERNAL_USER");
			bRet= !this.vRoles.contains(iExpID2);
		}
		return bRet;	
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#removeRoles(Vector<Integer>)
	 */
	public void removeRoles(Vector<Integer> roleIDs) {
		this.vRoles.removeAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setContactInformation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setContactInformation(String email, String telNr, String address) {	
		this.sEmail = email;
		this.sTelNr = telNr;
		this.sAddress = address;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setInstitution(eu.planets_project.tb.api.model.Institution)
	 */
	public void setInstitution(Institution inst) {
		this.institution = inst;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.sPassword = password;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setPasswordRetrievalHint(java.lang.String, java.lang.String)
	 */
	public void setPasswordRetrievalHint(String question, String answer) {
		boolean bContains = this.hmPasswordRecovery.containsKey(question);
		if (bContains)
			this.hmPasswordRecovery.remove(question);

		this.hmPasswordRecovery.put(question,answer);
	}

	/* (non-Javadoc)
	 * This method is just a convenience method for setting the role 
	 * (true): TESTBED_ROLE_PLANETS_USER 
	 * (false): TESTBED_ROLE_PLANETS_EXTERNAL_USER
	 * @see eu.planets_project.tb.api.model.User#setPlanetsInternalUser(boolean)
	 */
	public void setPlanetsInternalUser(boolean internal) {
		TestbedRoles roles = new TestbedRoles();
		int roleID;
		if(internal){
			roleID = roles.getRoleID("TESTBED_ROLE_PLANETS_USER");
		}else{
			roleID = roles.getRoleID("TESTBED_ROLE_PLANETS_EXTERNAL_USER");	
		}
		boolean bContains = this.vRoles.contains(roleID);
		//check if the reflection of the static user names delivered a result or if the roleID was already set
		if (roleID!=-1||!bContains)
			this.vRoles.add(roleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setRole(int)
	 */
	public void setRole(int role) {
		this.vRoles.removeAllElements();
		this.vRoles.add(role);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setRoles(java.util.Vector)
	 */
	public void setRoles(Vector<Integer> roles) {
		this.vRoles.removeAllElements();
		this.vRoles.addAll(roles);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.User#setUserDetails(java.lang.String, java.lang.String)
	 */
	public void setUserDetails(String forename, String surname) {
		this.sForename = forename;
		this.sSurename = surname;
	}

	/* (non-Javadoc)
	 * A convenience method to check if the user implements the role TESTBED_ROLE_ADMINISTRATOR
	 * @see eu.planets_project.tb.api.model.User#isAdministrator()
	 */
	public boolean isAdministrator() {
		TestbedRoles roles = new TestbedRoles();
		int iExpID = roles.getRoleID("TESTBED_ROLE_ADMINISTRATOR");
		return this.vRoles.contains(iExpID);
	}

	/* (non-Javadoc)
	 * A convenience method to check if the user implements the role TESTBED_ROLE_READER
	 * @see eu.planets_project.tb.api.model.User#isReader()
	 */
	public boolean isReader() {
		TestbedRoles roles = new TestbedRoles();
		int iExpID = roles.getRoleID("TESTBED_ROLE_READER");
		return this.vRoles.contains(iExpID);	}

}
