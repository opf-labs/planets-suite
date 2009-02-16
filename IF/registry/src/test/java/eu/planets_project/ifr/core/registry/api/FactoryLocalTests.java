package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

/**
 * Tests for a local registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FactoryLocalTests extends PersistentRegistryTests {

    /**
     * Get a registry instance.
     */
    @BeforeClass
    public static void registryCreation() {
        registry = RegistryFactory.getInstance();
    }
}