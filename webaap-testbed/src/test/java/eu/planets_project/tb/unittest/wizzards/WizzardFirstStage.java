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
package eu.planets_project.tb.unittest.wizzards;

import java.util.Iterator;
import junit.framework.TestCase;

import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;

public class WizzardFirstStage extends TestCase{
	
	/**
	 * This method sets up and fills in a sample BasicProperties Object
	 * @return
	 */
	private BasicProperties fillInBasicProperties(){
		
		BasicPropertiesImpl props = new BasicPropertiesImpl();
		
		//addExperimentedObjectType:
		try {
			props.addExperimentedObjectType("image/gif");
			props.addExperimentedObjectType("text/html");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		
			
		return props;
	}
	
	/**
	 * This method sets up and fills in a sample ExperimentSetup Object
	 * @return
	 */
	private ExperimentSetup createExperimentSetup(){
		
		BasicPropertiesImpl props = (BasicPropertiesImpl)fillInBasicProperties();
		ExperimentSetupImpl expSetup = new ExperimentSetupImpl();
		
		//setBasicProperties
		expSetup.setBasicProperties(props);
		
		//setExperimentSetup
		expSetup.setState(ExperimentSetup.STATE_NOT_STARTED);
		
		return expSetup;
		
	}
	
	/**
	 * Uses TestbedManager.registerNewUser()
	 * @return the experiment's entity Id.
	 */
	@SuppressWarnings("unused")
	private long createExperiment1(){
		
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl) createExperimentSetup();
		
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		ExperimentImpl exp1 = new ExperimentImpl();
		exp1.setState(ExperimentSetup.STATE_COMPLETED);
		exp1.setExperimentSetup(expSetup);
		
		//as the manager and the ExperimentImpl object are detached it is required to execute:
		/**
		 * this call persists the ExperimentImpl and registers it within the TestbedManager
		 * When the Experiment is persisted ExperimentSetupImpl-->BasicPropertiesImpl are persisted 
		 * as well through the @OneToOne(cascade={CascadeType.ALL})annotation
		**/
		long expID = manager.registerExperiment(exp1);
		System.out.println("Registered ExperimentID: "+expID);
		//As the ID is injected by the container it is important to query the Experiment Object again.
		exp1 = (ExperimentImpl)manager.getExperiment(expID);
		System.out.println("Got Registered ExperimentID: "+exp1.getEntityID());
		return exp1.getEntityID();

	}
	
	/**
	 * Uses TestbedManager.createNewUser()
	 * @return the experiment's entity Id.
	 */
	private long createExperiment2(){
		
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl) createExperimentSetup();
		
		TestbedManagerImpl tbmanager = TestbedManagerImpl.getInstance();
		/**
		 * this call persists the ExperimentImpl. When the Experiment is persisted 
		 * ExperimentSetupImpl-->BasicPropertiesImpl are persisted as well through the
		 * @OneToOne(cascade={CascadeType.ALL})annotation
		**/
		System.out.println("createExperiment2: hier1");
		ExperimentImpl exp = (ExperimentImpl)tbmanager.createNewExperiment();
		System.out.println("createExperiment2: hier2: received exp from createNewExp with ID: "+exp.getEntityID());
		exp.setState(ExperimentSetup.STATE_COMPLETED);
		System.out.println("createExperiment2: hier3");
		exp.setExperimentSetup(expSetup);
		
		//as the manager and the ExperimentImpl object are detached it is required to execute:
		System.out.println("createExperiment2: manager.updateExperiment with ID: "+exp.getEntityID());
		tbmanager.updateExperiment(exp);
		System.out.println("createExperiment2: hier4");
		
		return exp.getEntityID();
		
	}
	
	/**
	 * Unittest, checking if the Experiment as well as it's included Objects are created
	 * Therefore take the Experiment's ID provided through createExperiment() and request the
	 * persistently stored object from the TestbedManager.
	 */
	public void testisExperimentCreated(){
		//check is SetupExperiment created
		//check are BasicProperties created
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		long lExperimentID = createExperiment2();

		ExperimentImpl exp = (ExperimentImpl)manager.getExperiment(lExperimentID);
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl)exp.getExperimentSetup();
		BasicPropertiesImpl props = (BasicPropertiesImpl)expSetup.getBasicProperties();
		
		//now test if everything worked out
		assertTrue(exp!=null);
		assertTrue(expSetup!=null);
		assertTrue(props!=null);
		
		assertEquals(2,props.getExperimentedObjectTypes().size());
		assertTrue(props.getExperimentedObjectTypes().contains("image/gif"));
		System.out.println("ExpSetup State: "+expSetup.getState());
		assertEquals(ExperimentSetup.STATE_NOT_STARTED, expSetup.getState());
		assertEquals(ExperimentSetup.STATE_COMPLETED, exp.getState());
	}
	
	public void isExperimentPersisted(){
		
	}
	
	public void testQueryAllExperiments(){
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		Iterator<Long> itIDs = manager.getAllExperimentIDs().iterator();
		while(itIDs.hasNext()){
			System.out.println("I'm using ID: "+itIDs.next());
		}
		
		//manager.queryAllExperiments();
		assertTrue(true);
	}

}
