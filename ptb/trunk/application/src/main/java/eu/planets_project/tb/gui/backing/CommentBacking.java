/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.CommentManagerImpl;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeState;
import org.apache.myfaces.custom.tree2.TreeStateBase;

/**
 * @author AnJackson
 *
 */
public class CommentBacking {
    
    // A logger for this bean
    private Log log = LogFactory.getLog(CommentBacking.class);

    // The currently-being-edited comment:
    String comment;
    
    // The comment manager:
    CommentManager cm = CommentManagerImpl.getInstance();
    
    // The experimental phase this comment pertains to
    String expPhase = ExperimentPhase.PHASENAME_EXPERIMENTSETUP;
 

    /**
     *  Constructor initialises an empty comment:
     */
    public CommentBacking() {
        comment = "";
    }
    
    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
    /**
     * @return the expPhase
     */
    public String getExpPhase() {
        return expPhase;
    }

    /**
     * @param expPhase the expPhase to set
     */
    public void setExpPhase(String expPhase) {
        this.expPhase = expPhase;
    }

    /**
     * 
     */
    public void addCommentAction() {
        log.info("Recieved addCommentAction()" );
        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow"); 
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        exp = testbedMan.getExperiment(expBean.getID());
        
        log.info("Recieved comment " + getComment() + " for " + expBean.getEname());
        
        Comment cmt = new CommentImpl(exp.getEntityID(), getExpPhase() );
        String title = getComment();
        if ( title.length() > 10 ) title = getComment().substring(0, 10);
        cmt.setExperimentID(exp.getEntityID());
        cmt.setExperimentPhaseID(getExpPhase());
        cmt.setPostDate( java.util.Calendar.getInstance() );
        cmt.setComment(title , this.comment );
        cmt.setAuthorID("fred");
        
        cm.registerComment(cmt, expBean.getID(), getExpPhase() );
    }

    /**
     * Get all the comment on the current part...
     * @return
     */
    public List<Comment> getAllComments() {
        //TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        //Experiment exp = testbedMan.getExperiment(expBean.getID());
        
        return cm.getComments(expBean.getID(), getExpPhase() );
    }

    public TreeModel getCommentTree() {
        // Get the comments.
        List<Comment> cmts = this.getAllComments();
        // Build the tree.
        TreeNode tn = new CommentTreeNode();
        tn.setType("comment"); tn.setLeaf(false);
        
        for (java.util.Iterator<Comment> it = cmts.iterator (); it.hasNext (); ) {
            Comment c = it.next();
            CommentTreeNode cnode = new CommentTreeNode();
            cnode.setAuthor(c.getAuthorID());
            cnode.setTitle(c.getTitle());
            cnode.setBody(c.getComment());
            cnode.setTime(c.getPostDate().toString());
            List<CommentTreeNode> cchilds = (List<CommentTreeNode>) tn.getChildren();
            cchilds.add(cnode);
        }
        
        TreeModel tree = new TreeModelBase(tn);
        
        return tree;
    }

}
