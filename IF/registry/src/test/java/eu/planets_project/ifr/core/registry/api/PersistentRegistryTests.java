package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.MatchingMode;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Tests for the persistent ServiceDescriptionRegistry.
 * @see Registry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class PersistentRegistryTests extends CoreRegistryTests {
    /**
     * Get a persistent registry instance for testing.
     */
    @BeforeClass
    public static void registryCreation() {
        registry = PersistentRegistry.getInstance(CoreRegistry.getInstance(),
                TEST_ROOT);
    }

    private Registry newRegistry;
    private ServiceDescription description;

    /**
     * Test to ensure the in-memory registry is up to date with the state on
     * disk. Test case for <a href="http://gforge.planets-project.eu/gf/project/pserv/tracker/?action=TrackerItemEdit&tracker_item_id=149"
     * > ticket #149</a>.
     */
    @Test
    public void syncInMemoryAndDisk() throws MalformedURLException {
        init();
        /* We want the other instance to be up to date as well: */
        List<ServiceDescription> updated = newRegistry.query(ServiceDescription
                .copy(description).author("Me").build());
        check(updated);
        clean();
    }

    /**
     * Test to ensure the in-memory registry is up to date with the state on
     * disk. Test case for <a href="http://gforge.planets-project.eu/gf/project/pserv/tracker/?action=TrackerItemEdit&tracker_item_id=149"
     * > ticket #149</a>.
     */
    @Test
    public void syncInMemoryAndDiskWithMatchingMode()
            throws MalformedURLException {
        init();
        /* The same thing should work for the other query method: */
        List<ServiceDescription> updated = newRegistry.queryWithMode(
                ServiceDescription.copy(description).author("Me").build(),
                MatchingMode.EXACT);
        check(updated);
        clean();
    }

    /**
     * @param updated The retrieved descriptions to check
     */
    private void check(List<ServiceDescription> updated) {
        Assert.assertEquals(1, updated.size());
        ServiceDescription retrievedUpdated = updated.get(0);
        Assert.assertEquals("Me", retrievedUpdated.getAuthor());
    }

    /*
     * Not using @Before and @After here to keep things local, and not spread
     * into subclasses
     */

    private void init() throws MalformedURLException {
        /* We create one registry and register one description: */
        description = ServiceDescription.create("Test", "Type").endpoint(
                new URL("http://no.where")).build();
        Response register = registry.register(description);
        if (!register.success) {
            String message = register.message;
            System.err.println(message);
        }
        Assert.assertEquals(true, register.success);
        /* Some client instantiates a registry backed by the same directory: */
        newRegistry = PersistentRegistry.getInstance(
                CoreRegistry.getInstance(), CoreRegistryTests.TEST_ROOT);
        List<ServiceDescription> list = newRegistry.query(description);
        Assert.assertEquals(1, list.size());
        ServiceDescription retrieved = list.get(0);
        Assert.assertEquals(description, retrieved);
        /* Now, if we change something in the original registry: */
        registry.register(ServiceDescription.copy(description).author("Me")
                .build());
    }

    private void clean() {
        registry.clear();
        newRegistry.clear();
    }
}
