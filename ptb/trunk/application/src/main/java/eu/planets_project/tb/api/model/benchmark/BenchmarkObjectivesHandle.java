package eu.planets_project.tb.api.model.benchmark;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Benchmark objectives should be extendable and are therefore not directly coded into the API.
 * A sample objective could be:
 * -ID: quota1
 * -path: root/performance/errquota123
 * -name: error quota 
 * -description: measures the relationship 
 * This interface is responsible of getting a handle on the XML Parser for retrieving objectives
 * 
 * @author alindley
 *
 */

public interface BenchmarkObjectivesHandle {
	
	/**
	 * @return All available objectives as Objective objects
	 */
	public Iterator<Objective> getAllAvailableObjectivesIterative();
	
	/**
	 * Returns a Hashtable of all available <ObjectiveIDs, Objectives> objectives
	 * e.g. root/performance/
	 * @param sCategoryPath an absolute path on an objective node the XML tree
	 * @return
	 */
	public Hashtable<String,Objective> getAllAvailableObjectives();
	
	/**
	 * Returns a Hashtable of all available <ObjectiveIDs, Objectives> objectives for a given absolut path
	 * e.g. root/performance/
	 * @param sCategoryPath an absolute path on aa branch the XML tree
	 * @return
	 */
	public Hashtable<String,Objective> getAllAvailableObjectives(String sCategoryPath);
	
	
	/**
	 * @param sFilelocation
	 * @return
	 */
	//private void setDocumentRoot(String sFilelocation);
	
	/**
	 * @return File object of the XML benchmark objectives file.
	 */
	//private File getDocumentRoot();
}
