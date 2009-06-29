package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.Service;

/**
 * Service registry factory methods.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class RegistryFactory {
    /** The shared local instance. */
    private static final Registry INSTANCE = PersistentRegistry
            .getInstance(CoreRegistry.getInstance());

    /** Thread-safe mapping of WSDL locations to registry instances. */
    private static ConcurrentHashMap<String, Registry> registries = new ConcurrentHashMap<String, Registry>();

    /** Access only via static methods. */
    private RegistryFactory() {}

    /** @return A local service registry instance */
    public static Registry getRegistry() {
        return INSTANCE;
    }

    /**
     * @param wsdlLocation The location of the WSDL
     * @return A registry instance running on the specified location
     */
    public static Registry getRegistry(final String wsdlLocation) {
        /* We cache and reuse registries for the individual WSDL locations: */
        synchronized (registries) {
            if (!registries.containsKey(wsdlLocation)) {
                Registry registry = createRegistry(wsdlLocation);
                if (registry == null) {
                    return null;
                }
                registries.put(wsdlLocation, registry);
            }
            return registries.get(wsdlLocation);
        }
    }

    /**
     * @param wsdlLocation The WSDL location of the registry to instantiate
     * @return A service registry instance running at the specified WSDL
     *         location or null
     */
    private static Registry createRegistry(final String wsdlLocation) {
        URL url = null;
        try {
            url = new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service;
        try {
            service = Service.create(url, Registry.QNAME);
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
        Registry registryService = service.getPort(Registry.class);
        return registryService;
    }
}
