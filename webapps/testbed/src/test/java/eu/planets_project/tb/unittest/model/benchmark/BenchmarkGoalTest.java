/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.unittest.model.benchmark;

import java.util.List;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;

@SuppressWarnings("deprecation")
public class BenchmarkGoalTest extends TestCase{
	
	//sample goal that is contained in the XML document
	public final String sCathegory = "Text";
	public final String sGoalID = "text-gen1";
	public final String sGoalName = "Number of levels";
	public final String sGoalType="java.lang.Integer";
	public final String sGoalScale="1..n";
	public final String sGoalVersion="1.0";
	public final String sGoalDefinition = "Defines the number of subdivision levels beneath the highest level in a text.";
	public final String sGoalDescription = "If the text is e.g. divided in parts, which each have chapters, which are divided in paragraphs, the number of levels would be 3. For most emails, the number of levels will be 0 if they are not subdivided. Makes it possible to decide whether the structure has been preserved / characterised. \"structure\"";
	
	BenchmarkGoalsHandler handler;
	
	public void setUp(){
		handler = BenchmarkGoalsHandlerImpl.getInstance();
	}
	
	
	public void testAttributes(){
		
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		assertEquals(this.sCathegory,nop1.getCategory());
		assertEquals(this.sGoalID, nop1.getID());
		assertEquals(this.sGoalName, nop1.getName());
		assertEquals(this.sGoalType, nop1.getType());
		assertEquals(this.sGoalScale, nop1.getScale());
		assertEquals(this.sGoalVersion, nop1.getVersion());
		assertEquals(this.sGoalDefinition, nop1.getDefinition());
		assertEquals(this.sGoalDescription, nop1.getDescription());
		assertEquals(-1,nop1.getWeight());
		assertEquals("",nop1.getSourceValue());
		assertEquals("",nop1.getTargetValue());
		assertEquals("",nop1.getEvaluationValue());
	}
	
	
	public void testWeight(){
		//Test1:
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		assertEquals(-1,nop1.getWeight());
		
		//Test2:
		try {
			nop1.setWeight(BenchmarkGoal.WEIGHT_MEDIUM);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(BenchmarkGoal.WEIGHT_MEDIUM,nop1.getWeight());
		
		try {
			nop1.setWeight(BenchmarkGoal.WEIGHT_MINIMUM);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(BenchmarkGoal.WEIGHT_MINIMUM,nop1.getWeight());
		
		//Test3: got a new instance?
		BenchmarkGoal nop2 = handler.getBenchmarkGoal(this.sGoalID);
		assertEquals(-1,nop2.getWeight());
		
		//Test4: invalid value
		BenchmarkGoal nop3 = handler.getBenchmarkGoal(this.sGoalID);
		try {
			nop3.setWeight(15);
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
		assertEquals(-1,nop3.getWeight());
		
	}
	
	
	public void testSourceValue(){
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals("",nop1.getSourceValue());
		
		//Type is of java.lang.Integer
		try {
			nop1.setSourceValue("25");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals("25",nop1.getSourceValue());
		
		//Test: value was not valid and therefore did not change
		try {
			nop1.setSourceValue("true");
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
		assertEquals("25",nop1.getSourceValue());
	}
	
	public void testTargetValue(){
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals("",nop1.getTargetValue());
		
		//Type is of java.lang.Integer
		try {
			nop1.setTargetValue("25");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals("25",nop1.getTargetValue());
		
		//Test: value was not valid and therefore did not change
		try {
			nop1.setTargetValue("true");
			assertEquals(true,false);
		} catch (InvalidInputException e) {
			assertEquals(true,true);
		}
		assertEquals("25",nop1.getTargetValue());
	}
	
	
	public void testEvaluationValue(){
		BenchmarkGoal nop1 = handler.getBenchmarkGoal(this.sGoalID);
		
		assertEquals("",nop1.getEvaluationValue());
		ExperimentEvaluation eval = new ExperimentEvaluationImpl();
		List<String> list = eval.getAllAcceptedEvaluationValues();
		
		assertEquals(4,list.size());
		
	//Test1:

		try {
			nop1.setEvaluationValue(list.get(0));
			assertEquals(list.get(0),nop1.getEvaluationValue());
			nop1.setEvaluationValue(list.get(1));
			assertEquals(list.get(1),nop1.getEvaluationValue());
		} catch (InvalidInputException e1) {
			assertEquals(true,false);
		}
	//Test2:
		try {
			nop1.setEvaluationValue("TestString");
			assertEquals(true,false);
		} catch (InvalidInputException e1) {
			assertEquals(true,true);
		}
	}
}
