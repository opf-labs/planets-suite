package eu.planets_project.TB.api.interfaces.model;

import java.util.GregorianCalendar;


public interface Comment {
	
	public long getCommentID();
	
	public void setAuthor(String sAuthorID);
	public void setAuthor(User author);
	
	public void setTitle(String sTitle);
	public String getTile();
	
	public void setPostDate(GregorianCalendar Date);
	public GregorianCalendar getPostDate();
	
	public void setComment(String sAuthorName, String sTitle, GregorianCalendar Date);

	
	//Adding and Removing Nodes
	/**
	 * @param sCommentingNodeID The node for replying on.
	 */
	public void setReply(Comment reply);
	/**
	 * Returns (non-recursive) replies
	 * @return
	 */
	public Comment[] getReplies();
	
	public Comment getParent();
	
	public void setExperimentPhaseID(String sID);
	public String getExperimentPhaseID();

}
