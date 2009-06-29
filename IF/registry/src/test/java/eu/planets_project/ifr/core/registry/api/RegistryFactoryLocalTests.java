package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

/**
 * Tests for a remote registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistryFactoryLocalTests extends PersistentRegistryTests {

    @BeforeClass
    public static void registryCreation() {
        registry = new RegistryFactoryLocalTests().createRegistry();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.PersistentRegistryTests#createRegistry()
     */
    @Override
    Registry createRegistry() {
        return RegistryFactory.getRegistry();
    }
}