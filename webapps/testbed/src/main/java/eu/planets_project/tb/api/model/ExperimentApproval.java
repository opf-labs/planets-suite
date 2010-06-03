package eu.planets_project.tb.api.model;

import java.util.List;

/**
 * @author alindley
 *
 */
public interface ExperimentApproval extends ExperimentPhase{
	
	public void setExplanation(String sExplanation);
	public String getExplanation();
	
	public void setDecision(String sDecision);
	public String getDecision();
	
	public void setGo(boolean go);
	public boolean getGo();

	
	
	/**
	 * These methods define which roles are required (a user must have) to approve this stage
	 * @param iRoleID
	 */
	public void setApprovalRequiredRole(int iRoleID);
	public void setApprovalRequiredRoles(List<Integer> iRoleIDs);
	public void addApprovalRequiredRole(int iRoleID);
	public void addApprovalRequiredRoles(List<Integer> iRoleIDs);
	public void removeApprovalRequiredRole(int iRoleID);
	public void removeApprovalRequiredRoles(List<Integer> iRoleIDs);
	public List<Integer> getApprovalRequiredRoles();
	
	
	/**
	 * A concrete User instance may only be added if at least one approvalRequiredRole has been added before
	 * @param user
	 */
	public void addApprovalUser(String userID);
	public void addApprovalUsers(List<String> usersIDs);
	public void removeApprovalUser(String userID);
	public void removeApprovalUsers(List<String> userIDs);
	/**
	 * Returns a list of user IDs
	 * @return
	 */
	public List<String> getApprovalUsersIDs();

}
