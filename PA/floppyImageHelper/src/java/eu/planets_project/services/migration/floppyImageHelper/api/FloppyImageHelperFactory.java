package eu.planets_project.services.migration.floppyImageHelper.api;

import java.util.logging.Logger;
import eu.planets_project.services.migration.floppyImageHelper.impl.utils.UniversalFloppyImageHelper;

public final class FloppyImageHelperFactory {

    /** Enforce non-instantiability with a private constructor. */
    private FloppyImageHelperFactory() {}
    
    private static Logger log = Logger.getLogger(FloppyImageHelperFactory.class.getName());
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String VERSION = System.getProperty("os.version");
    private static String ARCHITECTURE = System.getProperty("os.arch");

    /**
     * Hook up to an instance of the Planets FloppyImageHelper.
     * @return A windows based FloppyImageHelper instance
     */
    public static FloppyImageHelper getFloppyImageHelperInstance() {
        return checkOperatingSystemAndCreateInstance();
//        return new UniversalFloppyImageHelper();
    }
    
    private static FloppyImageHelper checkOperatingSystemAndCreateInstance() {
		FloppyImageHelper floppyHelper = null;
		if(OS.contains("windows")) {
			floppyHelper = new UniversalFloppyImageHelper();
			log.info("Created FloppyImageHelper instance of type: " + floppyHelper.getClass().getCanonicalName());
			return floppyHelper;
		}
		if(OS.contains("linux") || OS.contains("unix") /*|| OS.contains("mac")*/) {
			floppyHelper = new UniversalFloppyImageHelper();
			log.info("Created FloppyImageHelper instance of type: " + floppyHelper.getClass().getCanonicalName());
			return floppyHelper;
		}
		log.severe("Sorry, your Operating System " + OS.toUpperCase() + " " + VERSION + " " + ARCHITECTURE +  " is not supported by this service!");
		return null;
	}
}
