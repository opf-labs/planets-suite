/**
 * 
 */
package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.CommentImpl;

/**
 * @author alindley
 *
 */
@Stateless
public class CommentBrowser implements CommentBrowserRemote{

	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	
	public void deleteComment(long id) {
		CommentImpl t_helper = manager.find(CommentImpl.class, id);
		manager.remove(t_helper);
	}

	public void deleteComment(CommentImpl comment) {
		CommentImpl t_helper = manager.find(CommentImpl.class, comment.getCommentID());
		manager.remove(t_helper);
	}

	public CommentImpl findComment(long id) {
		return manager.find(CommentImpl.class, id);
	}

	public long persistComment(CommentImpl comment) {
		manager.persist(comment);
		return comment.getCommentID();
	}

	public void updateComment(CommentImpl comment) {
		manager.merge(comment);
	}	

}
