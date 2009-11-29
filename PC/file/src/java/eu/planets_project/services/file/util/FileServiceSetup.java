/**
 * 
 */
package eu.planets_project.services.file.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class handles the checking of the setup for the FileIdentify service.
 * 
 * Currently this involves checking that we're running on Windows and that the Cygwin
 * file.exe can be detected at the location given in the properties file.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class FileServiceSetup {
	/** The logger */
    private static Logger log = Logger.getLogger(FileServiceSetup.class.getName());
    /** Properties file location and Properties holder */
	private static final String PROPERTIES_PATH = "eu/planets_project/services/file/FileIdentify.properties";
	/** Location of cygwin file.exe **/
	private static final String FILECYGWIN_HOME = System.getenv("FILECYGWIN_HOME");

	/**
	 * The properties loaded from the properties file
	 */
	private static Properties _properties = null;
	
	/**
	 * @return
	 * 		True if running on a windows box, false otherwise
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/**
	 * @return
	 * 		true if the cygwin file command exists on this box, false otherwise
	 */
	public static boolean isCygwinFileDetected () {
		// Load the properties if not done so already
		if (FileServiceSetup._properties == null) FileServiceSetup.loadProperties();
		
		// Now let's get the cygwin file location property and make sure it exists
		// File cygwinFile = new File(_properties.getProperty("cygwin.file.location"));
		File cygwinFile = new File(FILECYGWIN_HOME, "file.exe");
		return cygwinFile.exists();
	}
	
	/**
	 * @return
	 * 		The static properties object
	 */
	public static Properties getProperties() {
		if (FileServiceSetup._properties == null) FileServiceSetup.loadProperties();
		return FileServiceSetup._properties;
	}
	
	/**
	 * @return
	 * 		The absolute pathname of the executable
	 */
	public static String getFileLocation() {
		return new File(FILECYGWIN_HOME, "file.exe").getAbsolutePath();
	}
	
	//======================================================================
	// PRIVATE METHODS
	//======================================================================

	private static void loadProperties() {
		try {
			// Create a new properties object and load the properties
			FileServiceSetup._properties = new Properties();
			FileServiceSetup._properties.load(FileServiceSetup.class.getClassLoader().getResourceAsStream(FileServiceSetup.PROPERTIES_PATH));
		} catch (IOException exp) {
			// Hopefully this won't happen, it's unrecoverable if it does
			// We'll log it and then set _propertes to null
			FileServiceSetup.log.fine("IOException processing properties file: "+ exp.getMessage());
			FileServiceSetup._properties = null;
		}
	}
}
