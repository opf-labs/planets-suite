/*COMMENT IN AGAIN
package eu.planets_project.tb.unittest;

import java.util.GregorianCalendar;

import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.unittest.model.ExperimentSetupTest;
import junit.framework.TestCase;

public class TestbedManagerTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManager manager;
	private AdminManager adminManager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance();
		adminManager = AdminManagerImpl.getInstance();
		//create two new test Experiments
		Experiment exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
		Experiment exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
	}
	
	//There are two ways of getting a persistently managed Experiment
	//a) registerExperiment
	public void testRegisterExperiment(){
		Experiment exp_test = new ExperimentImpl();
		
		//Test1: entity does not have an ID; it is injected by the container.
		assertEquals(-1,exp_test.getEntityID());
		
		//Test2:
		long exp_testID = manager.registerExperiment(exp_test);
		assertNotNull(exp_testID);
		//But Note: exp_test does is decoupled and therefore still has no ID injected
		assertTrue(manager.containsExperiment(exp_testID));
		assertEquals(-1,exp_test.getEntityID());
		
		//now retrieve the container managed Experiment
		exp_test = manager.getExperiment(exp_testID);
		assertNotNull(exp_test.getEntityID());
		
		//Test3:
		//it's not possible to register a previously or still registered experiment
		//as the container cannot inject a valid ID
		long lIDNew = manager.registerExperiment(exp_test);
		assertEquals(-1,lIDNew);
		//But experiment is still registered with the previously created and valid ID:
		assertNotNull(manager.getExperiment(exp_test.getEntityID()));
		
		
		//Teardown: now clean up the mess:
		manager.removeExperiment(exp_testID);

	}
	
	//Second possibility (preferable) is to let the manager create an Experiment instance
	//b) createExperiment
	public void testCreateExperiment(){
		
		Experiment exp_test = manager.createNewExperiment();
		
		//Test1: entity does already have an ID injected by the container
		assertNotNull(exp_test.getEntityID());
		assertTrue(manager.containsExperiment(exp_test.getEntityID()));
		
		//Tear down: now clean up the mess:
		manager.removeExperiment(exp_test.getEntityID());
	}
	
	
	public void testUpdateExperiment(){
		Experiment exp_test = manager.getExperiment(expID1);
		long exp_testID = exp_test.getEntityID();
		
		//now update some properties
		exp_test.setState(Experiment.STATE_NOT_STARTED);
		//now modify some properties - note: experiment is decoupled from manager
			//use ExperimentSetup Test Object
		ExperimentSetup expSetup = new ExperimentSetupTest().getExperimentSetupSample();
		exp_test.setExperimentSetup(expSetup);
		
		//now call manager.update to persist the experiment as well as all it's properties
		manager.updateExperiment(exp_test);

		//Test1: 
		//check if update was successfully
		Experiment exp_find = manager.getExperiment(exp_testID);
			//test the properties
		assertEquals(exp_test.getState(),exp_find.getState());
			//find persisted objects properties
		assertEquals(expSetup.getExperimentTypeID(),exp_find.getExperimentSetup().getExperimentTypeID());
		assertEquals(expSetup.getStartDateInMillis(),exp_find.getExperimentSetup().getStartDateInMillis());
	
		//Test2:
		//2a) check if I cannot update a not-registered Experiment
		Experiment exp_test2 = new ExperimentImpl();
		exp_test2.setEndDate(new GregorianCalendar());
		manager.updateExperiment(exp_test2);
		//not possible to check this state
		
		//2b)
		Experiment exp_test3 = manager.createNewExperiment();
		manager.removeExperiment(exp_test3.getEntityID());
		exp_test3.setEndDate(new GregorianCalendar());
		manager.updateExperiment(exp_test3);
		//Manager does not contain the experiment
		assertTrue(!manager.containsExperiment(exp_test3.getEntityID()));
	}
	
	
	public void testRemoveExperiment(){
		//TODO continue implementing
		
		//Test1: 
		Experiment exp_test = manager.getExperiment(expID1);
		assertTrue(manager.containsExperiment(exp_test.getEntityID()));
		
		manager.removeExperiment(exp_test.getEntityID());
		assertTrue(!manager.containsExperiment(exp_test.getEntityID()));
		
		//Test2: 
		//it's not possible to register a previously registered experiment
		//as the container cannot inject a valid ID
		long lIDNew = manager.registerExperiment(exp_test);
		assertEquals(-1,lIDNew);
	}
	
	
	public void testIsRegistered(){
		Experiment exp_test = manager.getExperiment(expID1);
		assertTrue(manager.isRegistered(exp_test));
		assertTrue(manager.isRegistered(exp_test.getEntityID()));
		
		Experiment exp_test2 = new ExperimentImpl();
		assertTrue(!manager.isRegistered(exp_test2));
		assertTrue(!manager.isRegistered(exp_test2.getEntityID()));
	}
	
	
	public void testContainsExperiment(){
		assertTrue(manager.containsExperiment(expID1));
		Experiment exp_test2 = new ExperimentImpl();
		assertTrue(!manager.containsExperiment(exp_test2.getEntityID()));
	}
	
	
	public void testGetAllExperiments(){
		//Test1:
		int size1 = manager.getAllExperimentIDs().size();
		Experiment exp_test = manager.createNewExperiment();
		assertEquals(size1+1,manager.getAllExperimentIDs().size());
		assertTrue(manager.getAllExperimentIDs().contains(exp_test.getEntityID()));
		
		//Test2:
		int size2 = manager.getAllExperiments().size();
		Experiment exp_test2 = manager.createNewExperiment();
		System.out.println("exp_test2ID: "+exp_test2.getEntityID());
		assertEquals(size2+1,manager.getAllExperiments().size());
		assertTrue(manager.getAllExperiments().contains(exp_test2));
		
		//noew clean up the mess
		manager.removeExperiment(exp_test.getEntityID());
		manager.removeExperiment(exp_test2.getEntityID());
	}
	
	
	public void testGetAllExperimentsOfType(){
		
		//Test3:
		int size3 = manager.getAllExperimentsOfType(
				adminManager.getExperimentTypeID("simple migration")).size();
		Experiment exp_test3 = manager.createNewExperiment();
		ExperimentSetup expSetup = new ExperimentSetupImpl();
		try{
		expSetup.setExperimentType(adminManager.getExperimentTypeID("simple migration"));
		}catch(InvalidInputException e){
			assertEquals(true,false);
		}
		exp_test3.setExperimentSetup(expSetup);
		manager.updateExperiment(exp_test3);
		
		assertEquals(size3+1,manager.getAllExperimentsOfType(
				adminManager.getExperimentTypeID("simple migration")).size());
		
		//now clean up the mess
		manager.removeExperiment(exp_test3.getEntityID());
	}
	
	
	public void testGetAllExperimentsOfUser(){
		//Test4:
		//Test where user is Experimenter
		String sUserID = "Karin";
		int size4 = manager.getAllExperimentsOfUsers(sUserID,true).size();
		Experiment exp_test4 = manager.createNewExperiment();
		ExperimentSetup expSetup4 = exp_test4.getExperimentSetup();
		BasicProperties props4 = expSetup4.getBasicProperties();
		props4.setExperimenter(sUserID);
		expSetup4.setBasicProperties(props4);
		exp_test4.setExperimentSetup(expSetup4);
		manager.updateExperiment(exp_test4);
		
		assertEquals(size4+1,manager.getNumberOfExperiments(sUserID, true));
		//Test where user is InvolvedUser
		sUserID = "Brian";
		int size5 = manager.getAllExperimentsOfUsers(sUserID,false).size();
		Experiment exp_test5 = manager.createNewExperiment();
		ExperimentSetup expSetup5 = new ExperimentSetupImpl();
		BasicProperties props5 = new BasicPropertiesImpl();
		props5.addInvolvedUser("Brian");
		expSetup5.setBasicProperties(props5);
		exp_test5.setExperimentSetup(expSetup5);
		manager.updateExperiment(exp_test5);
		assertEquals(size5+1,manager.getNumberOfExperiments(sUserID, false));
		manager.removeExperiment(exp_test4.getEntityID());
		manager.removeExperiment(exp_test5.getEntityID());
	}
	
	
	public void testIsExperimentNameUnique(){
		//setup
		Experiment exp_test = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = exp_test.getExperimentSetup();
		BasicProperties props = expSetup.getBasicProperties();
		try {
			props.setExperimentName("TestName1");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		expSetup.setBasicProperties(props);
		exp_test.setExperimentSetup(expSetup);
		manager.updateExperiment(exp_test);
		
		Experiment exp_find = manager.getExperiment(exp_test.getEntityID());
		assertEquals("TestName1",exp_find.getExperimentSetup().getBasicProperties().getExperimentName());
		
		//Test1:
		assertTrue(!manager.isExperimentNameUnique("TestName1"));
		assertTrue(manager.isExperimentNameUnique("FDSKLÖEWRJKDHSFIUWEHK"));
	
		//Test2:
		Experiment exp2 = manager.getExperiment(this.expID2);
		try {
			exp2.getExperimentSetup().getBasicProperties().setExperimentName("TestName1");
			//Exception should be thrown
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
	}
	
	
	public void testGetNumberOfExperiments(){
		
		//Test1:
		//getNumberOfExperiments	
		int iNumb = manager.getNumberOfExperiments();
		//now register a new Experiment
		Experiment exp_test = manager.createNewExperiment();
		assertEquals(iNumb+1,manager.getNumberOfExperiments());

		//Test2:
		//getNumberOfExperiments(String userID)
		String sUserID = "Andrew";
		int iNumbExperimenter1 = manager.getNumberOfExperiments(sUserID, true);
		int iNumbInvolved1 = manager.getNumberOfExperiments(sUserID, false);
		//now register a new Experiment
		Experiment exp_test2 = manager.createNewExperiment();
		ExperimentSetup expSetup = new ExperimentSetupImpl();
		BasicProperties props = new BasicPropertiesImpl();
		props.setExperimenter(sUserID);
		expSetup.setBasicProperties(props);
		exp_test2.setExperimentSetup(expSetup);
		manager.updateExperiment(exp_test2);
		
		//Number of Experiments for Experimenter should be +1
		assertEquals(iNumbExperimenter1+1,manager.getNumberOfExperiments(sUserID, true));
		//Number of Experiments for InvolvedUser should be +1 as Experimenter is also set as involved user
		assertEquals(iNumbInvolved1+1,manager.getNumberOfExperiments(sUserID, false));
		
		//Test2b:
		int iNumbExperimenter2 = manager.getNumberOfExperiments(sUserID, true);
		int iNumbInvolved2 = manager.getNumberOfExperiments(sUserID, false);
		Experiment exp_test3 = manager.createNewExperiment();
		expSetup = exp_test3.getExperimentSetup();
		props = expSetup.getBasicProperties();
		props.addInvolvedUser(sUserID);
		expSetup.setBasicProperties(props);
		exp_test3.setExperimentSetup(expSetup);
		manager.updateExperiment(exp_test3);
		
		//Number of Experiments for Experimenter should be +0
		assertEquals(iNumbExperimenter2,manager.getNumberOfExperiments(sUserID, true));
		//Number of Experiments for InvolvedUser should be +0
		assertEquals(iNumbInvolved2+1,manager.getNumberOfExperiments(sUserID, false));
		
		//noew clean up the mess
		manager.removeExperiment(exp_test.getEntityID());
		manager.removeExperiment(exp_test2.getEntityID());
		manager.removeExperiment(exp_test3.getEntityID());
	}
	
	
	protected void tearDown(){
		try{
			manager.removeExperiment(expID1);
			manager.removeExperiment(expID2);
		}
		catch(Exception e){
		}
	}

}

	END OF COMMENT IN AGAIN*/