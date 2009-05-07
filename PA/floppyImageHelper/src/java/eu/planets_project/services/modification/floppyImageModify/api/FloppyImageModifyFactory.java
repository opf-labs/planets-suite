package eu.planets_project.services.modification.floppyImageModify.api;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.modification.floppyImageModify.impl.FloppyImageModifyWin;

public final class FloppyImageModifyFactory {

    /** Enforce non-instantiability with a private constructor. */
    private FloppyImageModifyFactory() {}
    
    private static Log log = LogFactory.getLog(FloppyImageModifyFactory.class);
    
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String VERSION = System.getProperty("os.version");
    private static String ARCHITECTURE = System.getProperty("os.arch");

    /**
     * Hook up to an instance of the Planets FloppyImageHelper.
     * @return A windows based FloppyImageHelper instance
     */
    public static FloppyImageModify getFloppyImageModifyInstance() {
        return checkOperatingSystemAndCreateInstance();
    }
    
    private static FloppyImageModify checkOperatingSystemAndCreateInstance() {
    	FloppyImageModifyWin floppyModify = null;
		if(OS.toLowerCase().contains("windows")) {
			floppyModify = new FloppyImageModifyWin();
			return floppyModify;
		}
		log.error("Sorry, your Operating System " + OS.toUpperCase() + " " + VERSION + " " + ARCHITECTURE +  " is not supported by this service!");
		return null;
	}
}
