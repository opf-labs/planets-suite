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
        registry = new PersistentRegistryTests().createRegistry();
    }

    Registry createRegistry() {
        return PersistentRegistry.getInstance(CoreRegistry.getInstance());
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
        ServiceDescription example = ServiceDescription.copy(description)
                .author("Me")
                /*
                 * We need to be really careful when using the copy method, if
                 * we don't null the endpoint here, the new instance will have
                 * the old endpoint, resulting in wrong comparison, unsuccessful
                 * registration, etc.
                 */
                .endpoint(null).build();
        List<ServiceDescription> updated = newRegistry.query(example);
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
                ServiceDescription.copy(description).author("Me")
                        .endpoint(null).build(), MatchingMode.EXACT);
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
        if (!register.success()) {
            String message = register.getMessage();
            System.err.println(message);
        }
        Assert.assertEquals(true, register.success());
        /* Some client instantiates a registry backed by the same directory: */
        newRegistry = createRegistry();
        checkIfPresent(description, newRegistry);
        /* Now, we change something in the original registry: */
        ServiceDescription newDescription = ServiceDescription
                .copy(description).author("Me").endpoint(
                        new URL("http://some.place")).build();
        registry.register(newDescription);
        checkIfPresent(newDescription, registry);
    }

    private void checkIfPresent(ServiceDescription description,
            Registry registry) {
        List<ServiceDescription> list = registry.query(description);
        Assert.assertEquals(1, list.size());
        ServiceDescription retrieved = list.get(0);
        Assert.assertEquals(description, retrieved);
    }

    private void clean() {
        registry.clear();
        newRegistry.clear();
    }
}
