package eu.planets_project.tb.api;

import java.util.Vector;

import eu.planets_project.tb.api.model.Comment;

public interface CommentManager {
	
	public Vector<Comment> getComments(long lExperimentID, String sExperimentPhaseID);
	
	public Vector<Long> getCommentIDs(long lExperimentID, String sExperimentPhaseID);
	
	public Comment getComment(long lCommentID);
	
	public Comment getNewRootComment(long lExperimentID, String sExperimentPhaseID);
	
	public void registerComment(Comment comment, long lExperimentID, String sExperimentPhaseID);
	public void removeComment(long lCommentID);
	
	public void updateComment(Comment comment);

}
