/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.ifr.core.common.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

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
            IOUtils.closeQuietly(stream);
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
