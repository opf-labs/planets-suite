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
package eu.planets_project.tb.api.model.benchmark;

import java.util.List;

public interface BenchmarkGoalsHandler {
	
	public void buildBenchmarkGoalsFromXML();
	public List<BenchmarkGoal> getAllBenchmarkGoals();
	/**
	 * This method is used to retrieve all available benchmark goals for a given category
	 * @see the BenchmarkGoals.xsd for additional information
	 * @param sCategoryName
	 * @return
	 */
	public List<BenchmarkGoal> getAllBenchmarkGoals(String sCategoryName);
	public List<String> getAllBenchmarkGoalIDs(String sCategoryName);
	public List<String> getAllBenchmarkGoalIDs();
	public BenchmarkGoal getBenchmarkGoal(String sID);
	public List<String> getCategoryNames();
	

}
