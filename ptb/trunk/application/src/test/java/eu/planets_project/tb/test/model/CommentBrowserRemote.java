package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.Comment;

@Remote
public interface CommentBrowserRemote {
	
	public long persistComment(Comment comment);
	public Comment findComment(long id);
	
	/**
	 * Fetches the given and already persisted BasicProperties object and updates it with given values.
	 * @param props The BasicProperties which is look-uped and contains the values for the update	
	 */
	public void updateComment(Comment comment);
	public void deleteComment(long id);
	public void deleteComment(Comment comment);


}
