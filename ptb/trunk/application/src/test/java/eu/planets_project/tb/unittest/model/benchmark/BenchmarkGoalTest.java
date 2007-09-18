package eu.planets_project.tb.unittest.model.benchmark;

import java.util.Iterator;

import eu.planets_project.tb.api.model.benchmark.Benchmark;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkHandlerImpl;
import junit.framework.TestCase;

public class BenchmarkGoalTest extends TestCase{
	
	//sample goal that is contained in the XML document
	public final String sCathegory = "Text";
	public final String sGoalID = "nop1";
	public final String sGoalName = "number of pages";
	public final String sGoalType="java.lang.Integer";
	public final String sGoalScale="1..n";
	public final String sGoalVersion="1.0";
	public final String sGoalDefinition = "identifies the number of pages";
	public final String sGoalDescription = "Count the number of pages including the front-page";
	
	BenchmarkHandler handler;
	
	public void setUp(){
		handler = BenchmarkHandlerImpl.getInstance();
	}
	
	
	public void testAttributes(){
		
		Benchmark nop1 = handler.getBenchmark(this.sGoalID);
		assertEquals(this.sCathegory,nop1.getCategory());
		assertEquals(this.sGoalID, nop1.getID());
		assertEquals(this.sGoalName, nop1.getName());
		assertEquals(this.sGoalType, nop1.getType());
		assertEquals(this.sGoalScale, nop1.getScale());
		assertEquals(this.sGoalVersion, nop1.getVersion());
		assertEquals(this.sGoalDefinition, nop1.getDefinition());
		assertEquals(this.sGoalDescription, nop1.getDescription());
// has to be tested with class BenchmarkGoal
		/*assertEquals(-1,nop1.getWeight());
		assertEquals("",nop1.getValue());*/
	}
	
	
/*	public void testWeight(){
		//Test1:
		BenchmarkGoal nop1 = handler.getBenchmark(this.sGoalID);
		assertEquals(-1,nop1.getWeight());
		
		//Test2:
		nop1.setWeight(BenchmarkGoal.WEIGHT_MEDIUM);
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,nop1.getWeight());
		
		nop1.setWeight(BenchmarkGoal.WEIGHT_MINIMUM);
		assertEquals(BenchmarkGoal.WEIGHT_MINIMUM,nop1.getWeight());
		
		//Test3: got a new instance?
		Benchmark nop2 = handler.getBenchmark(this.sGoalID);
		assertEquals(-1,nop2.getWeight());
		
		//Test4: invalid value
		Benchmark nop3 = handler.getBenchmark(this.sGoalID);
		nop3.setWeight(15);
		assertEquals(-1,nop3.getWeight());
		
	}
	
	
	public void testValue(){
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals("",nop1.getValue());
		
		//Type is of java.lang.Integer
		nop1.setValue("25");
		assertEquals("25",nop1.getValue());
		
		//Test: value was not valid and therefore did not change
		nop1.setValue("true");
		assertEquals("25",nop1.getValue());
	} */
}
