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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;

/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class CommentImpl implements Comment, java.io.Serializable {
	
	@Id
	@GeneratedValue
	private long commentID;
	private long parentID = -1;
	private long experimentID;
	private String experimentPhaseID;
	private String title;
	private String comment;
	private String authorID;
	//time in millis
	private Calendar postDate;
	
	//Default constructor required for EJB persistency
	public CommentImpl(){
	}
	
	/**
	 * Is used to create a new root comment, without any parents
	 * @param lExperimentID
	 * @param sExperimentPhaseID
	 */
	public CommentImpl(long lExperimentID, String sExperimentPhaseID){

		this.experimentID = lExperimentID;
		this.experimentPhaseID = sExperimentPhaseID;
		//this is a new root comment
		this.parentID = -1;
		
	}
	
	/**
	 * Used to create a child comment. Attributes lExperimentID and sExperimentPhaseID are retrieved from the Comment(parentID)
	 * @param parentID
	 */
	public CommentImpl(long parentID){
		this.parentID = parentID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getCommentID()
	 */
	public long getCommentID() {
		return this.commentID;
	}

    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getExperimentPhaseID()
	 */
	public String getExperimentPhaseID() {
		return this.experimentPhaseID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setExperimentID(long)
	 */
	public void setExperimentID(long lID){
		this.experimentID = lID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getExperimentID()
	 */
	public long getExperimentID(){
		return this.experimentID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getParent()
	 */
	public Comment getParent() {
		
		return CommentManagerImpl.getInstance().getComment(this.parentID);
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
	public String getTitle() {
		return this.title;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setAuthorID(java.lang.String)
	 */
	public void setAuthorID(String sAuthorID) {
		this.authorID = sAuthorID;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setComment(java.lang.String, java.lang.String, java.util.GregorianCalendar)
	 */
	public void setComment(String authorID, String title, String commentText) {
		GregorianCalendar temp = new GregorianCalendar();
		this.postDate = temp;
		this.title = title;
		this.authorID = authorID;
		this.comment = commentText;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setComment(java.lang.String, java.lang.String)
	 */
	public void setComment(String title, String commentText){
		GregorianCalendar temp = new GregorianCalendar();
		this.postDate = temp;
		this.title = title;
		this.comment = commentText;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#setExperimentPhaseID(java.lang.String)
	 */
	public void setExperimentPhaseID(String sid) {
		this.experimentPhaseID = sid;
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
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getReplies()
	 */
	public List<Comment> getReplies() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getAuthorID()
	 */
	public String getAuthorID() {
		return this.authorID;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getComment()
	 */
	public String getComment() {
		return this.comment;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Comment#getPostDateInMillis()
	 */
	public long getPostDateInMillis() {
		return this.postDate.getTimeInMillis();
	}

    /**
     * @return the parentID
     */
    public long getParentID() {
        return parentID;
    }

    /**
     * @param parentID the parentID to set
     */
    public void setParentID(long parentID) {
        this.parentID = parentID;
    }

}
