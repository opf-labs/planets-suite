package eu.planets_project.ifr.core.registry.api;

/**
 * Tests for a local registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistryFactoryTests extends PersistentRegistryTests {
    @Override
    Registry createRegistry() {
        return RegistryFactory.getRegistry();
    }
}