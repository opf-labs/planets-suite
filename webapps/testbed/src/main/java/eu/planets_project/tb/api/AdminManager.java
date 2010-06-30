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
package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.Map;

public interface AdminManager {
    
	public Collection<String> getExperimentTypesNames();

	public Collection<String> getExperimentTypeIDs();
	
	public String getExperimentTypeID(String sExpTypeName);

	public String getExperimentTypeName(String sTypeID);
	
	public Map<String,String> getExperimentTypeIDsandNames();

}
