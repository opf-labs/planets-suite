package eu.planets_project.tb.unittest.wizzards;

import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyRemote;
import junit.framework.TestCase;

public class BrowseExperiments extends TestCase{
	
	Context jndiContext;
	ExperimentPersistencyRemote dao_r;
	TestbedManagerImpl manager;
	
	protected void setUp(){
		//System.out.println("Setup: Via Remote Interface");
		try {
			jndiContext = getInitialContext();
			dao_r = (ExperimentPersistencyRemote) PortableRemoteObject.narrow(
				jndiContext.lookup("ExperimentPersistency/remote"), ExperimentPersistencyRemote.class);
			//create two test Experiments, note their ID and persist them
			manager = TestbedManagerImpl.getInstance();
		} catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Setup: Exception in while setUp: "+e.toString());
		}
	}
	
	private void createExperiment(){
		
	}
	
	public void getAllExperiments(){
		manager = TestbedManagerImpl.getInstance();
		Set<Long> expIDs = manager.getAllExperimentIDs();
		
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
