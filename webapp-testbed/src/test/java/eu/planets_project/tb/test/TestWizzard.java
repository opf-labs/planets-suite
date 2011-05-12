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
package eu.planets_project.tb.test;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eu.planets_project.tb.impl.TestBean;

@Stateless(name="ejb/TestWizzard")
public class TestWizzard implements TesterRemote, TesterLocal{

	@PersistenceContext(unitName="testbed")
	private EntityManager manager;

	public void createTestEntry(TestBean test) {
		manager.persist(test);
	}

	public TestBean findTestEntry(int pKey) {
		return manager.find(TestBean.class, pKey);
	}
	
	/* (non-Javadoc)
	 * Note: The TestBean test Object must must be the one that is already persisted
	 * @see test.eu.planets_project.tb.TesterRemote#updateExistingTestEntry(int, eu.planets_project.tb.impl.TestBean)
	 */
	public void updateExistingTestEntry(int pKey, String sName, int htableKey, String htableValue){
		TestBean t_helper = manager.find(TestBean.class, pKey);
		//System.out.println("updating: "+t_helper.getId()+" "+t_helper.getName());
		t_helper.setName(sName);
		t_helper.setKeyValuePairs(htableKey, htableValue);
		manager.persist(t_helper);
	}
	
	public void deleteTestEntry(int pKey){
		TestBean t_helper = manager.find(TestBean.class, pKey);
		manager.remove(t_helper);
	}
	
	public void deleteTestEntry(TestBean test){
		manager.remove(test);
	}

}
