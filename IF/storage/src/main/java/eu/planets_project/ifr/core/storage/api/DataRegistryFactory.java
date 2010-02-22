package eu.planets_project.ifr.core.storage.api;

import java.net.URI;
import java.net.URISyntaxException;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Data registry factory and utility methods.
 * @author CFWilson, Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class DataRegistryFactory {

    /** Enforce non-instantiability with a private constructor. */
    private DataRegistryFactory() {}

    /**
     * Factory method, this is the way to get a DataRegistry instance.
     * @return The DataRegistry instance
     */
    public static DataRegistry getDataRegistry() {
        return DataRegistryImpl.getInstance();
    }

    /**
     * @param name The name to create a data registry ID for
     * @return The data registry ID for the given name
     * @throws URISyntaxException If the URI created for the given name is invalid
     */
    public static URI createDataRegistryIdFromName(final String name) throws URISyntaxException {
        return DataRegistryImpl.createDataRegistryIdFromName(name);
    }

    /**
     * This is a bit of a hack required by the test bed, it's NOT in the interface, it's specific to
     * the implementation. FIXME: This needs to be better
     * @param dataReg The data registry
     * @param uri The URI identifying the object to retrieve
     * @return A DigitalObject where the content is guaranteed to be a TB reference
     * @throws DigitalObjectNotFoundException If the object identified by the URI could not be found
     *         in the given registry
     */
    public static DigitalObject retrieveAsTbReference(final DataRegistry dataReg, final URI uri)
            throws DigitalObjectNotFoundException {
        return ((DataRegistryImpl) dataReg).retrieveAsTbReference(uri);
    }

}
