/**
 * 
 */
package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.BasicProperties;
import eu.planets_project.tb.impl.model.Comment;

/**
 * @author alindley
 *
 */
@Stateless
public class CommentBrowser implements CommentBrowserRemote{

	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	
	public void deleteComment(long id) {
		Comment t_helper = manager.find(Comment.class, id);
		manager.remove(t_helper);
	}

	public void deleteComment(Comment comment) {
		Comment t_helper = manager.find(Comment.class, comment.getCommentID());
		manager.remove(t_helper);
	}

	public Comment findComment(long id) {
		return manager.find(Comment.class, id);
	}

	public long persistComment(Comment comment) {
		manager.persist(comment);
		return comment.getCommentID();
	}

	public void updateComment(Comment comment) {
		manager.merge(comment);
	}	

}
