package eu.planets_project.ifr.core.registry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.registry.api.FactoryLocalTests;
import eu.planets_project.ifr.core.registry.api.FactoryRemoteTests;
import eu.planets_project.ifr.core.registry.api.PersistentRegistryTests;
import eu.planets_project.ifr.core.registry.api.CoreRegistryTests;
import eu.planets_project.ifr.core.registry.api.RegistryWebserviceTests;
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
        ServiceRegistryWebServiceTests.class,
        /* Service description registry tests: */
        CoreRegistryTests.class, PersistentRegistryTests.class,
        RegistryWebserviceTests.class, FactoryLocalTests.class,
        FactoryRemoteTests.class,
        /* And the sample usage: */
        RegistrySampleUsage.class })
public class AllRegistrySuite {}
