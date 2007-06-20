package eu.planets_project.TB.api.interfaces.model;

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
	
	public void setApprovalMinRequiredRole(int iRoleID);
	public void setApprovalMinRequiredRoles(int[] iRoleIDs);
	public void addApprovalMinRequiredRole(int iRoleID);
	public void addApprovalMinRequiredRoles(int[] iRoleIDs);
	public void removeApprovalMinRequiredRole(int iRoleID);
	public void removeApprovalMinRequiredRoles(int[] iRoleIDs);
	public int[] getApprovalMinRequiredRoles();
	
	public void setApprovalUser(User user);
	public void setApprovalUsers(User[] users);
	public void addApprovalUser(User user);
	public void addApprovalUsers(User[] user);
	public void removeApprovalUser(User user);
	public void removeApprovalUsers(User[] user);
	public int[] getApprovalUsers();

}
