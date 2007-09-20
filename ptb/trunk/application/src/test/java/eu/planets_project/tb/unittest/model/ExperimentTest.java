package eu.planets_project.tb.unittest.model;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
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
		assertEquals(Experiment.PHASE_EXPERIMENTSETUP, iPhase);
		
		//Test2:
		test_exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
		test_exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
		iPhase = test_exp.getCurrentPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
		
		//Test3:
		//Note: ExperimentApproval is still IN_PROGRESS
		test_exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
		//It's possible to set the state in progress but the currentPhase should point
		//to the last one that's not completed:
		iPhase = test_exp.getCurrentPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
	}
	
	
	public void testGetCurrentPhase(){
		Experiment test_exp = manager.getExperiment(this.expID1);

		//Test1:
		ExperimentPhase phase = test_exp.getCurrentPhase();
		assertNotNull(phase);
		assertEquals(Experiment.PHASE_EXPERIMENTSETUP, phase.getPhasePointer());
		
		//Test2:
		test_exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
		test_exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);

		int iPhase = test_exp.getCurrentPhase().getPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
		
		//Test3:
		//Note: ExperimentApproval is still IN_PROGRESS
		test_exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
		//It's possible to set the state in progress but the currentPhase should point
		//to the last one that's not completed:
		iPhase = test_exp.getCurrentPhase().getPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
	}
	
	
	public void testGetCurrentPhaseName(){
		Experiment test_exp = manager.getExperiment(this.expID1);

		//Test1:
		ExperimentPhase phase = test_exp.getCurrentPhase();		
		assertNotNull(phase);
		assertEquals(Experiment.PHASE_EXPERIMENTSETUP, phase.getPhasePointer());
		
		//Test2:
		test_exp.getExperimentSetup().setState(Experiment.STATE_COMPLETED);
		test_exp.getExperimentApproval().setState(Experiment.STATE_IN_PROGRESS);
		int iPhase = test_exp.getCurrentPhase().getPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
		
		//Test3:
		//Note: ExperimentApproval is still IN_PROGRESS
		test_exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
		//It's possible to set the state in progress but the currentPhase should point
		//to the last one that's not completed:
		iPhase = test_exp.getCurrentPhase().getPhasePointer();
		assertEquals(Experiment.PHASE_EXPERIMENTAPPROVAL, iPhase);
	}
	
	public void testGetExperimentRefInPhase(){
		
		Experiment test_exp = manager.getExperiment(this.expID1);
		
		//Test1:
		assertEquals(test_exp.getEntityID(),((ExperimentSetupImpl)test_exp.getExperimentSetup()).getExperimentRefID());
		assertEquals(test_exp.getEntityID(),((ExperimentApprovalImpl)test_exp.getExperimentApproval()).getExperimentRefID());
		assertEquals(test_exp.getEntityID(),((ExperimentExecutionImpl)test_exp.getExperimentExecution()).getExperimentRefID());
		assertEquals(test_exp.getEntityID(),((ExperimentEvaluationImpl)test_exp.getExperimentEvaluation()).getExperimentRefID());
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
