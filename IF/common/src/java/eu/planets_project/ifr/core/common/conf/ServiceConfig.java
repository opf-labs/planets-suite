package eu.planets_project.ifr.core.common.conf;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Factory for obtaining configuration parameters. Configuration files
 * are "properties" files, stored in a common directory which is defined
 * by the system property "config.dir"
 * 
 * Generally, configuration is expected to be performed in a manner analogous
 * to log4j. For example:
 * <pre>
 * public class MyClass {
 *     private static Configuration config = ServiceConfig.getConfiguration(MyClass.class);
 *     
 *     public MyClass() {
 *         String value1 = config.getString("test.name");
 *         int    value2 = config.getInt("test.version");
 *     }
 * }
 * </pre>
 * 
 * The configuration will then be loaded from a file in the directory specified (above).
 * The name of the configuration file will be the fully qualified class name suffixed
 * with ".properties"
 * 
 * The method <code>getConfiguration()</code> can alternatively accept a String type
 * parameter which allows the name of the configuration file to be more explicitly
 * specified. The name of the configuration file will be the value of the parameter
 * suffixed with ".properties" and will still be located in the directory specified
 * above.
 * 
 * If the property file does not exist, or cannot be parsed a 
 * {@link ConfigurationException}
 * will be thrown. If a property is requested that cannot be found, a 
 * {@link NoSuchElementException}
 * will be thrown.
 * 
 * The underlying implementation is provided by Jakarta's commons-configuration. For
 * more details of the Configuration returned see:
 * {@link Configuration}
 * 
 * @author Ian Radford
 */
public final class ServiceConfig {
	static final String BASE_DIR_PROPERTY = "config.dir";
	private ServiceConfig() {
		// This should never be called - we're just a factory with static methods
	}
	
	/**
	 * Find the configuration file for a given class and return an object representing it.
	 * @param clazz Specifies the configuration file in question. The filename will
	 *                 be the fully qualified class name suffixed with ".properties"
	 * @return Representation of the configuration file
	 * @throws ConfigurationException If the appropriate file cannot be found or, if present,
	 *                                parsed correctly.
	 */
	public static Configuration getConfiguration(Class<?> clazz) throws ConfigurationException {
		String className = clazz.getName();
		return getConfiguration(className);
	}
	
	/**
	 * Find a configuration file and return an object representing it.
	 * @param basename Specifies the configuration file in question. The filename will
	 *                 be suffixed with ".properties"
	 * @return Representation of the configuration file
	 * @throws ConfigurationException If the appropriate file cannot be found or, if present,
	 *                                parsed correctly.
	 */
	public static Configuration getConfiguration(String basename) throws ConfigurationException {
		/*
		 * Determine the absolute filename of the properties file.
		 */
		String baseDirectory = System.getProperty(BASE_DIR_PROPERTY);
		String filename = baseDirectory + File.separatorChar + basename + ".properties";
		/*
		 * Check that the file exists
		 */
		File properties = new File(filename);
		if (properties.exists()) {
			/*
			 * Parse properties file and return appropriate Configuration instance
			 */
			PropertiesConfiguration configuration = new PropertiesConfiguration(properties);
			/*
			 * Change default behaviour so that Object type properties will throw an exception
			 * rather than returning null if not found.
			 */
			configuration.setThrowExceptionOnMissing(true);
			return configuration;
		} else {
			throw new ConfigurationException("Can't find properties files at: " + filename);
		}
	}
}
