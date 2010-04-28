/**
 * 
 */
package eu.planets_project.services.file;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;

/**
 * This class handles the checking of the setup for the FileIdentify service.
 * 
 * Currently this involves checking that we're running on Windows and that the Cygwin
 * file.exe can be detected at the location given in the properties file.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class FileServiceUtilities {
	/** The logger */
    private static Logger log = Logger.getLogger(FileServiceUtilities.class.getName());
    /** The configuration */
	private static Configuration configuration = ServiceConfig.getConfiguration(FileIdentify.NAME);
	/** System property key for os name */
	private final static String OS_NAME_KEY = "os.name";

	/**
	 * @return
	 * 		True if running on a windows box, false otherwise
	 */
	public static boolean isWindows() {
		return System.getProperty(OS_NAME_KEY).toLowerCase().contains("windows");
	}

	/**
	 * @return
	 * 		True is running on a linux box, false otherwise
	 */
	public static boolean isLinux() {
		return System.getProperty(OS_NAME_KEY).toLowerCase().contains("");
	}

	/**
	 * @return
	 * 		true if the cygwin file command exists on this box, false otherwise
	 */
	public static boolean isCygwinFileDetected () {
		// File cygwinFile = new File(_properties.getProperty("cygwin.file.location"));
		File cygwinFile = new File(configuration.getString("cygwin.file.location"));
		return cygwinFile.exists();
	}
	
	/**
	 * @return
	 * 		The absolute pathname of the executable
	 */
	public static String getFileLocation() {
		return new File(configuration.getString("cygwin.file.location")).getAbsolutePath();
	}
	
	public static Configuration getConfiguration() {
		return configuration;
	}
	
	//======================================================================
	// PRIVATE METHODS
	//======================================================================
}
