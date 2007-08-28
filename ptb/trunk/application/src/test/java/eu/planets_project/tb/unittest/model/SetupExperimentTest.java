package eu.planets_project.tb.unittest.model;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
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
			TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
			ExperimentImpl exp1 = new ExperimentImpl();
			expID1 = dao_r.persistExperiment(exp1);
			ExperimentImpl find_exp1 = (ExperimentImpl)dao_r.findExperiment(expID1);
			System.out.println("OBj: "+find_exp1);
			System.out.println("ExpID1: "+expID1+ "find_exp1ID: "+find_exp1.getEntityID());
			//TODO: still need to test TestbedManager
			manager.registerExperiment(find_exp1);
			System.out.println("Contains? "+manager.containsExperiment(expID1));
			//create second test Experiment
			ExperimentImpl exp2 = new ExperimentImpl();
			expID2 = dao_r.persistExperiment(exp2);
			ExperimentImpl find_exp2 = (ExperimentImpl)dao_r.findExperiment(expID2);
			System.out.println("ExpID2: "+expID2+ "find_exp2ID: "+find_exp2.getEntityID());
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
		ExperimentImpl exp_find1 = (ExperimentImpl)dao_r.findExperiment(expID1);
		//use the private helper method to setup the ExperimentSetup
		ExperimentSetupImpl expSetup = createEnvironmentExperimentSetup(1);
		//Test1: add ExperimentSetup
			exp_find1.setExperimentSetup(expSetup);
			dao_r.updateExperiment(exp_find1);

			exp_find1 = (ExperimentImpl)dao_r.findExperiment(expID1);
			System.out.println("State from ExperimentPhase persisted? "+exp_find1.getState());
			assertNotNull(exp_find1.getExperimentSetup());
			ExperimentSetupImpl expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			//must also have an ID assigned through @OneToOne(cascade={CascadeType.ALL})
			assertTrue(expSetup_find1.getEntityID()>0);
		
		//Test2: modify ExperimentSetup
			exp_find1 = (ExperimentImpl)dao_r.findExperiment(expID1);
			expSetup = createEnvironmentExperimentSetup(2);
			exp_find1.setExperimentSetup(expSetup);
			dao_r.updateExperiment(exp_find1);
			
			exp_find1 = (ExperimentImpl)dao_r.findExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			assertEquals("ExperimentName2", exp_find1.getExperimentSetup().getBasicProperties().getExperimentName());
			assertTrue(expSetup_find1.getEntityID()>0);
	
	}
	
	/**
	 * Note: The ExperimentResources Object contains an estimate on the required resources
	 */
	/*public void testExperimentResources(){
		
	}*/
	
	private ExperimentSetupImpl createEnvironmentExperimentSetup(int testnr){
		ExperimentSetupImpl expSetup = new ExperimentSetupImpl();
		expSetup.setState(ExperimentPhase.STATE_IN_PROGRESS);
		BasicPropertiesImpl props = new BasicPropertiesImpl();
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
	
	/*protected void tearDown(){
		try{
			dao_r.deleteExperiment(this.expID1);
			dao_r.deleteExperiment(this.expID2);
		}
		catch(Exception e){
			//TODO Integrate with Logging Framework
			System.out.println("TearDown: Exception while tearDown: "+e.toString());
			}
	}*/

}
