/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.HashMap;
import java.util.Vector;

import eu.planets_project.tb.api.model.Comment;

/**
 * @author alindley
 *
 */

public class CommentManager implements eu.planets_project.tb.api.CommentManager {
	
	/*
	 * The CommentManager is used for managing existing comments and for creating/registering new ones.
	 * It does not contain any information on the structure (Post-Replies) of the comments, as this is 
	 * located in the Comment Class itself.
	 * 
	 * The structure looks like:
	 * HashMap hmExperimentComments<ExperimentID, HashMap<PhaseID,Vector<Comments>>>
	 * and at the same time (mapped synchronously)
	 * HashMap hmCommentsMapping<CommentID,Comment>
	 */
	
	private HashMap<Long,HashMap<Long,Vector<Comment>>> hmExperimentComments;
	private HashMap<Long,Comment> hmCommentsMapping;
	
	private static CommentManager instance;
	
	private CommentManager(){
		hmExperimentComments = new HashMap<Long,HashMap<Long,Vector<Comment>>>();
		hmCommentsMapping = new HashMap<Long,Comment>();
	}
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized CommentManager getInstance(){
		if (instance == null){
			instance = new CommentManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getComment(java.lang.String)
	 */
	public Comment getComment(long commentID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getCommentIDs(java.lang.String)
	 */
	public Vector<String> getCommentIDs(String experimentPhaseID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getComments(java.lang.String)
	 */
	public Vector<Comment> getComments(String experimentPhaseID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getNewComment()
	 */
	public Comment getNewComment() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#registerComment(eu.planets_project.tb.api.model.Comment)
	 */
	public void registerComment(Comment comment) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#removeComment(java.lang.String)
	 */
	public void removeComment(long commentID) {
		// TODO Auto-generated method stub

	}

}
