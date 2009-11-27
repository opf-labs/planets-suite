/**
 * 
 */
package eu.planets_project.ifr.core.common.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import eu.planets_project.services.utils.FileUtils;

/**
 * Planets server configuration.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsServerConfig {
    private static Logger log = Logger.getLogger(PlanetsServerConfig.class.getName());
    
    // Server properties:
    private static final String PLANETS_HOSTNAME = "planets.server.hostname";
    private static final String PLANETS_PORT = "planets.server.port";
    private static final String PLANETS_SSL_PORT = "planets.server.ssl.port";

    /**
     * Static property loader.
     * @return The Properties array:
     */
    private static Properties loadProps() {
        Properties props = new Properties();
        InputStream stream = null;
        try {
            stream = PlanetsServerConfig.class.getResourceAsStream(
                    "/eu/planets_project/ifr/core/common/conf/planets-server-config.properties" );
            props.load( stream );
        } catch( IOException e ) {
            log.severe("Server properties failed to load! :: "+e);
            FileUtils.close(stream);
        }
        return props;
    }


    /**
     * The name of the server.
     * 
     * @return The configured hostname of this deployment.
     */
    public static String getHostname() {
        return loadProps().getProperty(PLANETS_HOSTNAME);
    }

    /**
     * The server port.
     * 
     * @return The port number.
     */
    public static int getPort() {
        return Integer.parseInt(loadProps().getProperty(PLANETS_PORT));
    }
    
    /**
     * The server secure sockets port.
     * 
     * @return The port number.
     */
    public static int getSSLPort() {
        return Integer.parseInt(loadProps().getProperty(PLANETS_SSL_PORT));
    }
    
}
