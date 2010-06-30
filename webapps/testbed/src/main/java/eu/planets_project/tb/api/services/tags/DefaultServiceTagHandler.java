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
package eu.planets_project.tb.api.services.tags;

import java.util.Collection;
import java.util.Map;


/**
 * @author Andrew Lindley, ARC
 * A handler to load and retrieve all pre-defined service annotation tags which
 * are stored within a backend xml file (according to a given schema)
 * This does not take into account user defined free-text annotation tags
 *
 */
public interface DefaultServiceTagHandler {
	
	/**
	 * Returns a map of the default service tag's ID and the object
	 * @return
	 */
	public Map<String,ServiceTag> getAllIDsAndTags();
	public Collection<ServiceTag> getAllTags();
	public void buildTagsFromXML();
	
	public int getMaxPriority(int i);
	public int getMinPriority(int i);

}
