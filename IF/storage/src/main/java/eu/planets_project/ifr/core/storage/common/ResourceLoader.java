package eu.planets_project.ifr.core.storage.common;

import eu.planets_project.ifr.core.storage.common.*;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


/**
 * ResourceLoader is a utility class that has methods for retrieving application
 * resources (flat files, Properties files, ResourceBundles, and so on) from an
 * application's CLASSPATH (usually inside a JAR file) using a ClassLoader.
 *
 * Resources may be retrieved as Properties objects or URLs.
 *
 *  @author Markus Reis, ARC
 *
 */
public class ResourceLoader {

    /**
     * Making the default (no arg) constructor private
     * ensures that this class cannnot be instantiated.
     */
    private ResourceLoader() {}

    /**
     * Retrieves a resource for the given name in the CLASSPATH using the specified Class
     * Loader, returning it as a Properties object.
     *
     * @param name The resource name.
     *
     * @return Properties The Properties object for the resource.
     *
     */
    public static Properties getAsProperties(String name) {
        Properties props = new Properties();
        URL url = ResourceLoader.getAsUrl(name);

 		if (url != null) {
			try {
				// Load the properties using the URL (from the CLASSPATH).

				props.load(url.openStream());
			} catch (IOException e) {
			}
		}

        return props;
    }

    /**
     * Retrieves a resource for the given name in the CLASSPATH, returning it as an URL.
     *
     * @param name The resource name.
     *
     * @return URL The URL for reading data from the resource, or null
     *             if the resource wan't found.
     *
     */
    public static URL getAsUrl(String name) {

		// The Thread Context ClassLoader is the ClassLoader used by the creator
		// of the Thread that runs your code. By using the Thread Context ClassLoader,
		// we’re guaranteed to load the resource (class or property file) as long as
		// it's on the application's CLASSPATH.

		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader classLoader = new ResourceLoader().getClass().getClassLoader();
		return classLoader.getResource(name);
    }
}