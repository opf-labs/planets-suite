package eu.planets_project.ifr.core.registry.api.jaxr;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryObjectFactory;
import eu.planets_project.ifr.core.registry.impl.jaxr.ServiceRegistryManager;

/**
 * Tests of the service registry manager implementation.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceRegistryManagerTests extends ServiceRegistryTests {
    @BeforeClass
    public static void setup() {
        if(ServiceRegistryTestsHelper.guard()) return;
        registry = new ServiceRegistryManager();
        mock = new ServiceRegistryObjectFactory(USERNAME, PASSWORD, registry);
    }
}
