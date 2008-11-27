package eu.planets_project.ifr.core.registry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.registry.api.PersistentServiceDescriptionRegistryTests;
import eu.planets_project.ifr.core.registry.api.CoreServiceDescriptionRegistryTests;
import eu.planets_project.ifr.core.registry.api.ServiceRegistryManagerTests;
import eu.planets_project.ifr.core.registry.api.ServiceRegistryTests;
import eu.planets_project.ifr.core.registry.api.ServiceRegistryWebServiceTests;
import eu.planets_project.ifr.core.registry.api.ServiceTaxonomyTests;

/**
 * Suite to run all tests in the registry component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ServiceTaxonomyTests.class, ServiceRegistryTests.class,
        ServiceRegistryManagerTests.class,
        ServiceRegistryWebServiceTests.class,
        CoreServiceDescriptionRegistryTests.class,
        PersistentServiceDescriptionRegistryTests.class })
public class AllRegistryTests {}
