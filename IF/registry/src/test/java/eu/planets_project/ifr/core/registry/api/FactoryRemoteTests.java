package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

/**
 * Tests for a webservice registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FactoryRemoteTests extends CoreRegistryTests {

    @BeforeClass
    public static void registryCreation() {
        registry = RegistryFactory
                .getInstance("http://localhost:8080/pserv-if-registry/RegistryWebservice?wsdl");
    }
}