package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.impl.RegistryWebservice;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the persistent ServiceDescriptionRegistry.
 * @see Registry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistryWebserviceTests extends CoreRegistryTests {

    /**
     * set up by creating a registry before testing 
     */
    @BeforeClass
    public static void registryCreation() {
        registry = ServiceCreator.createTestService(Registry.QNAME,
                RegistryWebservice.class,
                "/pserv-if-registry-pserv-if-registry/RegistryWebservice?wsdl");
    }
}
