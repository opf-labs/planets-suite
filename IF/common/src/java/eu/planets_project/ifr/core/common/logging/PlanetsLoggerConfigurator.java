package eu.planets_project.ifr.core.common.logging;

import org.apache.log4j.*;
import org.apache.log4j.xml.*;
import java.net.*;

/**
 * Utility class to configure PlanetsLogger behavior.
 *
 *  @author Markus Reis, AR
 *  @author Klaus Rechert, ALUF
 */
public class PlanetsLoggerConfigurator
{
	private static Logger log = Logger.getLogger(PlanetsLoggerConfigurator.class);
	/**
 	* Loads a URL resource from a given filename. 
 	*
 	* @param filename The filename to load.
 	*/
	private static URL asUrl(String filename)
	{
		ClassLoader classLoader;

		classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResource(filename);
	}

	/**
 	* Configures PlanetsLogger using the specified Log4J XML configuration file.
 	* All configuration directive are added to the current configuration. Call reset()
 	* 	
 	* @param log4jxml The specified Log4J XML configuration file.
 	* @see #reset()
	*/
	public static void configure(String log4jxml)
	{
		log.debug("Loading configure file: " + log4jxml);
		URL url = asUrl(log4jxml);
		if(url != null)
			DOMConfigurator.configure(url);
		else
			log.warn("Loading configure file failed (" + log4jxml + ")");
	}

	/**
 	* Resets current PlanetLogger configuration.
 	*
 	* @see #configure(String)
 	*/	
	public static void reset()
	{
		BasicConfigurator.resetConfiguration();
	}
}
