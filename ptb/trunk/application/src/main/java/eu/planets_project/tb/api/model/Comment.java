package eu.planets_project.tb.api.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;


public interface Comment {
	
	public long getCommentID();
	
	public void setAuthorID(String sAuthorID);
	public String getAuthorID();
	
	public void setTitle(String sTitle);
	public String getTile();
	
	public void setPostDate(Calendar Date);
	public Calendar getPostDate();
	public long getPostDateInMillis();
	
	public void setComment(String authorID, String title, String commentText);
	public void setComment(String title, String commentText);
	public String getComment();

	
	//Adding and Removing Nodes
	/**
	 * @param sCommentingNodeID The node for replying on.
	 */
	public void setReplies(Vector<Comment> reply);
	
	public void addReply(Comment reply);
	public void removeReply(Comment reply);
	/**
	 * Returns (non-recursive) replies
	 * @return
	 */
	public Vector<Comment> getReplies();
	
	public Comment getParent();
	
	public void setExperimentPhaseID(String sID);
	public String getExperimentPhaseID();
	
	public void setExperimentID(long lID);
	public long getExperimentID();

}
