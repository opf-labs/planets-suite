package eu.planets_project.ifr.core.registry.api.jaxr;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.impl.jaxr.ServiceRegistryManager;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests of the web service version of the service registry.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryWebServiceTests extends ServiceRegistryTests {
    /** Create a registry and a mock object factory once for all tests. */
    @BeforeClass
    public static void setup() {
        if(ServiceRegistryTestsHelper.guard()) return;
        /*
         * We run the same tests as in the super test, but against the web
         * service version of the service registry:
         */
        registry = ServiceCreator
                .createTestService(ServiceRegistry.QNAME,
                        ServiceRegistryManager.class,
                        "/pserv-if-registry-pserv-if-registry/ServiceRegistryManager?wsdl");
        mock = new ServiceRegistryObjectFactory(USERNAME, PASSWORD, registry);
    }
}
