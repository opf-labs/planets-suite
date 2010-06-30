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
package eu.planets_project.tb.api.services.util;

import java.util.Map;

/**
 * @author Andrew Lindley, ARC
 *
 */
public interface ServiceRespondsExtractor {

	
	/**
	 * Returns a Map with <OutputPosition,OutputValue>
	 * Map<Position,Value>
	 * Position: e.g. the third returned item. The position is important to 
	 * create an inputFile Output mapping
	 * @return
	 */
	public Map<String,String> getAllOutputs();
	
	/**
	 * Gets the output for a certain position.
	 * @param position
	 * @return String may be null
	 */
	public String getOutput(int position);
	

}
