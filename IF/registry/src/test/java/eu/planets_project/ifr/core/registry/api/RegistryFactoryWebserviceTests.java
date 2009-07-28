package eu.planets_project.ifr.core.registry.api;

import javax.xml.ws.WebServiceException;

import org.junit.BeforeClass;

/**
 * Tests for a remote registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistryFactoryWebserviceTests extends RegistryWebserviceTests {

    private static final String REGISTRY_WEBSERVICE_WSDL = "http://localhost:8080/pserv-if-registry-pserv-if-registry/RegistryWebservice?wsdl";

    /** Get a remote registry instance for testing. */
    @BeforeClass
    public static void registryCreation() {
        registry = new RegistryFactoryWebserviceTests().createRegistry();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.PersistentRegistryTests#createRegistry()
     */
    @Override
    Registry createRegistry() {
        /*
         * Test retrieving a service registry instance from a specified WSDL
         * location:
         */
        try {
            Registry remoteRegistry = RegistryFactory.getRegistry(REGISTRY_WEBSERVICE_WSDL);
            /*
             * We fall back during testing if we are not running a server. Here,
             * it makes no sense to use the ServiceCreator as for the other
             * tests, as we explicitly want to test the factory method that
             * takes a full WSDL location and returns the registry webservice at
             * that location.
             */
            return remoteRegistry != null && serverMode() ? remoteRegistry : fallBack();
        } catch (WebServiceException x) {
            /*
             * We need this particular case when we have a running server but
             * are testing standalone:
             */
            return fallBack();
        }
    }

    private static Registry fallBack() {
        System.out.println("Falling back to local instance while testing remote registry webservice at: "
                + REGISTRY_WEBSERVICE_WSDL);
        return RegistryFactory.getRegistry();
    }
}