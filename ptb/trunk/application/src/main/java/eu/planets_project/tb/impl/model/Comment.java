/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.impl.UserManager;
import eu.planets_project.tb.impl.model.User;
import eu.planets_project.tb.impl.CommentManager;

/**
 * @author alindley
 *
 */
@Entity
public class Comment implements eu.planets_project.tb.api.model.Comment,
								java.io.Serializable{
	
	@Id
	@GeneratedValue
	private long lCommentID, lAuthorID, lParentID;
	private Vector<Long> vChildIDs;
	private String sExperimentPhaseID;
	private String sTitle, sComment, sAuthorName;
	//time in millis
	private GregorianCalendar postDate;
	
	public Comment(long lParentID, String sExperimentPhaseID){

		this.sExperimentPhaseID = sExperimentPhaseID;
		this.lParentID = lParentID;
		
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
	 * @see eu.planets_project.tb.api.model.Comment#getParent()
	 */
	public eu.planets_project.tb.api.model.Comment getParent() {
		
		return CommentManager.getInstance().getComment(this.lParentID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getPostDate()
	 */
	public GregorianCalendar getPostDate() {
		return this.postDate;
	}
	
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getTile()
	 */
	public String getTile() {
		return this.sTitle;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setAuthor(java.lang.String)
	 */
	public void setAuthor(long lAuthorID) {
		this.lAuthorID = lAuthorID;
		this.sAuthorName = UserManager.getInstance().getUser(lAuthorID).getName();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setAuthor(eu.planets_project.tb.api.model.User)
	 */
	public void setAuthor(eu.planets_project.tb.api.model.User author) {
		this.lAuthorID = author.getUserID();
		this.sAuthorName = author.getName();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setComment(java.lang.String, java.lang.String, java.util.GregorianCalendar)
	 */
	public void setComment(String authorName, String title, String commentText) {
		GregorianCalendar temp = new GregorianCalendar();
		this.postDate = temp;
		this.sTitle = title;
		this.sAuthorName = authorName;
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
	public void setPostDate(GregorianCalendar Date) {
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
	public Vector<eu.planets_project.tb.api.model.Comment> getReplies() {
		Vector<eu.planets_project.tb.api.model.Comment> vChilds = new Vector<eu.planets_project.tb.api.model.Comment>();
		Iterator<Long> itChilds = this.vChildIDs.iterator();
		for (int i=0; i<this.vChildIDs.size(); i++){
			Comment comment = (Comment) CommentManager.getInstance().getComment(itChilds.next());
			vChilds.addElement(comment);
		}
		return vChilds;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#addReply(eu.planets_project.tb.api.model.Comment)
	 */
	public void addReply(eu.planets_project.tb.api.model.Comment reply) {
		this.vChildIDs.addElement(reply.getCommentID());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#removeReply(eu.planets_project.tb.api.model.Comment)
	 */
	public void removeReply(eu.planets_project.tb.api.model.Comment reply) {
		this.vChildIDs.removeElement(reply.getCommentID());
	}

	/* (non-Javadoc)
	 * SetReplies removes all replies that have been added until now. To modify use add and remove
	 * @see eu.planets_project.tb.api.model.Comment#setReplies(java.util.Vector)
	 */
	public void setReplies(Vector<eu.planets_project.tb.api.model.Comment> replies) {
		//setReplies removes all replies that have been added until now
		this.vChildIDs.removeAllElements();
		
		Enumeration<eu.planets_project.tb.api.model.Comment> enumReplies = replies.elements();
		while (enumReplies.hasMoreElements()){
			eu.planets_project.tb.api.model.Comment commentReply = enumReplies.nextElement();
			long replyID = commentReply.getCommentID();
			this.vChildIDs.addElement(replyID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getAuthor()
	 */
	public eu.planets_project.tb.api.model.User getAuthor() {
		return 	UserManager.getInstance().getUser(this.lAuthorID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getAuthorID()
	 */
	public long getAuthorID() {
		return this.lAuthorID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getAuthorName()
	 */
	public String getAuthorName() {
		return this.sAuthorName;
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
