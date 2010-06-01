package eu.planets_project.ifr.core.servreg.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.servreg.api.CoreRegistry;
import eu.planets_project.ifr.core.servreg.api.MatchingMode;
import eu.planets_project.ifr.core.servreg.api.Response;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Tests for the CoreServiceDescriptioRegistry, demonstrating the query-by-example functionality.
 * @see CoreRegistry
 * @see ServiceRegistry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class CoreRegistryTests {
    static final String TEST_ROOT = "IF/registry/src/test/resources/service-description-registry/";
    private static FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
    static final URI PRONOM_TIFF = formatRegistry.createPronomUri("fmt/10");
    static final URI EXT_TIFF = formatRegistry.createExtensionUri("tiff");
    static final URI MIME_TIFF = formatRegistry.createMimeUri("image/tiff");
    static final URI PRONOM_PNG = formatRegistry.createPronomUri("fmt/11");

    static final String DESCRIPTION = "description";
    static final String TYPE1 = "type1";
    static final String TYPE2 = "type2";
    static final String NAME = "name";
    private URL endpoint1;
    private URL endpoint2;
    private URL endpoint3;
    private URL endpoint4;
    static ServiceRegistry registry;

    /**
     * Create a registry.
     */
    @BeforeClass
    public static void registryCreation() {
        registry = CoreRegistry.getInstance();
    }

    ServiceDescription description1 = null;
    ServiceDescription description2 = null;

    /**
     * Test the registration of services.
     */
    @Before
    public void registerSampleServices() {
        try {
            endpoint1 = new URL("http://some.dummy.endpoint");
            endpoint2 = new URL("http://another.dummy.endpoint");
            endpoint3 = new URL("http://third.dummy.endpoint");
            endpoint4 = new URL("http://fourth.dummy.endpoint");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        registry.clear();
        /* We register one service using the TIFF pronom ID as input format: */
        description1 = new ServiceDescription.Builder(NAME, TYPE1).description(DESCRIPTION).inputFormats(PRONOM_TIFF,
                PRONOM_PNG).endpoint(endpoint1).build();
        /* We register another one using the TIFF extension ID as input format: */
        description2 = new ServiceDescription.Builder(NAME, TYPE2).inputFormats(EXT_TIFF, PRONOM_PNG).endpoint(
                endpoint2).build();
        Response register = registry.register(description1);
        if (!register.success()) {
            System.err.println(register.getMessage());
        }
        /* Register another description, which is restricted, and will not be returned unless authenticated via UI: */
        register = registry.register(new ServiceDescription.Builder(NAME, TYPE1).description(DESCRIPTION).inputFormats(
                PRONOM_TIFF, PRONOM_PNG).endpoint(endpoint4).properties(Property.authorizedRoles("admin, provider"))
                .build());
        if (!register.success()) {
            System.err.println(register.getMessage());
        }
        Assert.assertTrue("Could not register when it should work", register.success());
        /* But we can't register descriptions without an endpoint: */
        Response fail = registry.register(new ServiceDescription.Builder(NAME, TYPE1).build());
        Assert.assertFalse("Could register when it should not work", fail.success());
        registry.register(description2);
        /* We finally register one service using the mime URI: */
        Assert.assertTrue("Failed to register using mime type", registry.register(
                new ServiceDescription.Builder("third", TYPE2).inputFormats(MIME_TIFF).endpoint(endpoint3).build())
                .success());
    }

    /**
     * Clear up the registry.
     */
    @After
    public void cleanupRegistry() {
        registry.clear();
    }

    /**
     * Register a single service description.
     */
    @Test
    public void registerServiceDescription() {
        Response message = registry.register(description1);
        Assert.assertEquals("Double registration!", 3, registry.query(null).size());
        Assert.assertNotNull("No result message;", message);
        System.out.println("Registered: " + message);
    }

    /**
     * Test query by service name.
     */
    @Test
    public void findByName() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(NAME, null).build());
        Assert.assertEquals(2, services.size());
    }

    /**
     * Test query by service type.
     */
    @Test
    public void findByType() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, TYPE1).build());
        compare(services);
    }

    /**
     * Test query by description.
     */
    @Test
    public void findByDescription() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).description(
                DESCRIPTION).build());
        compare(services);
    }

    /**
     * Test query by name and description.
     */
    @Test
    public void findByNameAndDescription() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(NAME, null).description(
                DESCRIPTION).build());
        /*
         * This should only retrieve the first description, with matching name and description:
         */
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    /**
     * Test query by input format.
     */
    @Test
    public void findByInputFormat() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).inputFormats(
                PRONOM_PNG).build());
        /*
         * This should retrieve both descriptions, as both of them support the specified input format (among others):
         */
        Assert.assertEquals(2, services.size());
    }

    /**
     * Test query by input format, mapping from extension to pronom ID.
     */
    @Test
    public void findByInputFormatMappingExtension() {
        /* We use an extension URI for querying: */
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).inputFormats(
                EXT_TIFF).build());
        /*
         * This should retrieve all three descriptions, even if we query using the extension and the services had been
         * registered using the pronom ID or mime type:
         */
        Assert.assertEquals(3, services.size());
    }

    /**
     * Test query by input format, mapping from pronom ID to extension.
     */
    @Test
    public void findByInputFormatMappingPronom() {
        /* We use the pronom URI for querying: */
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).inputFormats(
                PRONOM_TIFF).build());
        /*
         * This should retrieve all three descriptions, even if we query using the pronom ID and the services had been
         * registered using the extension or mime type:
         */
        Assert.assertEquals(3, services.size());
    }

    /**
     * Test query by input format, mapping from pronom ID to extension.
     */
    @Test
    public void findByInputFormatMappingMime() {
        /* We use the mime type for querying: */
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).inputFormats(
                MIME_TIFF).build());
        /*
         * This should retrieve all three descriptions, even if we query using the mime type and the services had been
         * registered using the extension or pronom ID:
         */
        Assert.assertEquals(3, services.size());
    }

    /**
     * Test query by input format and service type.
     */
    @Test
    public void findByInputFormatAndType() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, TYPE1).inputFormats(
                PRONOM_TIFF).build());
        /*
         * While this should only retrieve the first description, as the type is only corresponding to the first
         * description:
         */
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    /**
     * Query by service endpoint.
     */
    @Test
    public void findByEndpoint() {
        List<ServiceDescription> services = registry.query(new ServiceDescription.Builder(null, null).endpoint(
                endpoint1).build());
        Assert.assertEquals(1, services.size());
        compare(services);
    }

    /**
     * Test queries using wildcard matching mode.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @Test
    public void findUsingWildcard() throws MalformedURLException, URISyntaxException {
        /* Using a wildcard matches any number of characters: */
        List<ServiceDescription> services = registry.queryWithMode(new ServiceDescription.Builder(NAME, null).endpoint(
        /* any endpoint containing "dummy": */
        new URL("http://*dummy*")).build(), MatchingMode.WILDCARD);
        Assert.assertEquals(2, services.size());
        services = registry.queryWithMode(
        /* any type ending in "1": */
        new ServiceDescription.Builder(NAME, "*1").build(), MatchingMode.WILDCARD);
        Assert.assertEquals(1, services.size());
    }

    /**
     * Test queries using regular expression matching mode.
     */
    @Test
    public void findUsingRegex() {
        List<ServiceDescription> services = registry.queryWithMode(new ServiceDescription.Builder(NAME, "type[0-9]")
                .build(), MatchingMode.REGEX);
        Assert.assertEquals(2, services.size());
    }

    /**
     * Test service deletion.
     */
    @Test
    public void deleteByExample() {
        Response response = registry.delete(new ServiceDescription.Builder(null, TYPE1).endpoint(endpoint1).build());
        Assert.assertTrue(response.success());
        List<ServiceDescription> services = registry.query(null);
        Assert.assertEquals(2, services.size());
    }

    /**
     * We don't allow duplicate endpoints so check.
     */
    @Test
    public void duplicateEndpointGuard() {
        Response response = registry.register(new ServiceDescription.Builder(null, TYPE1).endpoint(endpoint1).build());
        Assert.assertFalse(response.success());
        List<ServiceDescription> services = registry.query(null);
        Assert.assertEquals(3, services.size());
    }

    /**
     * @param services The retrieved services to be compared to the testing data
     */
    private void compare(final List<ServiceDescription> services) {
        Assert.assertTrue("No services found!", services.size() > 0);
        Assert.assertEquals(description1.getName(), services.get(0).getName());
        Assert.assertEquals(description1.getDescription(), services.get(0).getDescription());
        Assert.assertEquals(description1.getType(), services.get(0).getType());
    }
}
