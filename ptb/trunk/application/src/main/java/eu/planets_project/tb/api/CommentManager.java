package eu.planets_project.tb.api;

import java.util.Vector;

import eu.planets_project.tb.api.model.Comment;

public interface CommentManager {
	
	public Vector<Comment> getComments(String sExperimentPhaseID);
	
	public Vector<String> getCommentIDs(String sExperimentPhaseID);
	
	public Comment getComment(long lCommentID);
	
	public Comment getNewComment();
	
	public void registerComment(Comment comment);
	public void removeComment(long lCommentID);

}
