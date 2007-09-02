package eu.planets_project.tb.unittest.model.benchmark;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

public class BenchmarkGoalHandlerTest extends TestCase{
	
	/*public static void main (String[] args) {
		BenchmarkGoalsHandlerImpl handler = new BenchmarkGoalsHandlerImpl();
		List<BenchmarkGoal> textitems = handler.getAllBenchmarkGoals("Text");
		for(int i=0;i<textitems.size();i++){
			BenchmarkGoalImpl impl = (BenchmarkGoalImpl)textitems.get(i);
		}
		BenchmarkGoalImpl goal = (BenchmarkGoalImpl)handler.getBenchmarkGoal("nop2");
		
		
	}*/
	
	public void testBuildBenchmarkGoalsFromXML(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		handler.buildBenchmarkGoalsFromXML();
		assertEquals(true,true);
	}
	

}
