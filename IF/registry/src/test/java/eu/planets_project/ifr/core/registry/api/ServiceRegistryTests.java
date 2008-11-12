package eu.planets_project.ifr.core.registry.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsRegistryObject;
import eu.planets_project.ifr.core.registry.api.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceList;
import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;

/**
 * Local tests of the service registry. Each test is atomic and represents a use
 * case for the service registry.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceRegistryTests {

    /** Brief registry sample usage. */
    @Test
    public void usage() {
        /* First, we create a registry instance: */
        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
        /* Then create an object factory for the registry: */
        ServiceRegistryObjectFactory factory = new ServiceRegistryObjectFactory(
                USERNAME, PASSWORD, registry);
        /* With that, we create an organization, a service and a binding: */
        PsOrganization organization = factory.createOrganization("Planets",
                "Preservation and Long-Term Access via Networked Services",
                "Planets Info", "info@planets-project.eu");
        PsService service = factory.createService("Droid",
                "Droid Identification Service", organization);
        PsBinding binding = factory.createBinding("Universal Droid Endpoint",
                "http://127.0.0.1:8080/pserv-pc-droid/Droid?wsdl", service);
        /* Now, the registry can be queried for these: */
        List<PsBinding> bindings = registry.findBindings(USERNAME, PASSWORD,
                service.getKey()).bindings;
        assertEquals(binding.getDescription(), bindings.get(0).getDescription());
        /*
         * For further details (e.g. setting classifications etc., see the tests
         * further below)
         */
        /*
         * TODO: The ServiceRegistry Interface should probably only offer write
         * access for ServiceDescription objects, and some flexible query
         * methods (Services for organization, for name, for category, etc.),
         * and the ServiceRegistryObjectFactory should probably be hidden
         * completely, as well as all those PsObjects.
         */
    }

    protected static final String USERNAME = "provider";
    protected static final String PASSWORD = "provider";
    private static final String WILDCARD = "%";
    protected static ServiceRegistry registry;
    protected static ServiceRegistryObjectFactory mock;

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

    private void compareRegistryObjects(PsRegistryObject expected,
            PsRegistryObject actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

}
