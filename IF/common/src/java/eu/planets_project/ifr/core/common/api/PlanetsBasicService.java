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
package eu.planets_project.ifr.core.common.api;

/**
 * Basic Planets Service Interface for level-1-services, 
 * only takes a byte[] and returns another one containing the migrated image.
 *
 *  @author : Peter Melms
 *  Email  : peter.melms@uni-koeln.de
 *  Created : 27.05.2008
 *
 */
public interface PlanetsBasicService {
	
	public byte[] basicMigrateBinary(byte[] imageData) throws PlanetsException;

}
