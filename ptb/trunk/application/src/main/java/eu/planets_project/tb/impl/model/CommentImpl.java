/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;

/**
 * @author alindley
 *
 */
@Entity
public class CommentImpl implements eu.planets_project.tb.api.model.Comment,
								java.io.Serializable{
	
	@Id
	@GeneratedValue
	private long lCommentID;
	private long lParentID, lExperimentID;
	//vChildIDs is a flat structure of Comments just in the next level (not recursively)
	private Vector<Long> vChildIDs;
	private String sExperimentPhaseID;
	private String sTitle, sComment, sAuthorID;
	//time in millis
	private Calendar postDate;
	
	//Default constructor required for EJB persistency
	private CommentImpl(){
	}
	
	/**
	 * Is used to create a new root comment, without any parents
	 * @param lExperimentID
	 * @param sExperimentPhaseID
	 */
	public CommentImpl(long lExperimentID, String sExperimentPhaseID){

		this.lExperimentID = lExperimentID;
		this.sExperimentPhaseID = sExperimentPhaseID;
		//this is a new root comment
		this.lParentID = -1;
		
		this.vChildIDs = new Vector<Long>();
		
	}
	
	/**
	 * Used to create a child comment. Attributes lExperimentID and sExperimentPhaseID are retrieved from the Comment(lParentID)
	 * @param lParentID
	 */
	public CommentImpl(long lParentID){
		CommentManagerImpl manager = (CommentManagerImpl)TestbedManagerImpl.getInstance(true).getCommentManagerInstance();
		CommentImpl parent = (CommentImpl)manager.getComment(lParentID);
		//DELTE
		System.out.println("parent NULL? ");
		if (parent==null){
			System.out.println("Contains? "+manager.containsComment(lParentID));
			System.out.println("parent =NULL! ");
		}
		
		try{
			System.out.println("IN TRY0");
			this.sExperimentPhaseID = parent.getExperimentPhaseID();
			System.out.println("IN TRY1: "+this.sExperimentPhaseID);
			this.lParentID = parent.getCommentID();
			System.out.println("IN TRY2: "+this.lParentID);
			this.lExperimentID = parent.getExperimentID();
			System.out.println("IN TRY3: "+this.lExperimentID);
		}catch(Exception e){
			System.out.println("In TRY ERROR "+e.toString());
		}
		//END DELETE
		
		//Child element shares the following attributes with its parent
		this.sExperimentPhaseID = parent.getExperimentPhaseID();
		this.lParentID = parent.getCommentID();
		this.lExperimentID = parent.getExperimentID();
		System.out.println("IN Comment: "+this.sExperimentPhaseID+ " "+this.lParentID+ " "+this.lExperimentID);
		//add the child comment to the parent's replies
		parent.addReply(this);
		System.out.println("IN Comment4");
		manager.updateComment(parent);
		System.out.println("IN Comment5");
		
		this.vChildIDs = new Vector<Long>();
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getCommentID()
	 */
	public long getCommentID() {
		return this.lCommentID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getExperimentPhaseID()
	 */
	public String getExperimentPhaseID() {
		return this.sExperimentPhaseID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setExperimentID(long)
	 */
	public void setExperimentID(long lID){
		this.lExperimentID = lID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getExperimentID()
	 */
	public long getExperimentID(){
		return this.lExperimentID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getParent()
	 */
	public Comment getParent() {
		
		return CommentManagerImpl.getInstance().getComment(this.lParentID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getPostDate()
	 */
	public Calendar getPostDate() {
		return this.postDate;
	}
	
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getTile()
	 */
	public String getTile() {
		return this.sTitle;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setAuthorID(java.lang.String)
	 */
	public void setAuthorID(String sAuthorID) {
		this.sAuthorID = sAuthorID;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setComment(java.lang.String, java.lang.String, java.util.GregorianCalendar)
	 */
	public void setComment(String authorID, String title, String commentText) {
		GregorianCalendar temp = new GregorianCalendar();
		this.postDate = temp;
		this.sTitle = title;
		this.sAuthorID = authorID;
		this.sComment = commentText;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setComment(java.lang.String, java.lang.String)
	 */
	public void setComment(String title, String commentText){
		GregorianCalendar temp = new GregorianCalendar();
		this.postDate = temp;
		this.sTitle = title;
		this.sComment = commentText;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setExperimentPhaseID(java.lang.String)
	 */
	public void setExperimentPhaseID(String sid) {
		this.sExperimentPhaseID = sid;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setPostDate(java.util.GregorianCalendar)
	 */
	public void setPostDate(Calendar Date) {
		this.postDate = Date;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.sTitle = title;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getReplies()
	 */
	public List<Comment> getReplies() {
		Vector<eu.planets_project.tb.api.model.Comment> vChilds = new Vector<eu.planets_project.tb.api.model.Comment>();
		Iterator<Long> itChilds = this.vChildIDs.iterator();
		for (int i=0; i<this.vChildIDs.size(); i++){
			CommentImpl comment = (CommentImpl) CommentManagerImpl.getInstance().getComment(itChilds.next());
			vChilds.addElement(comment);
		}
		return vChilds;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#addReply(eu.planets_project.tb.api.model.Comment)
	 */
	public void addReply(Comment reply) {
		this.vChildIDs.addElement(reply.getCommentID());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#removeReply(eu.planets_project.tb.api.model.Comment)
	 */
	public void removeReply(Comment reply) {
		this.vChildIDs.removeElement(reply.getCommentID());
	}

	/* (non-Javadoc)
	 * SetReplies removes all replies that have been added until now. To modify use add and remove
	 * @see eu.planets_project.tb.api.model.Comment#setReplies(java.util.Vector)
	 */
	public void setReplies(List<Comment> replies) {
		//setReplies removes all replies that have been added until now
		this.vChildIDs.removeAllElements();
		
		Iterator<Comment> itReplies = replies.iterator();
		while (itReplies.hasNext()){
			CommentImpl commentReply = (CommentImpl)itReplies.next();
			long replyID = commentReply.getCommentID();
			this.vChildIDs.addElement(replyID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getAuthorID()
	 */
	public String getAuthorID() {
		return this.sAuthorID;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getComment()
	 */
	public String getComment() {
		return this.sComment;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getPostDateInMillis()
	 */
	public long getPostDateInMillis() {
		return this.postDate.getTimeInMillis();
	}

}
