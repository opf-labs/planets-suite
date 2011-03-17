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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.ExperimentSetupImpl;

@Stateless
public class ExperimentSetupPersistency implements ExperimentSetupPersistencyRemote{
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;

	public void deleteExperimentSetup(long id) {
		ExperimentSetupImpl t_helper = manager.find(ExperimentSetupImpl.class, id);
		manager.remove(t_helper);
		
	}

	public void deleteExperimentSetup(ExperimentSetupImpl expSetup) {
		ExperimentSetupImpl t_helper = manager.find(ExperimentSetupImpl.class, expSetup.getEntityID());
		manager.remove(t_helper);		
	}

	public ExperimentSetupImpl findExperimentSetup(long id) {
		return manager.find(ExperimentSetupImpl.class, id);
	}

	public long persistExperimentSetup(ExperimentSetupImpl expSetup) {
		manager.persist(expSetup);
		return expSetup.getEntityID();
	}

	public void updateExperimentSetup(ExperimentSetupImpl expSetup) {
		manager.merge(expSetup);
	}

}
