package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.impl.ServiceRegistryManager;

/**
 * Tests of the service registry manager implementation.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryManagerTests extends ServiceRegistryTests {
    @BeforeClass
    public static void setup() {
        registry = new ServiceRegistryManager();
        mock = new ServiceRegistryObjectFactory(USERNAME, PASSWORD, registry);
    }
}
