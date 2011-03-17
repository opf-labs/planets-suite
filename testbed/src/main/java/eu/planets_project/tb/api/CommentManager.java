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
package eu.planets_project.tb.api;

import java.util.List;

import eu.planets_project.tb.api.model.Comment;

public interface CommentManager {
	
	public List<Comment> getComments(long lExperimentID, String sExperimentPhaseID);
	
	public List<Long> getCommentIDs(long lExperimentID, String sExperimentPhaseID);
	
	public Comment getComment(long lCommentID);
	
	public Comment getNewRootComment(long lExperimentID, String sExperimentPhaseID);
	
	public void registerComment(Comment comment, long lExperimentID, String sExperimentPhaseID);
	public void removeComment(long lCommentID);
	
	public void updateComment(Comment comment);
	
	public boolean containsComment(long commentID);
	
	public List<Comment> getCommentsByParent( Comment c );

}
