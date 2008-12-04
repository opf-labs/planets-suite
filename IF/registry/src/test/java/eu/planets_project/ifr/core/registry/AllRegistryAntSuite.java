package eu.planets_project.ifr.core.registry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.registry.api.CoreRegistryTests;
import eu.planets_project.ifr.core.registry.api.PersistentRegistryTests;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceTaxonomyTests;

/**
 * Stripped suite to run all tests in the registry component via ant. (As the
 * JAXR-related tests currently cause trouble when running via Ant).
 * <p/>
 * TODO: fix the probably classpath-related problem causing this.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( {
/* JAXR registry tests: */
ServiceTaxonomyTests.class,
/* Service description registry tests: */
CoreRegistryTests.class, PersistentRegistryTests.class,
/* And the sample usage: */
RegistrySampleUsage.class })
public class AllRegistryAntSuite {}
