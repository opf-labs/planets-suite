package eu.planets_project.tb.unittest.model;

import java.util.Iterator;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.model.finals.TestbedRoles;
import eu.planets_project.tb.impl.model.BasicProperties;
import eu.planets_project.tb.impl.model.User;
import eu.planets_project.tb.test.model.SetupBasicPropertiesRemote;

import junit.framework.TestCase;

public class SetupBasicPropertiesTest extends TestCase{
	
	Context jndiContext;
	SetupBasicPropertiesRemote dao_r;
	
	private long propID1, propID2;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();

			dao_r = (SetupBasicPropertiesRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("SetupBasicPropertiesBean/remote"), SetupBasicPropertiesRemote.class);

			//create two test Properties, note their ID and persist them
			BasicProperties prop1 = new BasicProperties();
			propID1 = dao_r.persistProperties(prop1);
			
			BasicProperties prop2 = new BasicProperties();
			propID2 = dao_r.persistProperties(prop2);
			
		} catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Setup: Exception in while setUp: "+e.toString());
		}
	}
	
	
	// Tests all EJB persistency related issues:
	
	public void testEJBEntityCreated(){
		assertNotNull(dao_r.findProperties(propID1));
	}
	
	public void testEJBEntityDeleted(){
		dao_r.deleteProperties(this.propID1);
		dao_r.deleteProperties(dao_r.findProperties(propID2));
		BasicProperties props1 = new BasicProperties();
		BasicProperties props2 = new BasicProperties();
		try{
			props1 = dao_r.findProperties(propID1);
			props2 = dao_r.findProperties(propID2);
			
		}catch(Exception e){
			
		}
		assertNull(props1);
		assertNull(props2);	
	}
	
	public void testEJBEntityUpdated(){
		BasicProperties test_find1 =  dao_r.findProperties(propID1);
		//test set/getConsiderations
		test_find1.setConsiderations("consideration1");
		test_find1.setExperimentName("TestName");
		dao_r.updateProperties(test_find1);
		//Test1: updating existing entity
		test_find1 =  dao_r.findProperties(propID1);
		assertEquals("consideration1",test_find1.getConsiderations());	
	}
	
	public void testEJBEntityMerged(){
		testEJBEntityUpdated();
		BasicProperties test_find1 =  dao_r.findProperties(propID1);
		//test set/getConsiderations
		test_find1.setConsiderations("consideration1");
		test_find1.setExperimentName("TestName");
		dao_r.updateProperties(test_find1);
		
		//Test1: updating existing entity
		test_find1 =  dao_r.findProperties(propID1);
		assertEquals("consideration1",test_find1.getConsiderations());	
		
		//Test2: checking if merging entity works
		test_find1 =  dao_r.findProperties(propID1);
		test_find1.setExperimentName("TestUpdated");
		dao_r.updateProperties(test_find1);
		
		test_find1 =  dao_r.findProperties(propID1);
		assertEquals("consideration1",test_find1.getConsiderations());	
		assertEquals("TestUpdated",test_find1.getExperimentName());	
	}

	
	//Tests for the underlying Entity Bean's methods setter and getter's without any EJB issues

	public void testExperimentReferences(){
		BasicProperties props = new BasicProperties();
		long l1 = 1;
		long l2 = 2;
		long l3 = 3;
		//Test1: add references
		props.addExperimentReference(l1);
		props.addExperimentReference(l2);
		
		Vector<Long> v = props.getExperimentReferences();
		assertEquals(true,v.contains(l1));
		assertEquals(true,v.contains(l2));
		
		//Test2: remove references
		props.removeExperimentReference(l1);
		Vector<Long> v2 = props.getExperimentReferences();
		assertEquals(false,v2.contains(l1));
		assertEquals(true,v2.contains(l2));
		
		//Test3: set references
		props.setExperimentReference(3);
		Vector<Long> v3 = props.getExperimentReferences();
		assertEquals(true,v3.contains(l3));
		assertEquals(1,v3.size());
		
		//Test4: set Experiment[]
		//Not possible without EJB
		/*Experiment[] test = new Experiment[2];
		Experiment exp1 = new Experiment();
		exp1.setProgress(0);
		Experiment exp2 = new Experiment();
		exp2.setProgress(1);
		test[0]=exp1;
		test[1]=exp2;
		props.setExperimentReferences(test);
		
		Vector<Long> v4 = props.getExperimentReferences();
		System.out.println("size "+v4.size());
		assertEquals(2,v3.size());	*/
	}
	
	
	public void testInvolvedUsers(){
		//1.test addInvolvedUsers->getInvolvedUserIds
		BasicProperties props = new BasicProperties();
		long l1 = 1;
		long l2 = 2;
		Vector<Long> lUserIDs = new Vector<Long>();
		lUserIDs.addElement(l1);
		lUserIDs.addElement(l2);
		props.addInvolvedUsers(lUserIDs);
		
		Vector<Long> vInvolvedUsers = props.getInvolvedUserIds();
	
		assertEquals(true,vInvolvedUsers.contains(l1));
		assertEquals(true,vInvolvedUsers.contains(l2));
		
		//2.test: setInvolvedUsers->getInvolvedUserIds
		
			Vector<Integer> userRoles = new Vector<Integer>();
			userRoles.addElement(TestbedRoles.TESTBED_ROLE_PLANETS_USER);
			userRoles.addElement(TestbedRoles.TESTBED_ROLE_EXPERIMENTER);
			User testUser = new User(userRoles);
			props.setInvolvedUsers(testUser);
		
			Vector<Long> vInvolvedUsers2 = props.getInvolvedUserIds();
			assertNotNull(vInvolvedUsers2);
				
			//as set does override the existing settings it must not contain l1,l2
			assertEquals(false,vInvolvedUsers2.contains(l1));
			assertEquals(false,vInvolvedUsers2.contains(l2));
			assertEquals(1,vInvolvedUsers2.size());
		
		//3. test: setInvolvedUsers(Users)-->getInvolvedUsers(User)
			Vector<Integer> userRoles2 = new Vector<Integer>();
			userRoles2.addElement(TestbedRoles.TESTBED_ROLE_PLANETS_USER);
			userRoles2.addElement(TestbedRoles.TESTBED_ROLE_EXPERIMENTER);
			User user1 = new User(userRoles2);
			user1.setUserDetails("Forename1", "Surname1");
			User user2 = new User(userRoles2);
			user1.setUserDetails("Forename2", "Surname2");
			
			Vector<eu.planets_project.tb.api.model.User> vUsers = new Vector<eu.planets_project.tb.api.model.User>();
			vUsers.addElement(user1);
			vUsers.addElement(user2);
			props.setInvolvedUsers(vUsers);
			vUsers = props.getInvolvedUsers();
			
			assertEquals(2,vUsers.size());
			//TODO: At the moment UserManager is not testable - include later
			//assertTrue(vUsers.contains(user1));
			//assertTrue(vUsers.contains(user2));
		
		//4. test: removeUser->getInvolvedUsers
		
	}
	
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}
	
	protected void tearDown(){
		try{
			dao_r.deleteProperties(this.propID1);
			dao_r.deleteProperties(this.propID2);
		}
		catch(Exception e){
			//TODO Integrate with Logging Framework
			System.out.println("TearDown: Exception while tearDown: "+e.toString());
			}
	}

}
