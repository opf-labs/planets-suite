package eu.planets_project.ifr.core.registry.api;

import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Tests for the CoreServiceDescriptioRegistry, demonstrating the
 * query-by-example functionality.
 * @see CoreServiceDescriptioRegistry
 * @see Registry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class CoreRegistryTests {
    static final String TEST_ROOT = "IF/registry/src/test/resources/service-description-registry/";
    static final String PRONOM1 = "pronom:fmt/10";
    static final String PRONOM2 = "pronom:fmt/11";
    static final String DESCRIPTION = "description";
    static final String TYPE1 = "type1";
    static final String TYPE2 = "type2";
    static final String NAME = "name";
    static Registry registry;

    @BeforeClass
    public static void registryCreation() {
        registry = CoreRegistry.getInstance();
    }

    ServiceDescription description1 = null;
    ServiceDescription description2 = null;

    @Before
    public void registerSampleServices() {
        registry.clear();
        description1 = new ServiceDescription.Builder(NAME, TYPE1).description(
                DESCRIPTION).inputFormats(URI.create(PRONOM1),
                URI.create(PRONOM2)).build();
        description2 = new ServiceDescription.Builder(NAME, TYPE2)
                .inputFormats(URI.create(PRONOM1), URI.create(PRONOM2)).build();
        registry.register(description1);
        registry.register(description2);
    }

    @After
    public void cleanupRegistry() {
        registry.clear();
    }

    @Test
    public void registerServiceDescription() {
        Response message = registry.register(description1);
        Assert.assertEquals("Double registration!", 2, registry.query(null)
                .size());
        Assert.assertNotNull("No result message;", message);
        System.out.println("Registered: " + message);
    }

    @Test
    public void findByName() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(NAME, null).build());
        compare(services);
    }

    @Test
    public void findByType() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, TYPE1).build());
        compare(services);
    }

    @Test
    public void findByDescription() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, null).description(
                        DESCRIPTION).build());
        compare(services);
    }

    @Test
    public void findByNameAndDescription() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(NAME, null).description(
                        DESCRIPTION).build());
        /*
         * This should only retrieve the first description, with matching name
         * and description:
         */
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    @Test
    public void findByInputFormat() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, null).inputFormats(
                        URI.create(PRONOM1)).build());
        /*
         * This should retrieve both descriptions, as both of them support the
         * specified input format (among others):
         */
        Assert.assertEquals(2, services.size());
        compare(services);
    }

    @Test
    public void findByInputFormatAndType() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, TYPE1)
                        .inputFormats(URI.create(PRONOM1)).build());
        /*
         * While this should only retrieve the first description, as the type is
         * only corresponding to the first description:
         */
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    /**
     * @param services The retrieved services to be compared to the testing data
     */
    private void compare(List<ServiceDescription> services) {
        Assert.assertTrue("No services found!", services.size() > 0);
        Assert.assertEquals(description1.getName(), services.get(0).getName());
        Assert.assertEquals(description1.getDescription(), services.get(0)
                .getDescription());
        Assert.assertEquals(description1.getType(), services.get(0).getType());
    }
}
