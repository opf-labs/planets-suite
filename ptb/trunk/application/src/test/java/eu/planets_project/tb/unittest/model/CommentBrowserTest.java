/**
 * 
 */
package eu.planets_project.tb.unittest.model;

import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.CommentManager;
import eu.planets_project.tb.impl.model.Comment;
import eu.planets_project.tb.test.model.CommentBrowser;
import eu.planets_project.tb.test.model.CommentBrowserRemote;
import junit.framework.TestCase;

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
				jndiContext.lookup("CommentBrowser/remote"), CommentBrowserRemote.class);

			//create two test Comments, note their ID and persist them
			//new root comment with Comment(long lExperimentID, String sExperimentPhaseID)
			//please note: phaseID are not correct
			CommentManager manager = CommentManager.getInstance();
			//Comment com1 = (Comment)manager.getNewRootComment(1, "setup");
			Comment com1 = new Comment(1, "setup");
			commentID1 = dao_r.persistComment(com1);
			manager.registerComment(com1, 1, "setup");
			
			//new root comment
			//Comment com2 = (Comment)manager.getNewRootComment(2, "evaluation");
			//commentID2 = dao_r.persistComment(com2);
			
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
		Comment c1,c2;
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
		Comment test_find1 =  dao_r.findComment(commentID1);
		//modify the bean
		long l1 = 1;
		test_find1.setTitle("Title1");
		test_find1.setExperimentID(l1);
		dao_r.updateComment(test_find1);
		//Test1: updating existing entity
		test_find1 =  dao_r.findComment(commentID1);
		assertEquals("Title1",test_find1.getTile());	
	}
	
	public void testEJBEntityMerged(){
		Comment test_find1 =  dao_r.findComment(commentID1);
		//modify the bean
		long l1 = 12;
		test_find1.setTitle("Title1");
		test_find1.setExperimentID(l1);
		dao_r.updateComment(test_find1);
		//Test1: updating existing entity
		assertEquals("Title1",test_find1.getTile());
		
		//Test2: checking if merging entity works
		test_find1 =  dao_r.findComment(commentID1);
		test_find1.setTitle("TitleUpdated");
		dao_r.updateComment(test_find1);
		
		test_find1 =  dao_r.findComment(commentID1);
		assertEquals(l1,test_find1.getExperimentID());	
		assertEquals("TitleUpdated",test_find1.getTile());	
	}
	
	//Tests for the underlying Entity Bean's methods setter and getter's without any EJB issues
	public void testAddChildComment(){
		//TODO: Add CommentManager registration
		Comment test_find1 =  dao_r.findComment(commentID1);
		Comment com_child = new Comment(test_find1.getCommentID());
		com_child.setComment("Andrew", "TestChild", "Comment Text");
		dao_r.persistComment(com_child);
		
		test_find1 =  dao_r.findComment(commentID1);
		Vector<eu.planets_project.tb.api.model.Comment> vChilds = test_find1.getReplies();
		
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
