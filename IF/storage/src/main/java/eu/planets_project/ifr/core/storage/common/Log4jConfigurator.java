package eu.planets_project.ifr.core.storage.common;

import java.net.*;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.logging.Log;

/**
 * This utility class configures/initializes Log4J
 * 
 * @author Markus Reis, ARC
 */
public class Log4jConfigurator {
	
	private static HashMap<Class, Log> logTable = new HashMap<Class, Log>();

    /**
     * Making the default (no arg) constructor private
     * ensures that this class cannnot be instantiated.
     */
    private Log4jConfigurator() {}

    /**
     * Configures Log4J for an application using the specified Log4J XML configuration file.
     *
     * @param log4jXmlFileName The specified Log4J XML configuration file.
     */
    public static void setup(String log4jXmlFileName) {

		URL url = ResourceLoader.getAsUrl(log4jXmlFileName);

        if (url != null) {

            // An URL (from the CLASSPATH)  that points to the Log4J XML configuration
            // file was provided, so use Log4J’s DOMConfigurator with the URL to
            // initialize Log4J with the contents of the Log4J XML configuration file.

            DOMConfigurator.configure(url);
        } else {

            // An URL that points to the Log4J XML configuration file wasn’t provided,
            // so use Log4J’s BasicConfigurator to initialize Log4J.

            BasicConfigurator.configure();
        }
        
    }
    
    public static Log getLog(Class logClass, String configFile) {
    	if (!logTable.containsKey(logClass)) {
    		setup(configFile);
    		logTable.put(logClass, LogFactory.getLog(logClass));
    	}	
    	else ;
    	return logTable.get(logClass);    	
    }
        
}