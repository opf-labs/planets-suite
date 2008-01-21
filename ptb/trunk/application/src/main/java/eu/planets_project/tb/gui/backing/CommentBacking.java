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
import eu.planets_project.tb.gui.UserBean;
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
import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeWalker;

/**
 * @author AnJackson
 *
 */
public class CommentBacking implements java.io.Serializable {
    static final long serialVerionUID = 1283216316723l;
    
    // A logger for this bean
    private Log log = LogFactory.getLog(CommentBacking.class);

    // The currently-being-edited comment:
    String commentId;
    String title;
    String comment;
    String parentId;
    String author;
    String time;
    
    // The comment manager:
    CommentManager cm = CommentManagerImpl.getInstance();
    
    // The experimental phase this comment pertains to
    String expPhase = ExperimentPhase.PHASENAME_EXPERIMENTSETUP;
    
    // The view-id that the comment was attached to:
    private String parentURI = null;

    // UI Data
    HtmlTree htmlTree;
    
    // Default maximum title length
    static int TITLE_LENGTH = 30;
    
    // NO PARENT indicator
    static int NO_PARENT = -1;
 

    /**
     *  Constructor initialises an empty comment:
     */
    public CommentBacking() {
        comment = "";
    }
    
    /**
     * @return the commentId
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * @param commentId the commentId to set
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
        if( commentId == null || "".equals(commentId) ) return;
        Comment c = cm.getComment(getlCommentID());
        this.title = c.getTitle();
        this.comment = c.getComment();
        this.parentId = new Long(c.getParentID()).toString();
        this.author = c.getAuthorID();
        this.expPhase = c.getExperimentPhaseID();
        // Format the date:
        java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance();
        this.time = df.format(c.getPostDate().getTime());
        this.storeCurrentURL();
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
        if( parentId == null || "".equals(parentId) ) return;
        this.storeCurrentURL();
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
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the tree
     */
    public HtmlTree getTree() {
        return htmlTree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(HtmlTree tree) {
        
        this.htmlTree = tree;
    }

    /**
     * 
     */
    public void addCommentAction() {
        log.info("Recieved addCommentAction()" );
        log.info("Recieved addCommentAction parentId = '" + parentId + "'" );
        log.info("Recieved addCommentAction expPhase = '" + expPhase + "'" );
        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Workflow"); 
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = null;
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        exp = testbedMan.getExperiment(expBean.getID());
        
        log.info("Recieved comment " + getComment() + " for " + expBean.getEname());
        
        Comment cmt = new CommentImpl(exp.getEntityID(), getExpPhase() );
        if( title == null || "".equals(title) ) {
            title = getComment();
            if ( title.length() > CommentBacking.TITLE_LENGTH ) 
                title = getComment().substring(0, CommentBacking.TITLE_LENGTH );
        }
        // User is?
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        // Existing comment?
        if( ! "".equals(commentId) && commentId != null ) {
            cmt = cm.getComment(this.getlCommentID());
        } else {
            cmt.setAuthorID(user.getUserid());
            cmt.setExperimentPhaseID( expBean.getCurrentPhaseName() );
        }
        // Edit/update the comment:
        cmt.setParentID( getlParentID() );
        cmt.setExperimentID(exp.getEntityID());
        cmt.setPostDate( java.util.Calendar.getInstance() );
        cmt.setComment(title , this.comment );
        

        // Add or update, depending on commendId:
        if( "".equals(commentId) ) {
            cm.registerComment(cmt, expBean.getID(), getExpPhase() );
        } else {
            cm.updateComment(cmt);
        }
        
        // Un-set the comment and title as they have now been used:
        this.setTitle("");
        this.setComment("");
        this.setParentId("");
        this.setExpPhase("");
        this.setCommentId("");
        this.author = "";
        this.time = "";
        
        // Redirect to the original page, if it is set:
        if( parentURI == null || parentURI == "" ) return;
        try {
          javax.faces.context.FacesContext.getCurrentInstance().getExternalContext().redirect(parentURI);
        } catch ( java.io.IOException e) {
            log.error("Redirect failed with exception: " + e);
        }
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

    /**
     * Backing for the Tomahawk Tree2 I'm using for displaying the comments.
     * @return A TreeModel holding the comments.
     */
    public TreeModel getCommentTree() {
        // Get the comments.
        List<Comment> cmts = this.getAllComments();
        
        // Build the tree.
        TreeNode tn = new CommentTreeNode();
        tn.setType("comments"); tn.setLeaf(false);

        // Create the tree:
        TreeModel tm = new TreeModelBase(tn);

        // Add child nodes:
        this.getChildComments(tm, tn, cmts);
        
        // Add a child for the reply box:
        TreeNode tncb = new CommentTreeNode();
        tncb.setType("commentbox"); tncb.setLeaf(true);
        tn.getChildren().add(tncb);
        
        return tm;
    }
    
    private void getChildComments( TreeModel tm, TreeNode parent, List<Comment> cmts ) {
        // Do nothing if there are no comments.
        if( cmts.size() == 0 ) return;
        
        // Iterate over the children:
        for (java.util.Iterator<Comment> cit = cmts.iterator(); cit.hasNext (); ) {
            Comment c = cit.next();
            CommentTreeNode cnode = new CommentTreeNode();
            cnode.setTitle(c.getTitle());
            cnode.setBody(c.getComment());
            cnode.setIdentifier(new Long(c.getCommentID()).toString());
            cnode.setAuthor(c.getAuthorID());
            // Format the date:
            java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance();
            cnode.setTime( df.format(c.getPostDate().getTime()) );
            cnode.setExpPhase(c.getExperimentPhaseID());
            cnode.setLeaf(false);
            // Add the child element to the tree:
            List<CommentTreeNode> cchilds = (List<CommentTreeNode>) parent.getChildren();
            cchilds.add(cnode);
            // Look for sub-comments:
            List<Comment> ccmt = cm.getCommentsByParent(c);
            // If there are any, add them via recursion:
            if( ccmt.size() > 0 ) 
                this.getChildComments(tm, cnode, ccmt);
        }
        
    }
    
    /**
     * 
     * @return
     */
    public CommentBacking getParentComment() {
        CommentBacking cb = new CommentBacking();
        cb.setCommentId(parentId);
        return cb;
    }
    
    /**
     * 
     * @return
     */
    public long getlCommentID() {
        if( commentId == null || "".equals(commentId) ) return -1;
        return Long.parseLong(commentId);
    }

    /**
     * 
     * @return
     */
    public long getlParentID() {
        if( parentId == null || "".equals(parentId) ) return NO_PARENT;
        return Long.parseLong(parentId);
    }

    /**
     * 
     * @return
     */
    public String expandAll()
    {
      if( htmlTree != null )
        htmlTree.expandAll();
      return null;
    }
    
    /**
     * 
     * @return
     */
    public String collapseAll()
    {
      if( htmlTree != null )
        htmlTree.collapseAll();
      return null;
    }
    
    /**
     * Stores the URL needed to drop the user back at the page they initiated the comment editor from.
     */
    private void storeCurrentURL() {
        // Pick up the view-id that invoked this method:
        javax.faces.context.ExternalContext fec = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
        this.parentURI = fec.getRequestContextPath();
        if( fec.getRequestServletPath() != null )
            this.parentURI += fec.getRequestServletPath();
        if( fec.getRequestPathInfo() != null )
            this.parentURI += fec.getRequestPathInfo();
    }

    /**
     * Code taken from org.apache.myfaces.custom.tree2.UITreeData
     * @param expanded
     */
    private void toggleAll(boolean expanded) {
        TreeWalker walker = htmlTree.getDataModel().getTreeWalker();
        walker.reset();

        TreeState state =  htmlTree.getDataModel().getTreeState();
        walker.setCheckState(false);
        walker.setTree(htmlTree);

        while(walker.next())
        {
            String id = htmlTree.getNodeId();
            if ((expanded && !state.isNodeExpanded(id)) || (!expanded && state.isNodeExpanded(id)))
            {
                state.toggleExpanded(id);
            }
        }
    }
    
    public String getTime() {
        return time;
    }
}
