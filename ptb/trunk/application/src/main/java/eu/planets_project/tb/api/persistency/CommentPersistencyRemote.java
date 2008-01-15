package eu.planets_project.tb.api.persistency;

import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.tb.api.model.Comment;

@Remote
public interface CommentPersistencyRemote {
	
	public long persistComment(Comment comment);
	public Comment findComment(long id);
	
	public void updateComment(Comment comment);
	public void deleteComment(long id);
	public void deleteComment(Comment comment);
	public List<Comment> getAllComments();
    public List<Comment> getComments(long experimentID, String experimentPhaseID);
	
}
