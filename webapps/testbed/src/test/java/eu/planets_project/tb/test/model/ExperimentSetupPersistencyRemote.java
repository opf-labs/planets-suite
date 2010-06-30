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

import eu.planets_project.tb.impl.model.ExperimentSetupImpl;

@Remote
public interface ExperimentSetupPersistencyRemote {
	
	public long persistExperimentSetup(ExperimentSetupImpl expSetup);
	public ExperimentSetupImpl findExperimentSetup(long id);
	
	public void updateExperimentSetup(ExperimentSetupImpl expSetup);
	public void deleteExperimentSetup(long id);
	public void deleteExperimentSetup(ExperimentSetupImpl expSetup);
}
