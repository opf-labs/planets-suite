package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.CommentImpl;

@Remote
public interface CommentBrowserRemote {
	
	public long persistComment(CommentImpl comment);
	public CommentImpl findComment(long id);
	
	/**
	 * Fetches the given and already persisted BasicProperties object and updates it with given values.
	 * @param props The BasicProperties which is look-uped and contains the values for the update	
	 */
	public void updateComment(CommentImpl comment);
	public void deleteComment(long id);
	public void deleteComment(CommentImpl comment);


}
