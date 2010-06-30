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
package eu.planets_project.tb.unittest.model;

import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.test.model.CommentBrowserRemote;

/**
 * @author alindley
 *
 */
public class CommentBrowserTest extends TestCase{
	
	Context jndiContext;
	CommentBrowserRemote dao_r;
	
	private long commentID1, commentID2;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();

			dao_r = (CommentBrowserRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("testbed/CommentBrowser/remote"), CommentBrowserRemote.class);

			//create two test Comments, note their ID and persist them
			//new root comment with Comment(long lExperimentID, String sExperimentPhaseID)
			//please note: phaseID are not correct
			CommentManagerImpl manager = CommentManagerImpl.getInstance();
			CommentImpl com1 = (CommentImpl)manager.getNewRootComment(1, "setup");
			//Comment com1 = new Comment(1, "setup");
			//System.out.println("Contains? "+manager.containsComment(1));
			commentID1 = dao_r.persistComment(com1);
			CommentImpl find_com1 = dao_r.findComment(commentID1);
			manager.registerComment(find_com1, find_com1.getExperimentID(),find_com1.getExperimentPhaseID());
			System.out.println("XXXContains? "+manager.containsComment(commentID1));
			
			//new root comment
			CommentImpl com2 = (CommentImpl)manager.getNewRootComment(2, "evaluation");
			commentID2 = dao_r.persistComment(com2);
			CommentImpl find_com2 = dao_r.findComment(commentID2);
			manager.registerComment(find_com2, find_com2.getExperimentID(),find_com2.getExperimentPhaseID());
			System.out.println("XXXContains? "+manager.containsComment(commentID2));
			
			
		} catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Setup: Exception in while setUp: "+e.toString());
		}
	}
	
// Tests all EJB persistency related issues:
	
	public void testEJBEntityCreated(){
		assertNotNull(dao_r.findComment(this.commentID1));
	}
	
	public void testEJBEntityDeleted(){
		dao_r.deleteComment(this.commentID1);
		dao_r.deleteComment(dao_r.findComment(commentID2));
		CommentImpl c1,c2;
		try{
			c1 = dao_r.findComment(commentID1);
			c2 = dao_r.findComment(commentID2);
			
		}catch(Exception e){
			c1 = null;
			c2 = null;
		}
		assertNull(c1);
		assertNull(c2);	
	}
	
	public void testEJBEntityUpdated(){
		CommentImpl test_find1 =  dao_r.findComment(commentID1);
		//modify the bean
		long l1 = 1;
		test_find1.setTitle("Title1");
		test_find1.setExperimentID(l1);
		dao_r.updateComment(test_find1);
		//Test1: updating existing entity
		test_find1 =  dao_r.findComment(commentID1);
		assertEquals("Title1",test_find1.getTitle());	
	}
	
	public void testEJBEntityMerged(){
		CommentImpl test_find1 =  dao_r.findComment(commentID1);
		//modify the bean
		long l1 = 12;
		test_find1.setTitle("Title1");
		test_find1.setExperimentID(l1);
		dao_r.updateComment(test_find1);
		//Test1: updating existing entity
		assertEquals("Title1",test_find1.getTitle());
		
		//Test2: checking if merging entity works
		test_find1 =  dao_r.findComment(commentID1);
		test_find1.setTitle("TitleUpdated");
		dao_r.updateComment(test_find1);
		
		test_find1 =  dao_r.findComment(commentID1);
		assertEquals(l1,test_find1.getExperimentID());	
		assertEquals("TitleUpdated",test_find1.getTitle());	
	}
	
	//Tests for the underlying Entity Bean's methods setter and getter's without any EJB issues
	public void testAddChildComment(){
		//TODO: Add CommentManager registration
		CommentImpl test_find1 =  dao_r.findComment(commentID1);
		System.out.println("Comment Partent ID: "+test_find1.getCommentID());
		CommentImpl com_child = new CommentImpl(test_find1.getCommentID());
		com_child.setComment("Andrew", "TestChild", "Comment Text");
		dao_r.persistComment(com_child);

		test_find1 =  dao_r.findComment(commentID1);
		System.out.println("Comment Partent ID: "+test_find1.getCommentID());
		System.out.println("Comment Child ID: "+com_child.getCommentID());
		
		Vector<Comment> vChilds = (Vector<Comment>)test_find1.getReplies();
		
		assertEquals(1,vChilds.size());	
		
		
	}
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}
	
	protected void tearDown(){
		try{
			dao_r.deleteComment(this.commentID1);
			dao_r.deleteComment(this.commentID2);
		}
		catch(Exception e){
			//TODO Integrate with Logging Framework
			System.out.println("TearDown: Exception while tearDown: "+e.toString());
			}
	}

}
