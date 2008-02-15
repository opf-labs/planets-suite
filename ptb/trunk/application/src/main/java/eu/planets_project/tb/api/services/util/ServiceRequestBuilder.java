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
	 * Analyzes a given XMLRequestTemplate, replaces the TBTokens and builds a XMLServiceRequest
	 * String which can then be handed over and invoked.
	 * @return
	 */
	public String buildXMLServiceRequest();

}
