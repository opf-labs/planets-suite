package eu.planets_project.ifr.core.servreg.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.Service;

/**
 * Service registry factory methods.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryFactory {
    /** The shared local instance. */
    private static final ServiceRegistry INSTANCE = PersistentRegistry
            .getInstance(CoreRegistry.getInstance());

    /** Thread-safe mapping of WSDL locations to registry instances. */
    private static ConcurrentHashMap<String, ServiceRegistry> registries = new ConcurrentHashMap<String, ServiceRegistry>();

    /** Access only via static methods. */
    private ServiceRegistryFactory() {}

    /** @return A local service registry instance */
    public static ServiceRegistry getRegistry() {
        return INSTANCE;
    }

    /**
     * @param wsdlLocation The location of the WSDL
     * @return A registry instance running on the specified location
     */
    public static ServiceRegistry getRegistry(final String wsdlLocation) {
        /* We cache and reuse registries for the individual WSDL locations: */
        synchronized (registries) {
            if (!registries.containsKey(wsdlLocation)) {
                ServiceRegistry registry = createRegistry(wsdlLocation);
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
    private static ServiceRegistry createRegistry(final String wsdlLocation) {
        URL url = null;
        try {
            url = new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service;
        try {
            service = Service.create(url, ServiceRegistry.QNAME);
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
        ServiceRegistry registryService = service.getPort(ServiceRegistry.class);
        return registryService;
    }
}