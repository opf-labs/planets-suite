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
package eu.planets_project.tb.api.model.eval;

import java.util.List;

/**
 * @author lindleyA
 * The Testbed's representation of an evaluation metric (as imported through a 
 * evaluation service template) and used for being mapped to the Testbed's evaluation
 * criteria.
 */
public interface Metric{
	
	public void setName(String sName);		
	public String getName();

	public void setType(String sType);
	public String getType();
	
	public List<String> getNumericTypes();
	
	public void setDescription(String sDescr);
	public String getDescription();
}