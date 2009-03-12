package eu.planets_project.ifr.core.services;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.AllExtractorSuite;
import eu.planets_project.ifr.core.services.comparison.comparator.AllComparatorSuite;
import eu.planets_project.ifr.core.services.comparison.fpm.AllFpmSuite;

/**
 * Main test suite to run all XCL suites against a standalone simulated server.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { AllExtractorSuite.class, AllComparatorSuite.class,
        AllFpmSuite.class })
public class XclStandaloneTests extends AllXclTests {
    @BeforeClass
    public static void setupProperties() {
        System.setProperty("pserv.test.context", "standalone");
    }
}
