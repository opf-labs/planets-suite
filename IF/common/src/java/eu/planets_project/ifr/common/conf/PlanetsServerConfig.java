/**
 * 
 */
package eu.planets_project.ifr.common.conf;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsServerConfig {
    private static Log log = LogFactory.getLog(PlanetsServerConfig.class);
    
    // Server properties:
    private static final String PLANETS_HOSTNAME = "planets.server.hostname";
    private static final String PLANETS_PORT = "planets.server.port";
    private static final String PLANETS_SSL_PORT = "planets.server.ssl.port";

    /**
     * Static property loader
     * @return The Properties array:
     */
    private static Properties loadProps() {
        Properties props = new Properties();
        try {
            props.load( PlanetsServerConfig.class.getResourceAsStream(
                    "/eu/planets_project/ifr/common/conf/planets-server-config.properties" ) );
        } catch( IOException e ) {
            log.error("Server properties failed to load! :: "+e);
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
