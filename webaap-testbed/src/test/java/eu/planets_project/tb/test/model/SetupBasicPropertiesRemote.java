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
package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.BasicPropertiesImpl;

@Remote
public interface SetupBasicPropertiesRemote {
	
	/**
	 * This method takes a given BasicProperties Entity Bean and persists it.
	 * The return value of this method is the auto-generated ID.
	 * @param props
	 * @return
	 */
	public long persistProperties(BasicPropertiesImpl props);
	public BasicPropertiesImpl findProperties(long id);
	
	/**
	 * Fetches the given and already persisted BasicProperties object and updates it with given values.
	 * @param props The BasicProperties which is look-uped and contains the values for the update	
	 */
	public void updateProperties(BasicPropertiesImpl props);
	public void deleteProperties(long id);
	public void deleteProperties(BasicPropertiesImpl props);

}
