package eu.planets_project.tb.api.model;

import java.util.Calendar;
import java.util.List;


public interface Comment {
	
	public long getCommentID();
	
	public void setAuthorID(String sAuthorID);
	public String getAuthorID();
	
	public void setTitle(String sTitle);
	public String getTitle();
	
	public void setPostDate(Calendar Date);
	public Calendar getPostDate();
	public long getPostDateInMillis();
	
	public void setComment(String authorID, String title, String commentText);
	public void setComment(String title, String commentText);
	public String getComment();
	
    public Comment getParent();
    
    public long getParentID();
    public void setParentID(long parentID);
	
	public void setExperimentPhaseID(String sID);
	public String getExperimentPhaseID();
	
	public void setExperimentID(long lID);
	public long getExperimentID();

}
