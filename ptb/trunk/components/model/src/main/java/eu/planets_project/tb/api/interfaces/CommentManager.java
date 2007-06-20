package eu.planets_project.TB.api.interfaces;

import eu.planets_project.TB.api.interfaces.model.Comment;

public interface CommentManager {
	
	public Comment[] getComments(String sExperimentPhaseID);
	
	public String[] getCommentIDs(String sExperimentPhaseID);
	
	public Comment getComment(String sCommentID);
	
	public Comment getNewComment();
	
	public void registerComment(Comment comment);
	public void removeComment(String sCommentID);

}
