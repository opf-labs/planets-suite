package eu.planets_project.tb.api;

import java.util.List;

import eu.planets_project.tb.api.model.Comment;
import javax.ejb.Local;

public interface CommentManager {
	
	public List<Comment> getComments(long lExperimentID, String sExperimentPhaseID);
	
	public List<Long> getCommentIDs(long lExperimentID, String sExperimentPhaseID);
	
	public Comment getComment(long lCommentID);
	
	public Comment getNewRootComment(long lExperimentID, String sExperimentPhaseID);
	
	public void registerComment(Comment comment, long lExperimentID, String sExperimentPhaseID);
	public void removeComment(long lCommentID);
	
	public void updateComment(Comment comment);
	
	public boolean containsComment(long commentID);
	
	public List<Comment> getCommentsByParent( Comment c );

}
