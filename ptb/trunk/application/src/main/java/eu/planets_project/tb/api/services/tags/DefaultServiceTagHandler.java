package eu.planets_project.tb.api.services.tags;

import java.util.Collection;
import java.util.List;
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
