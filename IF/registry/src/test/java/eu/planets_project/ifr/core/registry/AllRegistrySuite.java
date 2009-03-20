package eu.planets_project.ifr.core.registry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.registry.api.CoreRegistryTests;
import eu.planets_project.ifr.core.registry.api.PersistentRegistryTests;
import eu.planets_project.ifr.core.registry.api.RegistryFactoryTests;
import eu.planets_project.ifr.core.registry.api.RegistryWebserviceTests;

/**
 * Suite to run all tests in the registry component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( {
/* Service description registry tests: */
CoreRegistryTests.class, PersistentRegistryTests.class,
        RegistryWebserviceTests.class, RegistryFactoryTests.class,
        /* And the sample usage: */
        RegistrySampleUsage.class })
public class AllRegistrySuite {}
