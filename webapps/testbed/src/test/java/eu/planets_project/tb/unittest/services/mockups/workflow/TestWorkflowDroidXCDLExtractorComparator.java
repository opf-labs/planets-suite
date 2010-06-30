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
package eu.planets_project.tb.unittest.services.mockups.workflow;

import junit.framework.TestCase;
import java.io.File;
import java.util.Calendar;

import eu.planets_project.tb.api.model.eval.EvaluationExecutable;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowDroidXCDLExtractorComparator;

public class TestWorkflowDroidXCDLExtractorComparator extends TestCase{
	File f1,f2;
	
	public void setUp(){
		f1 = new File("C:/Data/T1europa.png");
		f2 = new File("C:/Data/T2europa.tif");
	}
	
	public void testRuns(){
		WorkflowDroidXCDLExtractorComparator wf = new WorkflowDroidXCDLExtractorComparator();
		EvaluationExecutable ex1 = wf.execute(f1, f2);
		assertTrue(ex1.isExecutableInvoked());
		assertTrue(ex1.isExecutionCompleted());
		assertTrue(ex1.isExecutionSuccess());
	}
	
	public void testCache(){
		WorkflowDroidXCDLExtractorComparator wf = new WorkflowDroidXCDLExtractorComparator();
		EvaluationExecutable ex1 = wf.execute(f1, f2);
		Calendar i1 = ex1.getExecutionStartDate();
		String s1 = ex1.getXCDLForSource();
		
		EvaluationExecutable ex2 = wf.execute(f1, f2);
		Calendar i2 = ex2.getExecutionStartDate();
		String s2 = ex2.getXCDLForSource();
		
		assertEquals(i1.getTimeInMillis()+"",i2.getTimeInMillis()+"");
		assertEquals(s1,s2);
		assertNotSame(ex1, ex2);
	}

}
