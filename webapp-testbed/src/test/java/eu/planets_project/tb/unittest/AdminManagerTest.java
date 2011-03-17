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
/**
 * 
 */
package eu.planets_project.tb.unittest;

import java.util.Collection;
import java.util.Map;

import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.impl.AdminManagerImpl;
import junit.framework.TestCase;

/**
 * @author alindley
 *
 */
public class AdminManagerTest extends TestCase{
	
	//information needs to be updated manually
	private final String sampleTypeName = "simple migration";
	private final String sampleTypeID = "experimentType.simpleMigration";
	private final int experimentTypes = 4;

	private AdminManager manager;
	
	public void setUp(){
		manager = AdminManagerImpl.getInstance();
	}
	
	public void testgetExperimentTypeIDs(){
		//Test1:
		Collection<String> expTypeIDs = manager.getExperimentTypeIDs();
		assertEquals(experimentTypes,expTypeIDs.size());
		assertTrue(expTypeIDs.contains(sampleTypeID));
		
		//Test2:
		assertEquals(this.sampleTypeID,manager.getExperimentTypeID(this.sampleTypeName));
	}
	
	public void testgetExperimentTypeNames(){
		//Test1:
		Collection<String> expTypeNames = manager.getExperimentTypesNames();
		assertEquals(experimentTypes, expTypeNames.size());
		assertTrue(expTypeNames.contains(sampleTypeName));
		
		//Test2:
		assertEquals(this.sampleTypeName,manager.getExperimentTypeName(this.sampleTypeID));
	}
	
	public void testExperimentTypeAndNames(){
		Map<String,String> idNames = manager.getExperimentTypeIDsandNames();
		assertEquals(experimentTypes,idNames.size());
		assertTrue(idNames.containsKey(this.sampleTypeID));
		assertTrue(idNames.containsValue(this.sampleTypeName));
	}
}
