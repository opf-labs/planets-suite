package eu.planets_project.ifr.core.registry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryManagerTests;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryTests;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryWebServiceTests;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceTaxonomyTests;

/**
 * Suite to run all tests in the registry component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( {
/* JAXR registry tests: */
ServiceTaxonomyTests.class, ServiceRegistryTests.class,
        ServiceRegistryManagerTests.class,
        ServiceRegistryWebServiceTests.class
        })
public class AllJaxrSuite {}
