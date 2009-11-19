package eu.planets_project.ifr.core.servreg.api;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;

/**
 * Tests for a remote registry instance retrieved via the factory.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceRegistryLocalTests extends PersistentRegistryTests {

    @BeforeClass
    public static void registryCreation() {
        registry = new ServiceRegistryLocalTests().createRegistry();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.PersistentRegistryTests#createRegistry()
     */
    @Override
    ServiceRegistry createRegistry() {
        return ServiceRegistryFactory.getRegistry();
    }
}