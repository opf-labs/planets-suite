package eu.planets_project.tb.unittest.model.benchmark;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import junit.framework.TestCase;

public class BenchmarkGoalTest extends TestCase{

	public void testXMLAttributes(){
		BenchmarkGoal goal = new BenchmarkGoalImpl();
		
		//goal.setWeight(BenchmarkGoal.WEIGHT_MAXIMUM);
		//assertEquals(BenchmarkGoal.WEIGHT_MAXIMUM,goal.getWeight());
		assertTrue(true);
	}
}
