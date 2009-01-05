package eu.planets_project.ifr.core.registry.api.jaxr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsRegistryObject;
import eu.planets_project.services.utils.PlanetsLogger;

class ServiceRegistryTestsHelper {
    /**
     * @param binding The binding to check
     */
    static void checkBinding(final PsBinding binding) {
        checkString(binding.getAccessURI(), "Endpoint");
        checkString(binding.getDescription(), "Description");
    }

    /**
     * @param list The categories to check
     */
    static void checkCategories(final List<PsCategory> list) {
        for (PsCategory cat : list) {
            checkString(cat.id, "Category ID");
            checkString(cat.name, "Category Name");
            checkString(cat.code, "Category Code");
        }
    }

    /**
     * @param organization The organization to test
     */
    static void checkOrganization(final PsOrganization organization) {
        checkString(organization.getContactName(), "contact name");
        checkString(organization.getContactMail(), "contact mail");
        checkCategories(organization.getCategories());
        checkRegistyObject(organization);
    }

    /**
     * @param object The registry object to test
     */
    static void checkRegistyObject(final PsRegistryObject object) {
        checkString(object.getName(), "Name");
        checkString(object.getKey(), "Key");
        checkString(object.getDescription(), "Description");
    }

    /**
     * @param string The string to check
     * @param name The name of the string (for outputting info)
     */
    static void checkString(final String string, final String name) {
        assertNotNull(name + " is null;", string);
        assertTrue(name + " is empty;", string.trim().length() > 0);
        System.out.println("[OK] " + name + ": " + string);
    }

    private static boolean guard() {
        String property = System.getProperty("pserv.test.context");
        boolean testOnServer = property != null && property.equals("server");
        if (!testOnServer) {
            /*
             * All JAXR stuff can only be tested on the server. To include the
             * other things in the registry component in the tests without a
             * server, we guard against that case here and succeed without
             * running the test... the registry component should probably be
             * split into two for such reasons.
             */
            PlanetsLogger
                    .getLogger(ServiceRegistryTestsHelper.class)
                    .warn(
                            "Skipping JAXR registry test (not running against a server)");
            return true;
        }
        return false;
    }
}
