/**
 * Mock up of the Data Registry EJB, which is backed by a simple 
 * directory instead of Jackrabbit.
 */
package eu.planets_project.ifr.core.wdt.api.data;

import java.net.URI;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * @author AnJackson
 *
 */
public interface DataRegistryManager {

    public abstract DataManagerLocal getDataManager(URI puri);

    public abstract boolean canAccessURI(URI puri);

    public abstract DigitalObject[] list(URI puri);

    public abstract DigitalObject getRootDigitalObject();
}
