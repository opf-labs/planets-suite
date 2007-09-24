package eu.planets_project.tb.unittest.system.mockup;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.system.mockup.WorkflowInvoker;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowImpl;
import eu.planets_project.tb.impl.system.mockup.WorkflowInvokerImpl;

public class WorkflowInvokerTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManager manager;
		
	public void setUp(){
		manager = TestbedManagerImpl.getInstance();
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
		
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
			
	}

	public void testExecuteExperimentWorkflow() {
	//setupTest:
		WorkflowInvoker wfinvoker = new WorkflowInvokerImpl();
		Experiment exp = manager.getExperiment(this.expID1);
		WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
		Workflow workflow = null;
		//now build a sample workflow
		Collection<Workflow> workflows = wfhandler.getAllWorkflows();
		if((workflows!=null)&&(workflows.size()>0)){
			Iterator<Workflow> itWFs = workflows.iterator();
			while(itWFs.hasNext()){
				workflow = itWFs.next();
			}
		}else{
			assertEquals(true,false);
		}
		ExperimentWorkflow wf = new ExperimentWorkflowImpl(workflow);
	//Not generic for Unittests must crate sample input
		try{
			URI input1 = new URI("http://localhost:8080/testbed/planets-testbed/inputdata/-1171883584.jpg");
			wf.addInputData(input1);
			exp.getExperimentSetup().setWorkflow(wf);
			exp.getExperimentSetup().setState(ExperimentSetup.STATE_COMPLETED);
			exp.getExperimentApproval().setState(ExperimentApproval.STATE_COMPLETED);
			manager.updateExperiment(exp);
			
			//now execute the experiment
			wfinvoker.executeExperimentWorkflow(exp.getEntityID());
			
			//now check if the migration succeeded 
			manager = TestbedManagerImpl.getInstance(true);
			Experiment expUpdated = manager.getExperiment(this.expID1);

			assertNotNull(expUpdated.getExperimentSetup().getExperimentWorkflow().getDataEntry(input1).getValue());
			assertEquals(Experiment.PHASENAME_EXPERIMENTEVALUATION,expUpdated.getCurrentPhase().getPhaseName());
			assertNotNull(expUpdated.getExperimentExecution().getExecutionDataEntry(input1).getValue());
			
		}catch(Exception e){
			System.out.println("Problem in running ExecuteExperimentWorkflowTest "+e.toString());
			assertEquals(true,false);
		}
		assertEquals(true,true);

	}
		
		
	public void tearDown(){
		try{
			manager.removeExperiment(this.expID1);
			manager.removeExperiment(this.expID2);
		}
		catch(Exception e){
		}
	}

}
