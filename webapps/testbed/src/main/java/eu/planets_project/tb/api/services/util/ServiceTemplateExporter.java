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

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * @author Andrew Lindley, ARC
 *
 */
public interface ServiceTemplateExporter {
	

	/**
	 * Exports a given service template as serialized xml-string
	 * @param template
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public String getExportAsString(TestbedServiceTemplate template) throws TransformerConfigurationException, ParserConfigurationException;
	
	/**
	 * Exports a given service template as serialized xml-string within an outputStream
	 * @param template
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public ByteArrayOutputStream getExportAsStream(TestbedServiceTemplate template) throws ParserConfigurationException, TransformerException;
	
}
