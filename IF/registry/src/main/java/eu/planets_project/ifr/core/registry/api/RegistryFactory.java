package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Service;


/**
 * Service Registry factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class RegistryFactory {
    /** Access only via static methods. */
    private RegistryFactory() {}

    /** @return A registry instance */
    public static Registry getRegistry() {
        return PersistentRegistry.getInstance(CoreRegistry.getInstance());
    }

    /**
     * @param wsdlLocation The location of the WSDL
     * @return A registry instance running on the specified location
     */
    public static Registry getRegistry(final String wsdlLocation) {
        URL url = null;
        try {
            url = new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, Registry.QNAME);
        Registry registryService = service.getPort(Registry.class);
        return registryService;
    }
}
