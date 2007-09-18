package eu.planets_project.tb.unittest.model.benchmark;

import java.util.Iterator;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkHandlerImpl;

public class BenchmarkGoalHandlerTest extends TestCase{
	
	//to be configured manually as within XML file
	public final int iNumbOfCategories = 2;
	public final int iNumbOfGoals = 3;
	
	BenchmarkHandler handler;
	
	
	public void setUp(){
		handler = BenchmarkHandlerImpl.getInstance();
	}
	
	public void testBuildBenchmarkGoalsFromXML(){
		assertEquals(this.iNumbOfCategories,handler.getCategoryNames().size());
		assertEquals(this.iNumbOfGoals,handler.getAllBenchmarks().size());
	}
	
	public void testGetBenchmarks(){
		//Multiple tests
		assertTrue(0<handler.getAllBenchmarks().size());
		Iterator<String> itCategories = handler.getCategoryNames().iterator();
		int count = 0;
		int iSizeSum = 0;
		while(itCategories.hasNext()){
			count++;
			String category = itCategories.next();
			iSizeSum += handler.getAllBenchmarkIDs(category).size();
			assertEquals(handler.getAllBenchmarkIDs(category).size(),handler.getAllBenchmarks(category).size());
		}
		assertEquals(count,this.iNumbOfCategories);
		assertEquals(iSizeSum,handler.getAllBenchmarks().size());
		assertEquals(handler.getAllBenchmarkIDs().size(),handler.getAllBenchmarks().size());
		//getBenchmarkGoal
		Iterator<String> ids = handler.getAllBenchmarkIDs().iterator();
		while(ids.hasNext()){
			String id = ids.next();
			assertNotNull(handler.getBenchmark(id));
		}
		
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
