package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
 * @see CoreRegistry
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
    private static URL endpoint1;
    private static URL endpoint2;
    static Registry registry;

    /**
     * create a registry
     */
    @BeforeClass
    public static void registryCreation() {
        registry = CoreRegistry.getInstance();
        try {
            endpoint1 = new URL("http://some.dummy.endpoint");
            endpoint2 = new URL("http://another.dummy.endpoint");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    ServiceDescription description1 = null;
    ServiceDescription description2 = null;

    /**
     * Test the registration of services
     */
    @Before
    public void registerSampleServices() {
        registry.clear();
        description1 = new ServiceDescription.Builder(NAME, TYPE1).description(
                DESCRIPTION).inputFormats(URI.create(PRONOM1),
                URI.create(PRONOM2)).endpoint(endpoint1).build();
        description2 = new ServiceDescription.Builder(NAME, TYPE2)
                .inputFormats(URI.create(PRONOM1), URI.create(PRONOM2))
                .endpoint(endpoint2).build();
        Response register = registry.register(description1);
        Assert.assertTrue(register.success);
        /* But we can't register descriptions without an endpoint: */
        Response fail = registry.register(new ServiceDescription.Builder(NAME,
                TYPE1).build());
        Assert.assertFalse(fail.success);
        registry.register(description2);
    }

    /**
     * Clear up the registry
     */
    @After
    public void cleanupRegistry() {
        registry.clear();
    }

    /**
     * Register a single service description
     */
    @Test
    public void registerServiceDescription() {
        Response message = registry.register(description1);
        Assert.assertEquals("Double registration!", 2, registry.query(null)
                .size());
        Assert.assertNotNull("No result message;", message);
        System.out.println("Registered: " + message);
    }

    /**
     * test query by service name
     */
    @Test
    public void findByName() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(NAME, null).build());
        compare(services);
    }

    /**
     * test query by service type
     */
    @Test
    public void findByType() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, TYPE1).build());
        compare(services);
    }

    /**
     * test query by description
     */
    @Test
    public void findByDescription() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, null).description(
                        DESCRIPTION).build());
        compare(services);
    }

    /**
     * test query by name and description
     */
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

    /**
     * test query by input format
     */
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

    /**
     * test query by input format and service type
     */
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
     * query by service endpoint
     */
    @Test
    public void findByEndpoint() {
        List<ServiceDescription> services = registry
                .query(new ServiceDescription.Builder(null, null).endpoint(
                        endpoint1).build());
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    /**
     * Test service deletion 
     */
    @Test
    public void deleteByExample() {
        Response response = registry.delete(new ServiceDescription.Builder(
                null, TYPE1).endpoint(endpoint1).build());
        Assert.assertTrue(response.success);
        List<ServiceDescription> services = registry.query(null);
        Assert.assertEquals(1, services.size());
    }

    /**
     * We don't allow duplicate endpoints so check
     */
    @Test
    public void duplicateEndpointGuard() {
        Response response = registry.register(new ServiceDescription.Builder(
                null, TYPE1).endpoint(endpoint1).build());
        Assert.assertFalse(response.success);
        List<ServiceDescription> services = registry.query(null);
        Assert.assertEquals(2, services.size());
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
