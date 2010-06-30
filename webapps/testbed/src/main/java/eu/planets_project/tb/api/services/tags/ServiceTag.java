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
package eu.planets_project.tb.api.services.tags;

/**
 * @author Andrew Lindley, ARC
 *
 */
public interface ServiceTag{
	/**
	 * Allows the user to use free search-tags and values for tagging service templates.
	 * These tags should be searchable via the web interface. 
	 * @param sTagName: null not allowed
	 * @param sTagValue: null not allowed
	 * @param sDescription: null allowed
	 */
	public void setTag(String sTagName,String sTagValue, String sDescription);
	public void setTag(String sTagName,String sTagValue);
	public String getValue();
	public String getName();
	public void setName(String name);
	public void setDescription(String sDescription);
	public String getDescription();
	public int getPriority();
	/**
	 * Sets the priority if it is within a given range
	 * @param i
	 */
	public void setPriority(int i);
}
