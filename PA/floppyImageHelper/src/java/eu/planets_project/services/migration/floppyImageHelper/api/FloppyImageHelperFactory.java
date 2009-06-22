package eu.planets_project.services.migration.floppyImageHelper.api;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.migration.floppyImageHelper.impl.utils.FloppyImageHelperUnix;
import eu.planets_project.services.migration.floppyImageHelper.impl.utils.FloppyImageHelperWin;

public final class FloppyImageHelperFactory {

    /** Enforce non-instantiability with a private constructor. */
    private FloppyImageHelperFactory() {}
    
    private static Log log = LogFactory.getLog(FloppyImageHelperFactory.class);
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String VERSION = System.getProperty("os.version");
    private static String ARCHITECTURE = System.getProperty("os.arch");

    /**
     * Hook up to an instance of the Planets FloppyImageHelper.
     * @return A windows based FloppyImageHelper instance
     */
    public static FloppyImageHelper getFloppyImageHelperInstance() {
        return checkOperatingSystemAndCreateInstance();
    }
    
    private static FloppyImageHelper checkOperatingSystemAndCreateInstance() {
		FloppyImageHelper floppyHelper = null;
		if(OS.contains("windows")) {
			floppyHelper = new FloppyImageHelperWin();
			log.info("Created FloppyImageHelper instance of type: " + floppyHelper.getClass().getCanonicalName());
			return floppyHelper;
		}
		if(OS.contains("linux") || OS.contains("unix") || OS.contains("mac")) {
			floppyHelper = new FloppyImageHelperUnix();
			log.info("Created FloppyImageHelper instance of type: " + floppyHelper.getClass().getCanonicalName());
			return floppyHelper;
		}
		log.error("Sorry, your Operating System " + OS.toUpperCase() + " " + VERSION + " " + ARCHITECTURE +  " is not supported by this service!");
		return null;
	}
}
