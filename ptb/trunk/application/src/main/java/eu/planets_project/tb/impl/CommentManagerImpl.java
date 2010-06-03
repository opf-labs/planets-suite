/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.api.persistency.CommentPersistencyRemote;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.persistency.CommentPersistencyImpl;

/**
 * TODO Determine what the role of this class should be as it merely wraps the CommentPersistencyImpl at present.
 * As the Entity wrapper class only does really basic row-management, perhaps this should deal with the multiple-row stuff like 'getAllChildComments?'
 * 
 * @author alindley
 *
 */
public class CommentManagerImpl implements CommentManager {
    
    // A Log for this:
    @SuppressWarnings("unused")
	private Log log = LogFactory.getLog(CommentManagerImpl.class);

	private static CommentManagerImpl instance;
	private CommentPersistencyRemote cmp;
	
	private CommentManagerImpl(){
	    cmp = CommentPersistencyImpl.getInstance();
	}
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized CommentManagerImpl getInstance(){
		if (instance == null){
			instance = new CommentManagerImpl();
		}
		return instance;
	}

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.CommentManager#getComment(java.lang.String)
     */
    public Comment getComment(long commentID) {
        return cmp.findComment(commentID);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.CommentManager#getCommentIDs(long, java.lang.String)
     */
    public List<Long> getCommentIDs(long experimentID, String experimentPhaseID) {
        return null;
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getComments(long, java.lang.String)
	 */
	public List<Comment> getComments(long experimentID, String experimentPhaseID) {
        return cmp.getComments(experimentID, experimentPhaseID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getNewComment()
	 */
	public Comment getNewRootComment(long lExperimentID, String sExperimentPhaseID) {
		CommentImpl c1 = new CommentImpl(lExperimentID, sExperimentPhaseID);
		return c1;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#removeComment(java.lang.String)
	 */
	public void removeComment(long commentID) {
	    cmp.deleteComment(commentID);
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#registerComment(Comment, long, java.lang.String)
	 */
	public void registerComment(
			eu.planets_project.tb.api.model.Comment comment, long experimentID,
			String experimentPhaseID) {
		cmp.persistComment(comment);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#updateComment(eu.planets_project.tb.api.model.Comment)
	 */
	public void updateComment(Comment comment) {
	    cmp.updateComment(comment);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#containsComment(long)
	 */
	public boolean containsComment(long commentID) {
		return false;
	}
	
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.CommentManager#getCommentsByParent(Comment)
     */
    public List<Comment> getCommentsByParent( Comment c ) {
        return cmp.getCommentsByParent(c.getCommentID());
    }

}
