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
