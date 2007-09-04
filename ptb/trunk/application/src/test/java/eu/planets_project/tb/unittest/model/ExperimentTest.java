package eu.planets_project.tb.unittest.model;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import junit.framework.TestCase;

public class ExperimentTest extends TestCase{
	
	TestbedManager manager;
	private long expID1, expID2;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance();
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();		
	}
	
	
	public void testGetCurrentPhasePointer(){
		Experiment test_exp = manager.getExperiment(this.expID1);
		
		//Test1:
		int iPhase = test_exp.getCurrentPhasePointer();
		//all newly created Experiments are in the stage ExperimentSetup
		assertEquals(Experiment.STAGE_EXPERIMENTSETUP, iPhase);
		
		//Test2:
		test_exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
		test_exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
		iPhase = test_exp.getCurrentPhasePointer();
		assertEquals(Experiment.STAGE_EXPERIMENTAPPROVAL, iPhase);
		
		//Test3:
		//Note: ExperimentApproval is still IN_PROGRESS
		test_exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
		//It's possible to set the state in progress but the currentPhase should point
		//to the last one that's not completed:
		iPhase = test_exp.getCurrentPhasePointer();
		assertEquals(Experiment.STAGE_EXPERIMENTAPPROVAL, iPhase);
	}
	
	
	public void testGetCurrentPhase(){
		Experiment test_exp = manager.getExperiment(this.expID1);

		//Test1:
		ExperimentPhase phase = test_exp.getCurrentPhase();
		assertNotNull(phase);
		assertEquals(Experiment.STAGE_EXPERIMENTSETUP, phase.getStageMarker());
		
		//Test2:
		test_exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
		test_exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
		int iPhase = test_exp.getCurrentPhase().getStageMarker();
		assertEquals(Experiment.STAGE_EXPERIMENTAPPROVAL, iPhase);
		
		//Test3:
		//Note: ExperimentApproval is still IN_PROGRESS
		test_exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
		//It's possible to set the state in progress but the currentPhase should point
		//to the last one that's not completed:
		iPhase = test_exp.getCurrentPhase().getStageMarker();
		assertEquals(Experiment.STAGE_EXPERIMENTAPPROVAL, iPhase);
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
