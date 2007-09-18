package eu.planets_project.tb.unittest.model.benchmark;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

public class BenchmarkGoalHandlerTest extends TestCase{
	
	//to be configured manually as within XML file
	public final int iNumbOfCategories = 2;
	public final int iNumbOfGoals = 3;
	
	BenchmarkGoalsHandler handler;
	
	
	public void setUp(){
		handler = BenchmarkGoalsHandlerImpl.getInstance();
	}
	
	public void testBuildBenchmarkGoalsFromXML(){
		assertEquals(this.iNumbOfCategories,handler.getCategoryNames().size());
		assertEquals(this.iNumbOfGoals,handler.getAllBenchmarkGoals().size());
	}
	
	public void testGetBenchmarkGoals(){
		//Multiple tests
		assertTrue(0<handler.getAllBenchmarkGoals().size());
		Iterator<String> itCategories = handler.getCategoryNames().iterator();
		int count = 0;
		int iSizeSum = 0;
		while(itCategories.hasNext()){
			count++;
			String category = itCategories.next();
			iSizeSum += handler.getAllBenchmarkGoalIDs(category).size();
			assertEquals(handler.getAllBenchmarkGoalIDs(category).size(),handler.getAllBenchmarkGoals(category).size());
		}
		assertEquals(count,this.iNumbOfCategories);
		assertEquals(iSizeSum,handler.getAllBenchmarkGoals().size());
		assertEquals(handler.getAllBenchmarkGoalIDs().size(),handler.getAllBenchmarkGoals().size());
		//getBenchmarkGoal
		Iterator<String> ids = handler.getAllBenchmarkGoalIDs().iterator();
		while(ids.hasNext()){
			String id = ids.next();
			assertNotNull(handler.getBenchmarkGoal(id));
		}
		
	}
	
	public void testGetBencharmarkGoals2(){
		Vector<String> IDs = (Vector<String>)handler.getAllBenchmarkGoalIDs();
		BenchmarkGoal goal1 = handler.getBenchmarkGoal(IDs.firstElement());
		
		//Test1:
		assertEquals(-1, goal1.getWeight());
		goal1.setWeight(BenchmarkGoal.WEIGHT_MEDIUM);
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,goal1.getWeight());
		//check if a new object was returned
		BenchmarkGoal goal2 = handler.getBenchmarkGoal(IDs.firstElement());
		assertEquals(-1,goal2.getWeight());
	}
	
	/*public void testPrintln(){
		Iterator<String> itCategories = handler.getCategoryNames().iterator();
		while(itCategories.hasNext()){
			System.out.println("Categories: "+itCategories.next());
		}
		Iterator<String> itText = handler.getAllBenchmarkGoalIDs("Text").iterator();
		while(itText.hasNext()){
			System.out.println("Text: "+itText.next());
		}
		Iterator<String> itImage = handler.getAllBenchmarkGoalIDs("Image").iterator();
		while(itImage.hasNext()){
			System.out.println("Image: "+itImage.next());
		}
		
		Iterator<BenchmarkGoal> itGoals = handler.getAllBenchmarkGoals().iterator();
		while(itGoals.hasNext()){
			System.out.println("Goal: "+itGoals.next().getName());
		}
		
		Iterator<BenchmarkGoal> itText2 = handler.getAllBenchmarkGoals("Text").iterator();
		while(itText2.hasNext()){
			System.out.println("TextNode: "+itText2.next().getName());
		}
		Iterator<BenchmarkGoal> itImage2 = handler.getAllBenchmarkGoals("Image").iterator();
		while(itImage2.hasNext()){
			System.out.println("TextNode: "+itImage2.next().getName());
		}
		
		Iterator<String> ids = handler.getAllBenchmarkGoalIDs().iterator();
		while(ids.hasNext()){
			String id = ids.next();
			System.out.println(handler.getBenchmarkGoal(id).getDescription());
		}
		assertEquals(true,true);
	}*/
	
		
}
