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
package eu.planets_project.services;

/**
 * Planets specific exception
 */
@SuppressWarnings("serial")
public class PlanetsException extends Exception
{
	/**
	 * Construct from passed message
	 * @param msg
	 */
	public PlanetsException(String msg)
	{
		super(msg);
	}

	/**
	 * Construct from passed throwable
	 * @param t
	 */
	public PlanetsException(Throwable t)
	{
		super(t);
	}
}
