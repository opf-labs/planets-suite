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


public interface ExperimentReport {
	
	/*public String getExecutionMetadata();*/

	public void setHeader(String text);
	public String getHeader();
	
	public void setBodyText(String text);
	public String getBodyText();
	
	/*public List<String> getGeneralBenchmarkGoals();
	public List<String> getBenchmarkGoalsForFiles();*/

}
