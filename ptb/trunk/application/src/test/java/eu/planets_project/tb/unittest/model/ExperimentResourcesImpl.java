package eu.planets_project.tb.unittest.model;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import junit.framework.TestCase;

public class ExperimentResourcesImpl extends TestCase{
	
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
	
	
	public void testExperimentResources(){
		//TODO extend
		@SuppressWarnings("unused")
		Experiment exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
		//use the private helper method to setup the ExperimentSetup
		@SuppressWarnings("unused")
		ExperimentSetupImpl expResources = createEnvironmentExperimentResources();
		
	}
	
	public ExperimentSetupImpl createEnvironmentExperimentResources(){
		//TODO create default resources
		return null;
	}
	
	protected void tearDown(){
		try{
			manager.removeExperiment(this.expID1);
			manager.removeExperiment(this.expID2);
		}
		catch(Exception e){
		}
	}

}
