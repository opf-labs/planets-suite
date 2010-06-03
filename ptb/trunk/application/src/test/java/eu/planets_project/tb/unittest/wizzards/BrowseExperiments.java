package eu.planets_project.tb.unittest.wizzards;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import junit.framework.TestCase;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.TestbedManagerImpl;

public class BrowseExperiments extends TestCase{
	
	Context jndiContext;
	ExperimentPersistencyRemote dao_r;
	TestbedManagerImpl manager;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();
			dao_r = (ExperimentPersistencyRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("testbed/ExperimentPersistency/remote"), ExperimentPersistencyRemote.class);
			//create two test Experiments, note their ID and persist them
			manager = TestbedManagerImpl.getInstance();
		} catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Setup: Exception in while setUp: "+e.toString());
		}
	}
	
	public void getAllExperiments(){
		manager = TestbedManagerImpl.getInstance();
		manager.getAllExperimentIDs();
		
		//Get 
	}
	
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
