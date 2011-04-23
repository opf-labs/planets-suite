package eu.planets_project.ifr.core.storage.common;

import java.util.Properties;


/**
 * This utility class modfies system properties
 * 
 * @author Markus Reis, ARC
 */
public class SystemPropertiesUtil {

    /**
     * Making the default (no arg) constructor private
     * ensures that this class cannnot be instantiated.
     */
    private SystemPropertiesUtil() {}

    /**
     * Adds the keys/properties from an application-specific properties file to the System Properties.
     * The application-specific properties file MUST reside in a directory listed on the CLASSPATH.
     *
     * @param propsFileName The name of an application-specific properties file.
     *
     */
    public static void addToSystemPropertiesFromPropsFile(String propsFileName) {
		Properties props = ResourceLoader.getAsProperties(propsFileName),
                   sysProps = System.getProperties();

		// Add the keys/properties from the application-specific properties  to the System Properties.

		sysProps.putAll(props);
	}	
	
}
