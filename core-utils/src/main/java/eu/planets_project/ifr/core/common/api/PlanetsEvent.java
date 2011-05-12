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
 * PlanetsEvent interface
 *
 * @see eu.planets_project.ifr.core.common.impl.PlanetsEventExample
 * 
 * @author Klaus Rechert, ALUF
 */
public interface PlanetsEvent
{
	/**
         * @return The unique event id
         */
	int getId();	

	/** 
	 * @return The event's human readable message as String
	 */
	public String getMessage();

	
	/**
	 * @return A string representation of this event
	 */
	public String toString();	
}
