package eu.planets_project.tb.impl.persistency;

import java.util.List;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.api.persistency.CommentPersistencyRemote;

@Stateless
public class CommentPersistencyImpl implements CommentPersistencyRemote {
    
    @PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
    private EntityManager manager;

    // A logger for this:
    private Log log = LogFactory.getLog(CommentPersistencyImpl.class);   
    
    /**
     * A Factory method to build a reference to this interface.
     * @return
     */
    public static CommentPersistencyRemote getInstance() {
        Log log = LogFactory.getLog(CommentPersistencyImpl.class);
        try {
            Context jndiContext = new javax.naming.InitialContext();
            CommentPersistencyRemote dao_r = (CommentPersistencyRemote) PortableRemoteObject
                    .narrow(jndiContext
                            .lookup("testbed/CommentPersistencyImpl/remote"),
                            CommentPersistencyRemote.class);
            return dao_r;
        } catch (NamingException e) {
            log.error("Failure in getting PortableRemoteObject: "
                    + e.toString());
            return null;
        }
    }
    
    public void deleteComment(long id) {
        CommentImpl t_helper = manager.find(CommentImpl.class, id);
        manager.remove(t_helper);
    }

    public void deleteComment(Comment comment) {
        CommentImpl t_helper = manager.find(CommentImpl.class, comment.getCommentID());
        manager.remove(t_helper);
    }

    public Comment findComment(long id) {
        return manager.find(CommentImpl.class, id);
    }

    public long persistComment(Comment comment) {
        manager.persist(comment);
        return comment.getCommentID();
    }

    public void updateComment(Comment comment) {
        manager.merge(comment);
    }

    public List<Comment> getAllComments() {
        Query query = manager.createQuery("from CommentImpl");
        return (List<Comment>) query.getResultList();
    }
    
    public List<Comment> getComments(long experimentID, String experimentPhaseID) {        
        // TODO Move Entity declaration us so that we access Comments not CommenImpls?
        Query query = manager.createQuery("from CommentImpl where experimentID='"+experimentID+"'");
        return (List<Comment>) query.getResultList();
    }

}
