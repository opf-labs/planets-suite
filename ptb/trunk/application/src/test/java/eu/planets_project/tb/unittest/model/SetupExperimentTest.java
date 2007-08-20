package eu.planets_project.tb.unittest.model;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.TestbedManager;
import eu.planets_project.tb.impl.model.BasicProperties;
import eu.planets_project.tb.impl.model.Experiment;
import eu.planets_project.tb.impl.model.ExperimentResources;
import eu.planets_project.tb.impl.model.ExperimentSetup;
import eu.planets_project.tb.test.model.ExperimentPersistencyRemote;
import junit.framework.TestCase;

public class SetupExperimentTest extends TestCase{
	
	Context jndiContext;
	ExperimentPersistencyRemote dao_r;
	
	private long expID1, expID2;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();

			dao_r = (ExperimentPersistencyRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("ExperimentPersistency/remote"), ExperimentPersistencyRemote.class);

			//create two test Experiments, note their ID and persist them
			TestbedManager manager = TestbedManager.getInstance();
			Experiment exp1 = new Experiment();
			expID1 = dao_r.persistExperiment(exp1);
			Experiment find_exp1 = dao_r.findExperiment(expID1);
			//TODO: still need to test TestbedManager
			manager.registerExperiment(find_exp1);
			System.out.println("Contains? "+manager.containsExperiment(expID1));
			
			//create second test Experiment
			Experiment exp2 = new Experiment();
			expID2 = dao_r.persistExperiment(exp2);
			Experiment find_exp2 = dao_r.findExperiment(expID2);
			manager.registerExperiment(find_exp2);
			System.out.println("Contains? "+manager.containsExperiment(expID2));
			
			
		} catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Setup: Exception in while setUp: "+e.toString());
		}
	}
	
// Tests all EJB persistency related issues:
	
	/*public void testEJBEntityCreated(){
		assertNotNull(dao_r.findExperiment(this.expID1));
	}
	
	public void testEJBEntityDeleted(){
		dao_r.deleteExperiment(this.expID1);
		dao_r.deleteExperiment(dao_r.findExperiment(expID2));
		Experiment c1,c2;
		try{
			c1 = dao_r.findExperiment(expID1);
			c2 = dao_r.findExperiment(expID2);
			
		}catch(Exception e){
			c1 = null;
			c2 = null;
		}
		assertNull(c1);
		assertNull(c2);	
	}*/
	
	/*public void testEJBEntityUpdated(){
		Experiment test_find1 =  dao_r.findExperiment(expID1);
		//modify the bean
		long l1 = 1;
		test_find1.setTitle("Title1");
		test_find1.setExperimentID(l1);
		dao_r.updateComment(test_find1);
		//Test1: updating existing entity
		test_find1 =  dao_r.findComment(commentID1);
		assertEquals("Title1",test_find1.getTile());	
	}*/
	
	/*public void testEJBEntityMerged(){
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
	}*/
	
	
	//Tests for the underlying Entity Bean's methods setter and getters
	public void testSetExperimentSetup(){
		Experiment exp_find1 = dao_r.findExperiment(expID1);
		//use the private helper method to setup the ExperimentSetup
		ExperimentSetup expSetup = createEnvironmentExperimentSetup(1);
		//Test1: add ExperimentSetup
			exp_find1.setExperimentSetup(expSetup);
			dao_r.updateExperiment(exp_find1);

			exp_find1 = dao_r.findExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			//must also have an ID assigned through @OneToOne(cascade={CascadeType.ALL})
			assertTrue(exp_find1.getExperimentSetup().getExperimentSetupID()>0);
		
		//Test2: modify ExperimentSetup
			exp_find1 = dao_r.findExperiment(expID1);
			expSetup = createEnvironmentExperimentSetup(2);
			exp_find1.setExperimentSetup(expSetup);
			dao_r.updateExperiment(exp_find1);
			
			exp_find1 = dao_r.findExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			assertEquals("ExperimentName2", exp_find1.getExperimentSetup().getBasicProperties().getExperimentName());
			assertTrue(exp_find1.getExperimentSetup().getExperimentSetupID()>0);
	
	}
	
	/**
	 * Note: The ExperimentResources Object contains an estimate on the required resources
	 */
	/*public void testExperimentResources(){
		
	}*/
	
	private ExperimentSetup createEnvironmentExperimentSetup(int testnr){
		ExperimentSetup expSetup = new ExperimentSetup();
		BasicProperties props = new BasicProperties();
		props.setConsiderations("considerations"+testnr);
		props.setExperimentName("ExperimentName"+testnr);
		expSetup.setBasicProperties(props);
		
		return expSetup;
	}
	
	/*private ExperimentResources createEnvironmentExperimentResources(int testnr){
		return null;
	}*/
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}
	
	protected void tearDown(){
		try{
			dao_r.deleteExperiment(this.expID1);
			dao_r.deleteExperiment(this.expID2);
		}
		catch(Exception e){
			//TODO Integrate with Logging Framework
			System.out.println("TearDown: Exception while tearDown: "+e.toString());
			}
	}

}
