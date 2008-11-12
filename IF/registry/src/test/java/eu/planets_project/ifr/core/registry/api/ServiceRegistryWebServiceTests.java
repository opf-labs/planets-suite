package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;


/**
 * Tests of the web service version of the service registry.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryWebServiceTests extends ServiceRegistryTests {
    /** The remote registry instance used for testing. */
    private static final String TEST_SERVER_WSDL = "http://localhost:8080/"
            + "pserv-if-service-registry/ServiceRegistryManager?wsdl";

    /** Create a registry and a mock object factory once for all tests. */
    @BeforeClass
    public static void setup() {
        /*
         * We run the same tests as in the super test, but against the web
         * service version of the service registry:
         */
        registry = ServiceRegistryFactory.getInstance(TEST_SERVER_WSDL);
        mock = new ServiceRegistryObjectFactory(USERNAME, PASSWORD, registry);
    }
}
