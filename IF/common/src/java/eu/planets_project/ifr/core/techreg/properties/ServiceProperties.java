/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.ifr.core.techreg.properties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import eu.planets_project.services.datatypes.Property;

/**
 * @author AnJackson
 *
 */
public class ServiceProperties {
    /** */
    private static Logger log = Logger.getLogger(ServiceProperties.class.getName());
    
    /** A standard environment identifier for the Java System.getProperties. */
    public static final URI ENV_JAVA_SYS_PROP = URI.create("planets:if/srv/java-system-properties");
    
    /** A standard Planets identifier for the wall-clock time a process takes to execute. */
    public static final URI SERVICE_WALLCLOCK_PROP = URI.create("planets:tb/srv/exec/wallclock");
    
    
    /**
     * A utility to construct a 'wall-clock' time estimate from two times, as gleaned from System.currentTimeMillis()
     * 
     * @param startTimeInMillis
     * @param endTimeInMillis
     * @return A Property for this value.
     */
    public static Property createWallClockTimeProperty(long startTimeInMillis, long endTimeInMillis ) {
        Property.Builder p = new Property.Builder( SERVICE_WALLCLOCK_PROP );
        p.name("Wall-clock time");
        p.value(""+((endTimeInMillis-startTimeInMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time, as would be measured by a clock on the wall.");
        return p.build();
    }

    /**
     * Add or Update automatically generated list of JVM/OS properties.
     * Embeds information about the service environment inside the service description 
     * as a property called 'planets:if/srv/java-system-properties'
     */
    /*
     * TODO Upgrade this idea to some standardised form for platform/environment/software stacks.
     */
    public static Property createServerDescriptionProperty() {
        java.util.Properties p = System.getProperties();
        
        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        try {
            p.storeToXML(byos, "Automatically generated server description.", "UTF-8");
            Property jspp = new Property(ENV_JAVA_SYS_PROP,"Java JVM System Properties", byos.toString("UTF-8") );
            return jspp;
        } catch ( IOException e ) {
            // Fail silently.
            log.fine("IOException when storing server properties to XML. "+e);
        }
        
        return null;
    }

}
