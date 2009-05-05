package eu.planets_project.services.migration.floppyImageHelper.api;


import eu.planets_project.services.migration.floppyImageHelper.impl.FloppyImageHelperUnix;
import eu.planets_project.services.migration.floppyImageHelper.impl.FloppyImageHelperWin;

public final class FloppyImageHelperFactory {

    /** Enforce non-instantiability with a private constructor. */
    private FloppyImageHelperFactory() {}

    /**
     * Hook up to an instance of the Planets FloppyImageHelper.
     * @return A windows based FloppyImageHelper instance
     */
    public static FloppyImageHelper getWindowsFloppyImageHelper() {
        FloppyImageHelperWin floppyHelper = new FloppyImageHelperWin();
        return floppyHelper;
    }
    
    
    /**
     * Hook up to an instance of the Planets FloppyImageHelper.
     * @return A unix based FloppyImageHelper instance
     */
    public static FloppyImageHelper getUnixFloppyImageHelper() {
    	FloppyImageHelperUnix floppyHelper = new FloppyImageHelperUnix();
    	return floppyHelper;
    }
}
