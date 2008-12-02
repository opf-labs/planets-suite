package eu.planets_project.ifr.core.registry.api.jaxr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryObjectFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceRegistryMessage;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsRegistryObject;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceList;

/**
 * Local tests of the service registry. Each test is atomic and represents a use
 * case for the service registry.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceRegistryTests {

    /**
     * Registry sample usage: register services, query by type and input format.
     */
    @Test
    public void sampleUsage() {
        /* First, we create a registry instance: */
        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
        /* Then create an object factory for the registry: */
        ServiceRegistryObjectFactory factory = new ServiceRegistryObjectFactory(
                USERNAME, PASSWORD, registry);
        /* With that, we create an organization: */
        PsOrganization organization = factory.createOrganization("Planets",
                "Preservation and Long-Term Access via Networked Services",
                "Planets Info", "info@planets-project.eu");
        /*
         * Create a registered service with name, description, type,
         * organization and supported input formats (as a string):
         */
        PsService service = factory.createService("Droid",
                "Droid Identification Service", organization, "Identify",
                "PDF", "PNG", "info:pronom/fmt/51");
        /* And another one (for the query tests below): */
        factory.createService("Jhove", "Jhove Characterisation Service",
                organization, "Characterise", "PDF", "PNG");
        /* And create a binding for that service: */
        PsBinding binding = factory.createBinding("Universal Droid Endpoint",
                "http://127.0.0.1:8080/pserv-pc-droid/Droid?wsdl", service);
        /* Now, the registry can be queried for the bindings of a service: */
        List<PsBinding> bindings = registry.findBindings(USERNAME, PASSWORD,
                service.getKey()).bindings;
        assertEquals(binding.getDescription(), bindings.get(0).getDescription());
        /*
         * Or for services of a specified type and supporting a specified input
         * format:
         */
        List<PsService> services = registry.findServicesForInputFormats(
                USERNAME, PASSWORD, "Identify", "info:pronom/fmt/51").services;
        assertEquals(1, services.size());
        /*
         * If the format is supported, but it is the wrong type, we don't want
         * it:
         */
        services = registry.findServicesForInputFormats(USERNAME, PASSWORD,
                "Migrate", "PNG").services;
        assertEquals(0, services.size());
        /* And if a required input format is not supported too: */
        services = registry.findServicesForInputFormats(USERNAME, PASSWORD,
                "Identify", "PNG", "WAV").services;
        assertEquals(0, services.size());
        /*
         * And vice versa (wrong format):
         */
        services = registry.findServicesForInputFormats(USERNAME, PASSWORD,
                "Identify", "WAV").services;
        assertEquals(0, services.size());
        /*
         * But if the format is supported, and we don't care about the type, we
         * want it:
         */
        services = registry.findServicesForInputFormats(USERNAME, PASSWORD,
                null, "PNG", "PDF").services;
        assertEquals(2, services.size());
        /*
         * And vice versa (only type specified):
         */
        services = registry.findServicesForInputFormats(USERNAME, PASSWORD,
                "Identify").services;
        assertEquals(1, services.size());
        /*
         * For further details see the tests below.
         */
    }

    protected static final String USERNAME = "provider";
    protected static final String PASSWORD = "provider";
    private static final String WILDCARD = "%";
    static ServiceRegistry registry;
    static ServiceRegistryObjectFactory mock;

    /** Create a registry and a mock object factory once for all tests. */
    @BeforeClass
    public static void setup() {
        registry = ServiceRegistryFactory.getInstance();
        mock = new ServiceRegistryObjectFactory(USERNAME, PASSWORD, registry);
    }

    /** Before running any test, clear the registry. */
    @Before
    public void before() {
        registry.clear(USERNAME, PASSWORD);
    }

    /** After running any test, clear the registry. */
    @After
    public void after() {
        registry.clear(USERNAME, PASSWORD);
    }

    @Test
    public void testCreateOrganization() {
        assertEquals(0, registry
                .findOrganizations(USERNAME, PASSWORD, WILDCARD).organizations
                .size());
        PsOrganization organization = mock.createOrganization();
        List<PsOrganization> retrieved = registry.findOrganizations(USERNAME,
                PASSWORD, WILDCARD).organizations;
        assertEquals(1, retrieved.size());
    }

    @Test
    public void testFindOrganizations() {
        assertEquals(0, registry
                .findOrganizations(USERNAME, PASSWORD, WILDCARD).organizations
                .size());
        PsOrganization organization = mock.createOrganization();
        List<PsOrganization> retrieved = registry.findOrganizations(USERNAME,
                PASSWORD, WILDCARD).organizations;
        assertEquals(1, retrieved.size());
        PsOrganization retrievedOrganization = retrieved.get(0);
        ServiceRegistryTestsHelper.checkOrganization(retrievedOrganization);
        compareRegistryObjects(organization, retrievedOrganization);
        assertEquals(organization.getContactName(), retrievedOrganization
                .getContactName());
        assertEquals(organization.getContactMail(), retrievedOrganization
                .getContactMail());
    }

    @Test
    public void testDeleteOrganization() {
        assertEquals(0, registry
                .findOrganizations(USERNAME, PASSWORD, WILDCARD).organizations
                .size());
        PsOrganization organization = mock.createOrganization();
        List<PsOrganization> retrieved = registry.findOrganizations(USERNAME,
                PASSWORD, WILDCARD).organizations;
        assertEquals(1, retrieved.size());
        PsOrganization retrievedOrganization = retrieved.get(0);
        ServiceRegistryMessage message = registry.deleteOrganization(USERNAME,
                PASSWORD, retrievedOrganization);
        assertEquals(0, registry
                .findOrganizations(USERNAME, PASSWORD, WILDCARD).organizations
                .size());
    }

    @Test
    public void testCreateService() {
        assertEquals(
                0,
                registry.findServices(USERNAME, PASSWORD, WILDCARD, "").services
                        .size());
        PsService service = mock.createService(mock.createOrganization());
        assertEquals(
                1,
                registry.findServices(USERNAME, PASSWORD, WILDCARD, "").services
                        .size());
    }

    @Test
    public void testFindServices() {
        assertEquals(
                0,
                registry.findServices(USERNAME, PASSWORD, WILDCARD, "").services
                        .size());
        PsService service = mock.createService(mock.createOrganization());
        List<PsService> retrievedServices = registry.findServices(USERNAME,
                PASSWORD, WILDCARD, "").services;
        PsService retrieved = retrievedServices.get(0);
        assertEquals(1, retrievedServices.size());
        compareRegistryObjects(service, retrieved);
    }

    @Test
    public void testDeleteService() {
        PsOrganization organization = mock.createOrganization();
        PsService service = mock.createService(organization);
        // After adding, it should be there:
        assertEquals(
                1,
                registry.findServices(USERNAME, PASSWORD, WILDCARD, "").services
                        .size());
        registry.deleteService(USERNAME, PASSWORD, service);
        // After deletion, it should be gone:
        assertEquals(
                0,
                registry.findServices(USERNAME, PASSWORD, WILDCARD, "").services
                        .size());
    }

    @Test
    public void testSaveBinding() {
        PsService s = mock.createService(mock.createOrganization());
        /* Now we can associate a binding with it: */
        PsBinding binding = mock.createBinding(s);
        List<PsBinding> bindings = registry.findBindings(USERNAME, PASSWORD, s
                .getKey()).bindings;
        assertEquals(1, bindings.size());
    }

    @Test
    public void testFindBindings() {
        PsService s = mock.createService(mock.createOrganization());
        /* Now we can associate a binding with it: */
        PsBinding binding = mock.createBinding(s);
        List<PsBinding> bindings = registry.findBindings(USERNAME, PASSWORD, s
                .getKey()).bindings;
        assertEquals(1, bindings.size());
        for (PsBinding b : bindings) {
            ServiceRegistryTestsHelper.checkBinding(b);
        }
        PsBinding actual = bindings.get(0);
        compareRegistryObjects(binding, actual);
        assertEquals(binding.getAccessURI(), actual.getAccessURI());
        assertEquals(binding.isValidateuri(), actual.isValidateuri());
    }

    @Test
    public void testDeleteBinding() {
        PsService s = mock.createService(mock.createOrganization());
        PsBinding binding = mock.createBinding(s);
        List<PsBinding> bindings = registry.findBindings(USERNAME, PASSWORD, s
                .getKey()).bindings;
        assertEquals(1, bindings.size());
        registry.deleteBinding(USERNAME, PASSWORD, binding);
        assertEquals(0,
                registry.findBindings(USERNAME, PASSWORD, s.getKey()).bindings
                        .size());
    }

    @Test
    public void testGetTaxonomy() {
        PsSchema taxonomy = registry.findTaxonomy(USERNAME, PASSWORD)
                .getPsSchema();
        assertTrue("Could not retrieve taxonomy;", taxonomy != null);
        ServiceRegistryTestsHelper.checkString(taxonomy.schemaDescription,
                "Description");
        ServiceRegistryTestsHelper.checkString(taxonomy.schemaName, "Name");
        List<PsCategory> categories = taxonomy.categories;
        for (PsCategory category : categories) {
            ServiceRegistryTestsHelper.checkString(category.name, "Name");
        }
    }

    @Test
    public void testFreeClassification() {
        PsService service = mock.createService(mock.createOrganization());
        String category = "super thing";
        registry.saveFreeClassification(USERNAME, PASSWORD, service.getKey(),
                category);
        List<PsService> services = registry.findServices(USERNAME, PASSWORD,
                WILDCARD, category).services;
        assertEquals(1, services.size());
        assertEquals(category, services.get(0).getCategories().get(0).code);
    }

    @Test
    public void testPredefinedClassification() {
        String id = registry.findTaxonomy(USERNAME, PASSWORD).getPsSchema().categories
                .get(1).id;
        PsOrganization organization = mock.createOrganization();
        /* Create both a classified and an unclassified service: */
        PsService classifiedService = mock.createService(organization);
        PsService unclassifiedService = mock.createService(organization);
        registry.savePredefinedClassification(USERNAME, PASSWORD,
                classifiedService.getKey(), id);
        /* Retrieve only the classified service: */
        List<PsService> classifiedServices = registry.findServices(USERNAME,
                PASSWORD, WILDCARD, id).services;
        assertTrue(
                "Classified service not found after searching by classification "
                        + id + ";", classifiedServices.size() > 0);
        assertEquals(1, classifiedServices.size());
        PsService retrievedShouldBeClassified = classifiedServices.get(0);
        assertEquals(id, retrievedShouldBeClassified.getCategories().get(0).id);
        /* Retrieve unclassified and classified services: */
        ServiceList allServices = registry.findServices(USERNAME, PASSWORD,
                WILDCARD, "");
        assertEquals("Query for all returned less than all;", 2,
                allServices.services.size());
    }

    @Test
    public void testMultipleClassification() {
        PsSchema schema = registry.findTaxonomy(USERNAME, PASSWORD)
                .getPsSchema();
        String id1 = schema.getId("identify");
        String id2 = schema.getId("migrate");
        String id3 = schema.getId("characterise");

        Assert.assertNotNull(id1);
        Assert.assertNotNull(id2);
        Assert.assertNotNull(id3);

        /* for each category, we register two services: */
        PsOrganization organization = mock.createOrganization();

        PsService s1 = mock.createService(organization);
        PsService s2 = mock.createService(organization);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s1.getKey(),
                id1);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s2.getKey(),
                id1);

        PsService s3 = mock.createService(organization);
        PsService s4 = mock.createService(organization);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s3.getKey(),
                id2);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s4.getKey(),
                id2);

        PsService s5 = mock.createService(organization);
        PsService s6 = mock.createService(organization);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s5.getKey(),
                id3);
        registry.savePredefinedClassification(USERNAME, PASSWORD, s6.getKey(),
                id3);

        ServiceList services1 = registry.findServices(USERNAME, PASSWORD,
                WILDCARD, id1);
        ServiceList services2 = registry.findServices(USERNAME, PASSWORD,
                WILDCARD, id2);
        ServiceList services3 = registry.findServices(USERNAME, PASSWORD,
                WILDCARD, id3);

        Assert.assertEquals(2, services1.services.size());
        Assert.assertEquals(2, services2.services.size());
        Assert.assertEquals(2, services3.services.size());
    }

    private void compareRegistryObjects(PsRegistryObject expected,
            PsRegistryObject actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

}
