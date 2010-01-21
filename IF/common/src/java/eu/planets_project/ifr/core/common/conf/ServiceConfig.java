package eu.planets_project.ifr.core.common.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Factory for obtaining configuration parameters. Configuration files
 * are "properties" files, stored in a common directory which is defined
 * by the system property "eu.planets-project.config.dir"
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
 * @author Ian Radford
 */
public final class ServiceConfig {
	private static Logger _log = Logger.getLogger(ServiceConfig.class.getName()); 

	static final String BASE_DIR_PROPERTY = "eu.planets-project.config.dir";
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
		_log.info("Class name is " + className);
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
        if (baseDirectory == null) {
            _log.warning(String.format("System property %s is not set, looking for file in current directory...",
                    BASE_DIR_PROPERTY));
            baseDirectory = ".";
        }
		String filename = baseDirectory + File.separatorChar + basename + ".properties";

		return getConfiguration(new File(filename));
	}
	
	/**
	 * Return a configuration based on the contents of the passed file
	 * @param properties the config file
	 * @return Representation of the config file
	 */
	public static Configuration getConfiguration(File properties) {
		_log.info("File loc:" + properties.getAbsolutePath());
		/*
		 * Check that the file exists
		 */
		if (properties.exists()) {
			/*
			 * Parse properties file and return appropriate Configuration instance
			 */
			Properties configuration = new Properties();
			try {
				configuration.load(new FileInputStream(properties));
			} catch (FileNotFoundException e) {
				// This shouldn't happen as we've previously checked...
				throw new ConfigurationException("Configuration file missing: " + properties.getName(), e);
			} catch (IOException e) {
				throw new ConfigurationException("Error reading configuration file: " + properties.getName(), e);
			}
			/*
			 * Return a local implementation of the Configuration interface
			 * from the loaded properties.
			 */
			return new ConfigurationImpl(configuration);
		}
		_log.info("Properties file doesn't exist at " + properties.getAbsolutePath());
		throw new ConfigurationException("Can't find properties files at: " + properties.getAbsolutePath());
	}

	private static final class ConfigurationImpl implements Configuration {
		private Map<String, String> properties = new HashMap<String, String>();

		public ConfigurationImpl(Properties configuration) {
			try {
				for (Entry<Object, Object> entry: configuration.entrySet()) {
					this.properties.put((String)entry.getKey(), (String)entry.getValue());
				}
			} catch (Exception e) {
				throw new ConfigurationException("Error processing properties", e);
			}
		}

		public int getInteger(String key) {
			try {
				return Integer.valueOf(getString(key));
			} catch (NumberFormatException nfe) {
				throw new ConversionException("Bad number format for: " + key, nfe);
			}
		}

		public int getInteger(String key, int defaultValue) {
			try {
				return getInteger(key);
			} catch (NoSuchElementException nse) {
				return defaultValue;
			}
		}

		public String getString(String key) {
			String value = this.properties.get(key);
			if (value != null) {
				return value;
			}
			throw new NoSuchElementException("No such property: " + key);
		}

		public String getString(String key, String defaultValue) {
			try {
				return getString(key);
			} catch (NoSuchElementException nse) {
				return defaultValue;
			}
		}

		public URI getURI(String key) {
			try {
				return new URI(getString(key));
			} catch (URISyntaxException e) {
				throw new ConversionException("Bad URI format for: " + key, e);
			}
		}

		public URI getURI(String key, URI defaultValue) {
			try {
				return getURI(key);
			} catch (NoSuchElementException nse) {
				return defaultValue;
			}
		}

		public Iterator<String> getKeys() {
			return this.properties.keySet().iterator();
		}

	}
}
