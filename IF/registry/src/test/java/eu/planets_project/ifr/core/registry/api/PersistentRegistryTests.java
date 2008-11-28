package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Tests for the persistent ServiceDescriptionRegistry.
 * @see Registry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class PersistentRegistryTests extends
        CoreRegistryTests {
    @BeforeClass
    public static void registryCreation() {
        registry = PersistentRegistry
                .getInstance(CoreRegistry.getInstance(),TEST_ROOT);
    }
}
