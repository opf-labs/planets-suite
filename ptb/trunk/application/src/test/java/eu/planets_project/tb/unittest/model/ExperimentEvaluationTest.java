package eu.planets_project.tb.unittest.model;

import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;

public class ExperimentEvaluationTest extends TestCase{
	
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
	
	
	/*public void testEvaluateExperimentBenchmarkGoals(){
	
		ExperimentSetup expSetup = new ExperimentSetupImpl();
		ExperimentEvaluation expEval = new ExperimentEvaluationImpl(expSetup);
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		
		//Test1:
		assertEquals(0,expEval.getAddedExperimentBenchmarkGoals().size());
		
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
		expEval.evaluateExperimentBenchmarkGoal(goal1, "20");
		assertEquals(0,expEval.getEvaluatedExperimentBenchmarkGoals().size());
		
		//Test3:
		expSetup.addBenchmarkGoal(goal1);
		expEval.evaluateExperimentBenchmarkGoal(goal1, "20");
			
		BenchmarkGoal goalFound = expEval.getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		assertEquals("20", goalFound.getValue());

	}*/
	
	public void testEvaluateFileBenchmarkGoals(){
		
	}
	
	public void testRelatioshipSetupEvaluation(){
		Experiment expFound = manager.getExperiment(this.expID1);
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		System.out.println("ExperimentEvaluationTest1");
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
		System.out.println("ExperimentEvaluationTest2");

		expFound.getExperimentSetup().addBenchmarkGoal(goal1);
		System.out.println(expFound.getExperimentSetup().getAllAddedBenchmarkGoals().size());
		System.out.println("ExperimentEvaluationTest2.5");
		expFound.getExperimentEvaluation().evaluateExperimentBenchmarkGoal(goal1, "20");
		System.out.println("ExperimentEvaluationTest3 "+expFound.getExperimentEvaluation().getAddedExperimentBenchmarkGoals().size());

		manager.updateExperiment(expFound);
			
		Experiment expFound2 = manager.getExperiment(this.expID1);
		System.out.println("ExperimentEvaluationTest4"+expFound2.getExperimentSetup().getAllAddedBenchmarkGoals().size());

		BenchmarkGoal goalTest = expFound2.getExperimentSetup().getBenchmarkGoal(goal1.getID());
		System.out.println("ExperimentEvaluationTest5");
		System.out.println("FoundGoal: "+goalTest.getName());
		System.out.println("ExpSetup has Value?"+goalTest.getValue());
		BenchmarkGoal goalTest2 = expFound2.getExperimentEvaluation().getEvaluatedExperimentBenchmarkGoal(goal1.getID());
		System.out.println(goalTest2.getName());
		System.out.println("ExpEval has Value?"+goalTest2.getValue());
		assertEquals("20", goalTest2.getValue());
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
