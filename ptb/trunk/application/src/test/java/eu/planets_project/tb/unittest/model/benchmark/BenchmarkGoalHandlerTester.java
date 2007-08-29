package eu.planets_project.tb.unittest.model.benchmark;

import java.util.List;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

public class BenchmarkGoalHandlerTester {
	
	public static void main (String[] args) {
		BenchmarkGoalsHandlerImpl handler = new BenchmarkGoalsHandlerImpl();
		List<BenchmarkGoal> textitems = handler.getAllBenchmarkGoals("Text");
		for(int i=0;i<textitems.size();i++){
			BenchmarkGoalImpl impl = (BenchmarkGoalImpl)textitems.get(i);
		}
		BenchmarkGoalImpl goal = (BenchmarkGoalImpl)handler.getBenchmarkGoal("nop2");
		
		
	}
	

}
