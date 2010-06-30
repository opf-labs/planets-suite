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

import java.util.List;

/**
 * @author Andrew Lindley, ARC
 * 
 */
public interface ServiceRequestBuilder {
	
	//The tokens that are supported
	public final String TAG_FILE = "@tbFile@";
	public final String TAG_FILEARRAYLINE_START = "@tbFileArrayLineStart@";
	public final String TAG_FILEARRAYLINE_END = "@tbFileArrayLineEnd@";
	public final String TAG_BASE64BYTEARRAY= "@Base64ByteArray@";
	
	
	/**
	 * Returns a list of all supported TagValues
	 * @return
	 */
	public List<String> getSupportedTagValues();
	
	/**
	 * Determines the type of the provided xmlRequestTemplate and returns true if its file
	 * File must contain the Tag: FILE
	 * @return
	 */
	public boolean isFileTemplate();
	/**
	 * Determines the type of the provided xmlRequestTemplate and returns true if its fileArray
	 * FileArray must contain the three Tags: FILE, FILEARRAYLINE_START, FILEARRAYLINE_END
	 * @return
	 */
	public boolean isFileArrayTemplate();
	
	/**
	 * Determines the type of the provided xmlRequestTemplate and returns true if its base64byteArray
	 * Base64ByteArray must contain the Tag: BASE64BYTEARRAY
	 * @return
	 */
	public boolean isBase64ByteArrayTemplate();
	
	/**
	 * Determines the type of the provided Input and Output of a call
	 * i.e. if the data is passed in terms of references 
	 * as (FILE and FILEARRAY) or by value (BASE64)
	 * @return
	 */
	public boolean isCallByValue();
	
	/**
	 * Analyzes a given XMLRequestTemplate, replaces the TBTokens and builds a XMLServiceRequest
	 * String which can then be handed over and invoked.
	 * @return
	 */
	public String buildXMLServiceRequest();

}
