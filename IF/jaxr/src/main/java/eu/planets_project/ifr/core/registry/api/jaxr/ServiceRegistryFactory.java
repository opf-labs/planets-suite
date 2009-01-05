package eu.planets_project.ifr.core.registry.api.jaxr;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Factory class for central retrieval of registry instances.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryFactory {
    /**
     * We enforce non-instantiability with a private constructor (this is a
     * utility class).
     */
    private ServiceRegistryFactory() {}

    /** Service endpoint for the local registry service. */
//    private static final String WSDL = "http://localhost:8080/"
//            + "registry-ifr-registry-ejb/ServiceRegistryManager?wsdl";

    /**
     * @return A service registry instance
     */
    public static ServiceRegistry getInstance() {
        /*
         * FIXME OK for now, as the interface methods require authentication,
         * but no permanent solution
         */
        return JaxrServiceRegistry.getInstance("provider", "provider");
    }

    /**
     * This is incomplete work-in-progress, which is why it currently is package
     * private (aka default visibility).
     * @return A service registry instance
     */
//    static Registry getServiceDescriptionRegistryInstance() {
//        return PersistentRegistry
//                .getInstance(CoreRegistry.getInstance());
//    }

    /**
     * @param wsdl The WSDL location for the registry
     * @return A registry instance running at the given WSDL location
     */
    public static ServiceRegistry getInstance(final String wsdl) {
        URL url = null;
        try {
            url = new URL(wsdl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(ServiceRegistry.NS,
                ServiceRegistry.NAME));
        ServiceRegistry registryService = service
                .getPort(ServiceRegistry.class);
        return registryService;
    }

}
