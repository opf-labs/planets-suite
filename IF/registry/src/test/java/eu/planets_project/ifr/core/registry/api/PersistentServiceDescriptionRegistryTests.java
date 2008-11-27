package eu.planets_project.ifr.core.registry.api;

import org.junit.BeforeClass;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Tests for the persistent ServiceDescriptionRegistry.
 * @see ServiceDescriptionRegistry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class PersistentServiceDescriptionRegistryTests extends
        CoreServiceDescriptionRegistryTests {
    @BeforeClass
    public static void registryCreation() {
        registry = PersistentServiceDescriptionRegistry
                .getInstance(CoreServiceDescriptionRegistry.getInstance());
    }
}
