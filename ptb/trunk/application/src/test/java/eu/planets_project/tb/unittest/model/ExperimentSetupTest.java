package eu.planets_project.tb.unittest.model;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.*;

import junit.framework.TestCase;

public class ExperimentSetupTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManagerImpl manager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance();
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
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
	
	
	
	
	/**
	 * Note: The ExperimentResources Object contains an estimate on the required resources
	 */
	/*public void testExperimentResources(){
		
	}*/
	
	private ExperimentSetupImpl createEnvironmentExperimentSetup(int testnr){
		ExperimentSetupImpl expSetup = new ExperimentSetupImpl();
		expSetup.setState(ExperimentPhase.STATE_IN_PROGRESS);
		//BasicProperties
		BasicPropertiesImpl props = new BasicPropertiesImpl();
		props.setConsiderations("considerations"+testnr);
		props.setExperimentName("ExperimentName"+testnr);
		expSetup.setBasicProperties(props);
		
		//BenchmarkObjectives
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		BenchmarkGoalImpl goal = (BenchmarkGoalImpl)handler.getBenchmarkGoal("nop1");
		expSetup.addBenchmarkGoal(goal);
		
		return expSetup;
	}
	
	//Tests for the underlying Entity Bean's methods setter and getters
	public void testSetExperimentSetup(){

		ExperimentImpl exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
		//use the private helper method to setup the ExperimentSetup
		ExperimentSetupImpl expSetup = createEnvironmentExperimentSetup(1);
		//Test1: add ExperimentSetup
			exp_find1.setExperimentSetup(expSetup);
			manager.updateExperiment(exp_find1);

			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			ExperimentSetupImpl expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			//must also have an ID assigned through @OneToOne(cascade={CascadeType.ALL})
			assertTrue(expSetup_find1.getEntityID()>0);
		
		//Test2: modify ExperimentSetup
			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			expSetup = createEnvironmentExperimentSetup(2);
			exp_find1.setExperimentSetup(expSetup);
			manager.updateExperiment(exp_find1);
			
			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			assertEquals("ExperimentName2", exp_find1.getExperimentSetup().getBasicProperties().getExperimentName());
			assertTrue(expSetup_find1.getEntityID()>0);
			
			assertEquals(1,expSetup_find1.getAllAddedBenchmarkGoals().size());
			System.out.println("Definition :"+expSetup_find1.getBenchmarkGoal("nop1").getDefinition());
			assertEquals("nop1",expSetup_find1.getBenchmarkGoal("nop1").getID());
			
	
	}
	
	public void testBasicProperties(){
		
	}
	

	
	/*private ExperimentResources createEnvironmentExperimentResources(int testnr){
		return null;
	}*/
	
	
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
