package eu.planets_project.tb.unittest.model.benchmark;

import java.util.Iterator;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

public class BenchmarkGoalHandlerTest extends TestCase{
	
	//to be configured manually as within XML file
	public final int iNumbOfCategories = 2;
	public final int iNumbOfGoals = 3;
	
	//sample goal that is contained in the XML document
	public final String sCathegory = "Text";
	public final String sGoalID = "nop1";
	public final String sGoalName = "number of pages";
	public final String sGoalType="java.lang.Integer";
	public final String sGoalScale="1..n";
	public final String sGoalVersion="1.0";
	public final String sGoalDefinition = "identifies the number of pages";
	public final String sGoalDescription = "Count the number of pages including the front-page";
	
	
	public void testBuildBenchmarkGoalsFromXML(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance(true);
		//handler.buildBenchmarkGoalsFromXML();
		Iterator<String> itCategory = handler.getCategoryNames().iterator();
		assertEquals(this.iNumbOfCategories,handler.getCategoryNames().size());
		assertEquals(this.iNumbOfGoals,handler.getAllBenchmarkGoals().size());
	}
	
	
	public void testSampleBenchmarkGoal(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance(true);
		
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		assertEquals(this.sCathegory,nop1.getCategory());
		assertEquals(this.sGoalID, nop1.getID());
		assertEquals(this.sGoalName, nop1.getName());
		assertEquals(this.sGoalType, nop1.getType());
		assertEquals(this.sGoalScale, nop1.getScale());
		assertEquals(this.sGoalVersion, nop1.getVersion());
		assertEquals(this.sGoalDefinition, nop1.getDefinition());
		assertEquals(this.sGoalDescription, nop1.getDescription());
	}
	
	
	public void testWeight(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance(true);
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals(-1,nop1.getWeight());
		
		nop1.setWeight(BenchmarkGoal.WEIGHT_MEDIUM);
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,nop1.getWeight());
		
		/*nop1.setWeight(BenchmarkGoal.WEIGHT_MINIMUM);
		assertEquals(BenchmarkGoal.WEIGHT_MINIMUM,nop1.getWeight());
		*/
		System.out.println("Weight nop1: "+nop1.getWeight());

		BenchmarkGoal nop2 = handler.getBenchmarkGoal(this.sGoalID);
		System.out.println("Weight nop2: "+nop2.getWeight());
		//assertEquals(-1,nop2.getWeight());
		
		BenchmarkGoal nop3 = handler.getBenchmarkGoal(this.sGoalID);
		System.out.println("Weight nop3: "+nop3.getWeight());
		assertEquals(-1,nop3.getWeight());
	}
	
	
	public void testValue(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance(true);
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals("",nop1.getValue());
		
		//Type is of java.lang.Integer
		nop1.setValue("25");
		assertEquals("25",nop1.getValue());
		
		nop1.setValue("true");
		assertEquals("25",nop1.getValue());
	}
	
	
	public void testWeightAndValueValid(){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance(true);
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		//Test1: Weight Valid
		nop1.setWeight(BenchmarkGoal.WEIGHT_MEDIUM);
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,nop1.getWeight());
		
		//ValueIsValid should be checked: must be in the range of Minimum to Maximum
		nop1.setWeight(20);
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,nop1.getWeight());
		
		//Test2: Value Valid
		assertTrue(nop1.checkValueValid("5"));
		assertFalse(nop1.checkValueValid("1.5"));
		assertFalse(nop1.checkValueValid("true"));
	}
		
}
