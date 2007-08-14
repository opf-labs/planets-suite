/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import eu.planets_project.tb.api.model.User;
import eu.planets_project.tb.impl.UserManager;

/**
 * @author alindley
 *
 */
public class ExperimentApproval implements
		eu.planets_project.tb.api.model.ExperimentApproval,
		java.io.Serializable{
	
	private long lExpApprovalID;
	//roles as defined in the Class TestbedRoles
	private Vector<Integer> vReqRoles;
	private Vector<Long> vApprovalUsers;
	private String sDecision, sExplanation;
	private boolean bGo;
	
	
	public ExperimentApproval(){
		vReqRoles = new Vector<Integer>();
		vApprovalUsers = new Vector<Long>();
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
	public void addApprovalUser(User user) {
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
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalUsers(java.util.Vector)
	 */
	public void addApprovalUsers(Vector<User> user) {
		Iterator<User> itUsers = user.iterator();
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
	public Vector<Long> getApprovalUsersIDs() {
		return this.vApprovalUsers;
	}
	
	public Vector<User> getApprovalUsers() {
		Vector<User> vRet = new Vector<User>();
		UserManager userManager = UserManager.getInstance();
		Iterator<Long> vUserIDs = this.vApprovalUsers.iterator();
		while(vUserIDs.hasNext()){
			User user = userManager.getUser(vUserIDs.next());
			vRet.addElement(user);
		}
		return vRet;
	}

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
	public void removeApprovalUser(User user) {
		if (this.vApprovalUsers.contains(user.getUserID())){
			this.vApprovalUsers.remove(user.getUserID());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalUsers(java.util.Vector)
	 */
	public void removeApprovalUsers(Vector<User> users) {
		Iterator<User> itApprovalUsers = users.iterator();
		while(itApprovalUsers.hasNext()){
			removeApprovalUser(itApprovalUsers.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRole(int)
	 */
	public void setApprovalRequiredRole(int roleID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRoles(java.util.Vector)
	 */
	public void setApprovalRequiredRoles(Vector<Integer> roleIDs) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUser(eu.planets_project.tb.api.model.User)
	 */
	public void setApprovalUser(User user) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUsers(java.util.Vector)
	 */
	public void setApprovalUsers(Vector<User> users) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setDecision(java.lang.String)
	 */
	public void setDecision(String decision) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setExplanation(java.lang.String)
	 */
	public void setExplanation(String explanation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setGo(boolean)
	 */
	public void setGo(boolean go) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getDurationInMillis()
	 */
	public long getDurationInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDate()
	 */
	public GregorianCalendar getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getEndDateInMillis()
	 */
	public long getEndDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getPhaseID()
	 */
	public String getPhaseID() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getProgress()
	 */
	public int getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDate()
	 */
	public GregorianCalendar getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getStartDateInMillis()
	 */
	public long getStartDateInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getState()
	 */
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#getSummary()
	 */
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isCompleted()
	 */
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isInProgress()
	 */
	public boolean isInProgress() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#isNotStarted()
	 */
	public boolean isNotStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setEndDate(java.util.GregorianCalendar)
	 */
	public void setEndDate(GregorianCalendar endDate) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setProgress(int)
	 */
	public void setProgress(int progress) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setStartDate(java.util.GregorianCalendar)
	 */
	public void setStartDate(GregorianCalendar startDate) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentPhase#setState(int)
	 */
	public void setState(int state) {
		// TODO Auto-generated method stub

	}


}
