package eu.planets_project.tb.test;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.TestBean;

//import JUnit
import junit.framework.TestCase;

public class TestWizzardUnitTest extends TestCase{
	
	Context jndiContext;
	TesterRemote dao_r;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();

			dao_r = (TesterRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("ejb/TestWizzard/remote"), TesterRemote.class);

			TestBean test_1 = new TestBean();
			test_1.setId(1);
			test_1.setName("Andrew1");
			//System.out.println("Setup: Before storing ID1");
			dao_r.createTestEntry(test_1);
			//System.out.println("Setup: Successfully stored ID1");
		
			TestBean test_2 = new TestBean();
			test_2.setId(2);
			test_2.setName("Andrew2");
			//System.out.println("Setup: Before storing ID2");
			dao_r.createTestEntry(test_2);
			//System.out.println("Setup: Successfully stored ID2");
		} catch (NamingException e) {
			System.out.println("Setup: Exception while setUp: "+e.toString());
		}
		
	}
	
	protected void tearDown(){
		try{
			dao_r.deleteTestEntry(1);
			dao_r.deleteTestEntry(2);
		}
		catch(Exception e){
			System.out.println("TearDown: Exception while tearDown: "+e.toString());
			}
	}
	
	public void testEntitiesCreated(){
		
		TestBean test_find1 = dao_r.findTestEntry(1);
		//System.out.println("found ID "+test_find1.getId());
		//System.out.println("found Name "+test_find1.getName());
		
		TestBean test_find2 = dao_r.findTestEntry(2);
		//System.out.println("found ID "+test_find2.getId());
		//System.out.println("found Name "+test_find2.getName());
		
		assertEquals("Andrew1",test_find1.getName());
		assertEquals("Andrew2",test_find2.getName());
		
	}
	
	public void testEntitiesUpdated(){
		//NOTE: update ist noch nicht ganz schlüssig.!!!
		//how to update a managed object???
		
		//TestBean test_find1 = dao_r.findTestEntry(1);
		//test_find1.setName("NewName");
		
		dao_r.updateExistingTestEntry(1, "NewName",20, "20erWert");
		
		TestBean test_find2 = dao_r.findTestEntry(1);
		String htableValue = test_find2.getKeyValueOfPair(20);
		assertEquals("20erWert", htableValue);
		assertEquals("NewName",test_find2.getName());
	} 
	
	public void testEntitiesDeleted(){
		dao_r.deleteTestEntry(1);
		dao_r.deleteTestEntry(2);
		TestBean test_find1 = dao_r.findTestEntry(1);
		TestBean test_find2 = dao_r.findTestEntry(2);
		
		assertNull(test_find1);
		assertNull(test_find2);
	}
	
	public static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}

}
