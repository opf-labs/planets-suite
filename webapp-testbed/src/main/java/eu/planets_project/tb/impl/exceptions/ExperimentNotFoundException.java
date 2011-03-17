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
/**
 * 
 */
package eu.planets_project.tb.impl.exceptions;

/**
 * @author alindley
 *
 */
public class ExperimentNotFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1425887614667024503L;

	public ExperimentNotFoundException(){
		
	}
	
	public ExperimentNotFoundException(String msg){
		super(msg);
	}

}
