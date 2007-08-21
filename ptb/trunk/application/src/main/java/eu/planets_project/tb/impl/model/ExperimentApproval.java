/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * @author alindley
 *
 */
//@Entity
public class ExperimentApproval extends eu.planets_project.tb.impl.model.ExperimentPhase
implements eu.planets_project.tb.api.model.ExperimentApproval, java.io.Serializable {
	
	//roles as defined in the Class TestbedRoles
	private Vector<Integer> vReqRoles;
	private Vector<String> vApprovalUsers;
	private String sDecision, sExplanation;
	private boolean bGo;
	
	
	public ExperimentApproval(){
		vReqRoles = new Vector<Integer>();
		vApprovalUsers = new Vector<String>();
		bGo = false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalRequiredRole(int)
	 */
	public void addApprovalRequiredRole(int roleID) {
		this.vReqRoles.addElement(roleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalRequiredRoles(java.util.Vector)
	 */
	public void addApprovalRequiredRoles(Vector<Integer> roleIDs) {
		this.vReqRoles.addAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalUser(eu.planets_project.tb.api.model.User)
	 */
	public void addApprovalUser(String user) {
		this.vApprovalUsers.addElement(user);
		
		//approval required roles were added
		/*if (this.vReqRoles.size()>0){
			Vector<Integer> vUserRoleIDs = user.getRolesIDs();
			Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
			int i = 0;
			while(itUserRoleIDs.hasNext()){
				//check if user is registered to all required roles for approval
				int iRole = itUserRoleIDs.next();
				i++;
			}
			if (i==vUserRoleIDs.size()){
				this.vApprovalUsers.addElement(user.getUserID());
			}
		}*/
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalUsers(java.util.Vector)
	 */
	public void addApprovalUsers(Vector<String> users) {
		Iterator<String> itUsers = users.iterator();
		while(itUsers.hasNext()){
			addApprovalUser(itUsers.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getApprovalRequiredRoles()
	 */
	public Vector<Integer> getApprovalRequiredRoles() {
		return this.vReqRoles;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getApprovalUsers()
	 */
	public Vector<String> getApprovalUsersIDs() {
		return this.vApprovalUsers;
	}
	
/*	public Vector<User> getApprovalUsers() {
		Vector<User> vRet = new Vector<User>();
		UserManager userManager = UserManager.getInstance();
		Iterator<Long> vUserIDs = this.vApprovalUsers.iterator();
		while(vUserIDs.hasNext()){
			User user = userManager.getUser(vUserIDs.next());
			vRet.addElement(user);
		}
		return vRet;
	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getDecision()
	 */
	public String getDecision() {
		return this.sDecision;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getExplanation()
	 */
	public String getExplanation() {
		return this.sExplanation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getGo()
	 */
	public boolean getGo() {
		return this.bGo;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalRequiredRole(int)
	 */
	public void removeApprovalRequiredRole(int roleID) {
		if (this.vReqRoles.contains(roleID)){
			this.vReqRoles.removeElement(roleID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalRequiredRoles(java.util.Vector)
	 */
	public void removeApprovalRequiredRoles(Vector<Integer> roleIDs) {
		Iterator<Integer> itRoleIDs = roleIDs.iterator();
		while(itRoleIDs.hasNext()){
			removeApprovalRequiredRole(itRoleIDs.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalUser(eu.planets_project.tb.api.model.User)
	 */
	public void removeApprovalUser(String user) {
		if (this.vApprovalUsers.contains(user)){
			this.vApprovalUsers.remove(user);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalUsers(java.util.Vector)
	 */
	public void removeApprovalUsers(Vector<String> users) {
		Iterator<String> itApprovalUsers = users.iterator();
		while(itApprovalUsers.hasNext()){
			removeApprovalUser(itApprovalUsers.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRole(int)
	 */
	public void setApprovalRequiredRole(int roleID) {
		this.vReqRoles.removeAllElements();
		this.vReqRoles.addElement(roleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRoles(java.util.Vector)
	 */
	public void setApprovalRequiredRoles(Vector<Integer> roleIDs) {
		this.vReqRoles.removeAllElements();
		this.vReqRoles.addAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUser(eu.planets_project.tb.api.model.User)
	 */
/*	public void setApprovalUser(User user) {
		//approval required roles were added
		if (this.vReqRoles.size()>0){
			Vector<Integer> vUserRoleIDs = user.getRolesIDs();
			Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
			int i = 0;
			while(itUserRoleIDs.hasNext()){
				//check if user is registered to all required roles for approval
				int iRole = itUserRoleIDs.next();
				i++;
			}
			if (i==vUserRoleIDs.size()){
				this.vApprovalUsers.removeAllElements();
				this.vApprovalUsers.addElement(user.getUserID());
			}
		}
	}*/

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUsers(java.util.Vector)
	 */
/*	public void setApprovalUsers(Vector<User> users) {
		this.vApprovalUsers.removeAllElements();
		Iterator<User> itUsers = users.iterator();
		while(itUsers.hasNext()){
			User user = itUsers.next();
			//approval required roles were added
			if (this.vReqRoles.size()>0){
				Vector<Integer> vUserRoleIDs = user.getRolesIDs();
				Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
				int i = 0;
				while(itUserRoleIDs.hasNext()){
					//check if user is registered to all required roles for approval
					int iRole = itUserRoleIDs.next();
					i++;
				}
				if (i==vUserRoleIDs.size()){
					this.vApprovalUsers.addElement(user.getUserID());
				}
			}
		}
	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setDecision(java.lang.String)
	 */
	public void setDecision(String decision) {
		this.sDecision = decision;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setExplanation(java.lang.String)
	 */
	public void setExplanation(String explanation) {
		this.sExplanation = explanation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setGo(boolean)
	 */
	public void setGo(boolean go) {
		this.bGo = go;
	}

}
