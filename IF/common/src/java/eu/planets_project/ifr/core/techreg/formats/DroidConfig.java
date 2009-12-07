/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.io.File;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;

/**
 * Droid configuration settings.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
class DroidConfig {
	/** The logger */
    private static Logger log = Logger.getLogger(DroidConfig.class.getName());

    /** Properties keys for DROID sig file information */
    private static final String COMMON_CONF_FILE_NAME = "Common";
    private static final String SIG_FILE_LOC_KEY = "droid.sigfile.location";
    private static final String SIG_FILE_NAME_KEY = "droid.sigfile.name";
    /**
     * @return The location of the DROID signature file taken from the
     * 		   Droid configuration properties file
     */
    public static String getSigFileLocation() {
    	// String to hold the location
        String sigFileLocation = null;
        // Get the configuration object from the ServiceConfig util
        Configuration conf = ServiceConfig.getConfiguration(COMMON_CONF_FILE_NAME);
        // Create the file name from the properties
        sigFileLocation = conf.getString(SIG_FILE_LOC_KEY) +
        		File.separator + conf.getString(SIG_FILE_NAME_KEY);
        log.info("DROID Signature File location:" + sigFileLocation);
        return sigFileLocation;
    }

}
