package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.impl.RegistryWebservice;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for a webservice registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FactoryRemoteTests extends CoreRegistryTests {

    /**
     * get a remote instance
     */
    @BeforeClass
    public static void registryCreation() {
        registry = ServiceCreator.createTestService(Registry.QNAME,
                RegistryWebservice.class,
                "/pserv-if-registry-pserv-if-registry/RegistryWebservice?wsdl");
    }
}