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
package eu.planets_project.tb.api.persistency;

import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.tb.api.model.Comment;

@Remote
public interface CommentPersistencyRemote {
	
	public long persistComment(Comment comment);
	public Comment findComment(long id);
	
	public void updateComment(Comment comment);
	public void deleteComment(long id);
	public void deleteComment(Comment comment);
	public List<Comment> getAllComments();
    public List<Comment> getAllComments( long experimentID );
    public List<Comment> getComments(long experimentID, String experimentPhaseID);
    public List<Comment> getCommentsByParent(long commentID );    
	
}
