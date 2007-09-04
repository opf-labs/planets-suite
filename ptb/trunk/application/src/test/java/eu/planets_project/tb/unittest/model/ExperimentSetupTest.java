package eu.planets_project.tb.unittest.model;

import java.util.GregorianCalendar;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.*;
import eu.planets_project.tb.impl.model.mockup.ServiceImpl;
import eu.planets_project.tb.impl.services.mockups.WorkflowImpl;
import eu.planets_project.tb.api.model.finals.ExperimentTypes;
import eu.planets_project.tb.api.model.mockups.Workflow;

import junit.framework.TestCase;

public class ExperimentSetupTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManager manager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance();
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
	}
	
	
	public void testWorkflow(){
		Experiment exp_test = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = new ExperimentSetupImpl();
		Workflow workflow = new WorkflowImpl();
		Service service1 = new ServiceImpl();
		service1.setServiceName("TestService1");
		workflow.addService(0, service1);
		expSetup.setWorkflow(workflow);
		exp_test.setExperimentSetup(expSetup);
		
		manager.updateExperiment(exp_test);
		
		Experiment exp_find = manager.getExperiment(exp_test.getEntityID());
		assertEquals("TestService1",exp_find.getExperimentSetup().
				getExperimentWorkflow().getService(0).getServiceName());
		assertEquals(1,exp_find.getExperimentSetup().getExperimentWorkflow().
				getServices().size());
	}
	
	
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
	
	public ExperimentSetup getExperimentSetupSample(){
		//TODO: add a samle experimentSetup
		ExperimentSetup test_setup = new ExperimentSetupImpl();
		test_setup.setStartDate(new GregorianCalendar());
		test_setup.setExperimentType(ExperimentTypes.EXPERIMENT_TYPE_SIMPLEMIGRATION);
		return test_setup;
	}
	

	
	/*private ExperimentResources createEnvironmentExperimentResources(int testnr){
		return null;
	}*/
	
	
	protected void tearDown(){
		try{
			manager.removeExperiment(this.expID1);
			manager.removeExperiment(this.expID2);
		}
		catch(Exception e){
		}
	}

}
