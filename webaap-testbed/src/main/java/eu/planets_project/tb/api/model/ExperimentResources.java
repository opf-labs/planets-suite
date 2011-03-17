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
package eu.planets_project.tb.api.model;


public interface ExperimentResources {

	public final static int INTENSITY_LOW = 0;
	public final static int INTENSITY_MEDIUM = 1;
	public final static int INTENSITY_HIGH = 2;
	
	//List to be completed
	
	public void setNumberOfOutputFiles(int iNr);
	public int getNumberOfOutputFiles();

	public void setIntensity(int iIntensity);
	public int getIntensity();
	
}
