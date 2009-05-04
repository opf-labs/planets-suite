package eu.planets_project.services.migration.floppyImageHelper.api;


import eu.planets_project.services.migration.floppyImageHelper.impl.FloppyImageHelperWin;

public final class FloppyImageHelperFactory {

    /** Enforce non-instantiability with a private constructor. */
    private FloppyImageHelperFactory() {}

    /**
     * Hook up to an instance of the Planets format registry.
     * @return A format registry, as discovered via JNDI; or a local instance,
     *         if the lookup failed.
     */
    public static FloppyImageHelper getFloppyImageHelper() {
        FloppyImageHelperWin floppyHelper = new FloppyImageHelperWin();
        return floppyHelper;
    }
}
