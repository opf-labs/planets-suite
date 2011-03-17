/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.util;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;

/**
 * @author CFWilson
 *
 */
public class TBContentResolver {
	/**
	 * 
	 */
	public static final String CONTENT_RESOLVER_URI = "/testbed/external/digitalObjectContentResolver?id=";

	/**
     * This method returns HTTP content resolver path for permanent URI
     * @return path
     */
	public static String getResolverPath(){
		return "http://" + PlanetsServerConfig.getHostname() + ":"
		+ PlanetsServerConfig.getPort() + CONTENT_RESOLVER_URI;
	}

}
