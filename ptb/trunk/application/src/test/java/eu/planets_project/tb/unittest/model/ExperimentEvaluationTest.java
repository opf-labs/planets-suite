/*package eu.planets_project.tb.unittest.model;

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
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentReportImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
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
	
	
	public void testEvaluateExperimentInputBenchmarkGoals(){
		
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
		try {
			//just evaluating source value - no output value
			expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), "20",null,null);
			//an exception should be thrown
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
	//Test3: Example how it should run
		expSetup.addBenchmarkGoal(goal1);
		manager.updateExperiment(expTest);

		try {
			//just evaluating source value - no output value
			expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), "20",null,null);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("20", goalFound.getSourceValue());
		
	//Test4: test evaluate source and target BMGoals

		try {
			//just evaluating source value - no output value
			expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), "30","25","very good");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		
		goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("30", goalFound.getSourceValue());
		assertEquals("25", goalFound.getTargetValue());
		assertEquals("very good", goalFound.getEvaluationValue());
	}
	
	public void testEvaluateExperimentOutputBenchmarkGoals(){
		
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
		try {
			//just evaluating source value - no output value
			expEval.evaluateExperimentBenchmarkGoal(goal1.getID(), null, "20",null);
			//an exception should be thrown
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
	//Test3: Example how it should run
		expSetup.addBenchmarkGoal(goal1);
		manager.updateExperiment(expTest);

		try {
			//just evaluating source value - no output value
			expEval.evaluateExperimentBenchmarkGoal(goal1.getID(),null, "20",null);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("20", goalFound.getTargetValue());
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
			try {
				expEval.evaluateFileBenchmarkGoal(testFile, goal1.getID(), "20",null,null);
				//an exception should be thrown
				assertEquals(true,false);
			} catch (InvalidInputException e) {
				assertEquals(true,true);
			}
			assertEquals(0,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertNull(expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID()));
			
		//Test3: Example how it should run
			expSetup.addBenchmarkGoal(goal1);
			manager.updateExperiment(expFound);
			
			try {
				expEval.evaluateFileBenchmarkGoal(testFile, goal1.getID(), "20",null,null);
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
			BenchmarkGoal goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("20", goalFound.getSourceValue());
			assertEquals("", goalFound.getTargetValue());

			
		//Test4: 
			try {
				expEval.evaluateFileBenchmarkGoal(testFile, goal1.getID(), null,"30",null);
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
			goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertNull(expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID()));
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("20", goalFound.getSourceValue());
			assertEquals("30", goalFound.getTargetValue());
			
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
		try {
			goal_test.setSourceValue("100");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		List<BenchmarkGoal> list_test = new Vector<BenchmarkGoal>();
		list_test.add(goal_test);

	//Test3: Example how it should run
		expSetup.addBenchmarkGoal(goal1);
		manager.updateExperiment(expTest);
		try {
			expEval.setEvaluatedExperimentBenchmarkGoals(list_test);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertEquals(true,false);
		}
			
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("100", goalFound.getSourceValue());
		
	//Test4: 
		goal_test = ((BenchmarkGoalImpl)goal1).clone();
		try {
			goal_test.setSourceValue("150");
			goal_test.setTargetValue("200");
			list_test = new Vector<BenchmarkGoal>();
			list_test.add(goal_test);
			expEval.setEvaluatedExperimentBenchmarkGoals(list_test);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		
		goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals(1,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		assertEquals("150", goalFound.getSourceValue());
		assertEquals("200", goalFound.getTargetValue());
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
			try {
				goal_test.setSourceValue("100");
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
			List<BenchmarkGoal> list_test = new Vector<BenchmarkGoal>();
			list_test.add(goal_test);
			HashMap<URI, List<BenchmarkGoal>> hmFileGoals = new HashMap<URI, List<BenchmarkGoal>>();
			hmFileGoals.put(testFile, list_test);
			
		//Test2: Example how it should run
			expSetup.addBenchmarkGoal(goal1);
			manager.updateExperiment(expFound);
			try {
				expEval.setEvaluatedFileBenchmarkGoals(hmFileGoals);
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
				
			BenchmarkGoal goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("100", goalFound.getSourceValue());
			assertEquals("",goalFound.getTargetValue());
			
		//Test3: 
			try {
				goal_test.setSourceValue("-1");
				goal_test.setTargetValue("20");
				list_test = new Vector<BenchmarkGoal>();
				list_test.add(goal_test);
				hmFileGoals = new HashMap<URI, List<BenchmarkGoal>>();
				hmFileGoals.put(testFile, list_test);
				
				expEval.setEvaluatedFileBenchmarkGoals(hmFileGoals);
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
			goalFound = expEval.getEvaluatedFileBenchmarkGoal(testFile, goal1.getID());
			//I'm working with the File's Benchmark Goals and not the Experiment's 
			assertEquals(1,expEval.getEvaluatedFileBenchmarkGoals(testFile).size());
			assertEquals("-1", goalFound.getSourceValue());
			assertEquals("20",goalFound.getTargetValue());
			
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
			ExperimentWorkflow workflow = new ExperimentExecutableImpl(wfhandler.getWorkflow(wfIds.firstElement()));
			//need to set a workflow, as no predefined wf can be set
			expFound.getExperimentSetup().setWorkflow(workflow);
			expFound.getExperimentSetup().getExperimentWorkflow().addInputData(testFile);
			manager.updateExperiment(expFound);
			
			try {
				expFound.getExperimentEvaluation().evaluateExperimentBenchmarkGoal(goal1.getID(), "20", "30", "bad");
			} catch (InvalidInputException e) {
				assertEquals(true,false);
			}
			
		//UnitTestSetup completed: now perform the actual Tests
			//Test1:
			assertEquals(1,expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoals().size());
			Collection<BenchmarkGoal> evalBMGoals = expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoals();
			if(evalBMGoals.size()>0){
				Iterator<BenchmarkGoal> itEvalBMGoals = evalBMGoals.iterator();
				while(itEvalBMGoals.hasNext()){
					BenchmarkGoal evalGoal = itEvalBMGoals.next();
					if(evalGoal.getID().equals(goal1.getID())){
						assertEquals("20", evalGoal.getSourceValue());
						assertEquals("30", evalGoal.getTargetValue());
						assertEquals("bad", evalGoal.getEvaluationValue());
					}
				}
			}
			
			assertEquals("20", expFound.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(goal1.getID()).getSourceValue());
			
			manager.updateExperiment(expFound);
			Experiment expFound2 = manager.getExperiment(this.expID1);

			//Test2:
			BenchmarkGoal goalTest = expFound2.getExperimentSetup().getBenchmarkGoal(goal1.getID());
			assertEquals("", goalTest.getSourceValue());
			
			BenchmarkGoal goalTest2 = expFound2.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(goal1.getID());
			assertEquals("20", goalTest2.getSourceValue());
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentEvaluationTest: "+e.toString());
			assertEquals(true,false);
		}
	}
	
	COMMENT IN AGAIN*/
	/**
	 * Helper method to build and add a workflow to an ExperimentSetup, as this object cannot
	 * be initialized by default
	 * @param expSetup
	 * @return
	 */
	/*START COMMENT IN AGAIN
	/*private ExperimentSetup setupTestWorkflow(ExperimentSetup expSetup, URI testFile){
			//create an ExperimentWorkflow
			WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
			Vector<Long> wfIds = (Vector<Long>)wfhandler.getAllWorkflowIDs();
			ExperimentWorkflow workflow = new ExperimentExecutableImpl(wfhandler.getWorkflow(wfIds.firstElement()));
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
COMMEND IN AGAIN*/