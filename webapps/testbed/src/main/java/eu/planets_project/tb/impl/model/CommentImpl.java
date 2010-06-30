/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.CommentManagerImpl;

/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class CommentImpl implements Comment, java.io.Serializable {
	
	@Id
	@GeneratedValue
	@XmlTransient
	private long commentID;
    // Also store the comment identifier in the serialised form, but separately from the @Id
    private long xmlCommentID;
    // The ID of the parent comment:
	private long parentID = -1;
	// The ID of the parent Experiment
	@XmlTransient
	private long experimentID;
	// The point at which the comment was added.
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

    /**
     * @param commentID the commentID to set
     */
    public void setCommentID(long commentID) {
        this.commentID = commentID;
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

    /* For serialisation */
    
    /**
     * @return the xmlCommentID
     */
    public long getXmlCommentID() {
        return xmlCommentID;
    }

    /**
     * @param xmlCommentID the xmlCommentID to set
     */
    public void setXmlCommentID(long xmlCommentID) {
        this.xmlCommentID = xmlCommentID;
    }

}
