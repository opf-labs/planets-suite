package eu.planets_project.tb.unittest.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentReportImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;

public class ExperimentEvaluationTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManager manager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance(true);
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
	}
	
	
	public void testEvaluateExperimentBenchmarkGoals(){
		
		Experiment expTest = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = expTest.getExperimentSetup();
		ExperimentEvaluation expEval = expTest.getExperimentEvaluation();
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		
	//Test1:
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		
	//Test2: exp must not be evaluated as the benchmark is not contained in experimentSetup
		Vector<String> sGoalIDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		//now find a benchmark that accepts input value Integer
		Iterator<String> itGoalIDs = sGoalIDs.iterator();
		BenchmarkGoal goal1 = null;
		while(itGoalIDs.hasNext()){
			BenchmarkGoal goalTest = handler.getBenchmarkGoal(itGoalIDs.next());
			if(goalTest.getType().equals("java.lang.Integer")){
				goal1 = goalTest;
			}
		}
		//check if there is a benchmarkGoal with type Integer else cannot perform checks
		assertNotNull(goal1);
		//now check the actual unittests
		expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), "20");
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
	//Test3: Example how it should run
		expSetup.addBenchmarkGoal(goal1);
		manager.updateExperiment(expTest);

		expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), "20");
		
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("20", goalFound.getValue());
	}
	
	
	public void testEvaluateFileBenchmarkGoals(){
		Experiment expFound = manager.getExperiment(this.expID1);
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		Vector<String> sGoalIDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		
		try{
			ExperimentSetup expSetup = expFound.getExperimentSetup();
			URI testFile = new URI("file:http://planets-project.eu/testbed/files/1");
			expSetup = this.setupTestWorkflow(expSetup, testFile);
			
		//Test1:
			ExperimentEvaluation expEval = expFound.getExperimentEvaluation();
			assertEquals(0,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			
		//Test2: exp must not be evaluated as the benchmark is not contained in experimentSetup
			//now find a benchmark that accepts input value Integer
			Iterator<String> itGoalIDs = sGoalIDs.iterator();
			BenchmarkGoal goal1 = null;
			while(itGoalIDs.hasNext()){
				BenchmarkGoal goalTest = handler.getBenchmarkGoal(itGoalIDs.next());
				if(goalTest.getType().equals("java.lang.Integer")){
					goal1 = goalTest;
				}
			}
			//check if there is a benchmarkGoal with type Integer else cannot perform checks
			assertNotNull(goal1);
			//now check the actual unittests
			expEval.evaluateFileBenchmarkGoal(testFile, goal1.getID(), "20");
			assertEquals(0,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertNull(expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID()));
			
		//Test3: Example how it should run
			expSetup.addBenchmarkGoal(goal1);
			manager.updateExperiment(expFound);
			
			expEval.evaluateFileBenchmarkGoal(testFile, goal1.getID(), "20");
				
			BenchmarkGoal goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("20", goalFound.getValue());
			
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentEvaluationTest: "+e.toString());
			assertEquals(true,false);
		}
		
	}
	
	
	public void testSetEvaluatedExperimentBenchmarkGoals(){
		Experiment expTest = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = expTest.getExperimentSetup();
		ExperimentEvaluation expEval = expTest.getExperimentEvaluation();
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		
	//Test1:
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		
	//Helper: setup actual test
		Vector<String> sGoalIDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		//now find a benchmark that accepts input value Integer
		Iterator<String> itGoalIDs = sGoalIDs.iterator();
		BenchmarkGoal goal1 = null;
		while(itGoalIDs.hasNext()){
			BenchmarkGoal goalTest = handler.getBenchmarkGoal(itGoalIDs.next());
			if(goalTest.getType().equals("java.lang.Integer")){
				goal1 = goalTest;
			}
		}
		//check if there is a benchmarkGoal with type Integer else cannot perform checks
		assertNotNull(goal1);
		//now check the actual unittests
		BenchmarkGoal goal_test = ((BenchmarkGoalImpl)goal1).clone();
		goal_test.setValue("100");
		List<BenchmarkGoal> list_test = new Vector<BenchmarkGoal>();
		list_test.add(goal_test);

		
	//Test3: Example how it should run
		expSetup.addBenchmarkGoal(goal1);
		manager.updateExperiment(expTest);
		expEval.setEvaluatedExperimentBenchmarkGoals(list_test);
			
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("100", goalFound.getValue());
	}
	
	
	
	public void testSetEvaluateFileBenchmarkGoals(){
		Experiment expFound = manager.getExperiment(this.expID1);
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		Vector<String> sGoalIDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		
		try{
			ExperimentSetup expSetup = expFound.getExperimentSetup();
			URI testFile = new URI("file:http://planets-project.eu/testbed/files/1");
			expSetup = this.setupTestWorkflow(expSetup, testFile);
			
		//Test1:
			ExperimentEvaluation expEval = expFound.getExperimentEvaluation();
			assertEquals(0,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			
		//SetupHelper:
			//now find a benchmark that accepts input value Integer
			Iterator<String> itGoalIDs = sGoalIDs.iterator();
			BenchmarkGoal goal1 = null;
			while(itGoalIDs.hasNext()){
				BenchmarkGoal goalTest = handler.getBenchmarkGoal(itGoalIDs.next());
				if(goalTest.getType().equals("java.lang.Integer")){
					goal1 = goalTest;
				}
			}
			//check if there is a benchmarkGoal with type Integer else cannot perform checks
			assertNotNull(goal1);
			//now check the actual unittests
			BenchmarkGoal goal_test = ((BenchmarkGoalImpl)goal1).clone();
			goal_test.setValue("100");
			List<BenchmarkGoal> list_test = new Vector<BenchmarkGoal>();
			list_test.add(goal_test);
			HashMap<URI, List<BenchmarkGoal>> hmFileGoals = new HashMap<URI, List<BenchmarkGoal>>();
			hmFileGoals.put(testFile, list_test);
			
		//Test2: Example how it should run
			expSetup.addBenchmarkGoal(goal1);
			manager.updateExperiment(expFound);
			expEval.setEvaluatedFileBenchmarkGoals(hmFileGoals);
				
			BenchmarkGoal goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("100", goalFound.getValue());
			
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentEvaluationTest: "+e.toString());
			assertEquals(true,false);
		}
		
	}
	
	
	
	public void testRelatioshipSetupEvaluation(){
		Experiment expFound = manager.getExperiment(this.expID1);
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		Vector<String> sGoalIDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		
		//now find a benchmark that accepts input value Integer
		Iterator<String> itGoalIDs = sGoalIDs.iterator();
		BenchmarkGoal goal1 = null;
		while(itGoalIDs.hasNext()){
			BenchmarkGoal goalTest = handler.getBenchmarkGoal(itGoalIDs.next());
			if(goalTest.getType().equals("java.lang.Integer")){
				goal1 = goalTest;
			}
		}
		//check if there is a benchmarkGoal with type Integer else cannot perform checks
		assertNotNull(goal1);
		
		//Testsetup part two
		expFound.getExperimentSetup().addBenchmarkGoal(goal1);
		
		try{
			URI testFile = new URI("file:http://planets-project.eu/testbed/files/1");
			//create an ExperimentWorkflow
			WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
			Vector<Long> wfIds = (Vector<Long>)wfhandler.getAllWorkflowIDs();
			ExperimentWorkflow workflow = new ExperimentWorkflowImpl(wfhandler.getWorkflow(wfIds.firstElement()));
			//need to set a workflow, as no predefined wf can be set
			expFound.getExperimentSetup().setWorkflow(workflow);
			expFound.getExperimentSetup().getExperimentWorkflow().addInputData(testFile);
			manager.updateExperiment(expFound);
			
			expFound.getExperimentEvaluation().evaluateExperimentBenchmarkGoal(goal1.getID(), "20");
			
		//UnitTestSetup completed: now perform the actual Tests
			//Test1:
			assertEquals(1,expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoals().size());
			Collection<BenchmarkGoal> evalBMGoals = expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoals();
			if(evalBMGoals.size()>0){
				Iterator<BenchmarkGoal> itEvalBMGoals = evalBMGoals.iterator();
				while(itEvalBMGoals.hasNext()){
					BenchmarkGoal evalGoal = itEvalBMGoals.next();
					if(evalGoal.getID().equals(goal1.getID())){
						assertEquals("20", evalGoal.getValue());
					}
				}
			}
			
			assertEquals("20", expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(goal1.getID()).getValue());
			
			manager.updateExperiment(expFound);
			Experiment expFound2 = manager.getExperiment(this.expID1);

			//Test2:
			BenchmarkGoal goalTest = expFound2.getExperimentSetup().getBenchmarkGoal(goal1.getID());
			assertEquals("", goalTest.getValue());
			
			BenchmarkGoal goalTest2 = expFound2.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(goal1.getID());
			assertEquals("20", goalTest2.getValue());
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentEvaluationTest: "+e.toString());
			assertEquals(true,false);
		}
	}
	
	
	/**
	 * Helper method to build and add a workflow to an ExperimentSetup, as this object cannot
	 * be initialized by default
	 * @param expSetup
	 * @return
	 */
	private ExperimentSetup setupTestWorkflow(ExperimentSetup expSetup, URI testFile){
			//create an ExperimentWorkflow
			WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
			Vector<Long> wfIds = (Vector<Long>)wfhandler.getAllWorkflowIDs();
			ExperimentWorkflow workflow = new ExperimentWorkflowImpl(wfhandler.getWorkflow(wfIds.firstElement()));
			//need to set a workflow, as no predefined wf can be set
			expSetup.setWorkflow(workflow);
			expSetup.getExperimentWorkflow().addInputData(testFile);
			
			return expSetup;
	}
	
	
	public void testExperimentReport(){
		ExperimentEvaluation eval = new ExperimentEvaluationImpl();
		ExperimentReport report = new ExperimentReportImpl();
		
		//Test1: properly initialized
		assertNotNull(eval.getExperimentReport());
		
		//Test2: 
		report.setHeader("Header1");
		report.setBodyText("Body");
		eval.setExperimentReport(report);
		
		assertEquals("Header1",eval.getExperimentReport().getHeader());
		assertEquals("Body",eval.getExperimentReport().getBodyText());
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
