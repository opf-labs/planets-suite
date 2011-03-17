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
/*package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.BasicPropertiesImpl;

@Stateless
public class SetupBasicPropertiesBean implements SetupBasicPropertiesRemote{

	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	
	public long persistProperties(BasicPropertiesImpl props) {
		manager.persist(props);
		return props.getId();
	}

	public BasicPropertiesImpl findProperties(long pKey) {
		return manager.find(BasicPropertiesImpl.class, pKey);
	}
	
	public void deleteProperties(long pKey){
		BasicPropertiesImpl t_helper = manager.find(BasicPropertiesImpl.class, pKey);
		manager.remove(t_helper);
	}

	public void deleteProperties(BasicPropertiesImpl props) {
		BasicPropertiesImpl t_helper = manager.find(BasicPropertiesImpl.class, props.getId());
		manager.remove(t_helper);
	}*/

	/* (non-Javadoc)
	 * Note: The Object must be the one that is already persisted
	 * @see eu.planets_project.tb.test.model.SetupBasicPropertiesRemote#updateProperties(eu.planets_project.tb.impl.model.BasicProperties)
	 */
	/*public void updateProperties(BasicPropertiesImpl props) {
		manager.merge(props);
	}
	
}*/
