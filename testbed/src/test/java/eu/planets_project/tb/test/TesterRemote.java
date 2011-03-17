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

import javax.ejb.Remote;

import eu.planets_project.tb.impl.TestBean;

@Remote
public interface TesterRemote {

	public void createTestEntry(TestBean test);

	public TestBean findTestEntry(int pKey);
	
	public void updateExistingTestEntry(int pKey, String sName, int htableKey, String htableValue);
	
	public void deleteTestEntry(int pKey);
	
	public void deleteTestEntry(TestBean test);
}
