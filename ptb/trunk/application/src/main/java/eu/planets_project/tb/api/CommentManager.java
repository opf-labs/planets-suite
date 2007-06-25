package eu.planets_project.tb.api;

import eu.planets_project.tb.api.model.Comment;

public interface CommentManager {
	
	public Comment[] getComments(String sExperimentPhaseID);
	
	public String[] getCommentIDs(String sExperimentPhaseID);
	
	public Comment getComment(String sCommentID);
	
	public Comment getNewComment();
	
	public void registerComment(Comment comment);
	public void removeComment(String sCommentID);

}
