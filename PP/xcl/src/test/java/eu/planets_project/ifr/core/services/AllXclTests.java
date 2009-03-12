package eu.planets_project.ifr.core.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Main test suite to run all XCL suites locally, against a simulated standalone
 * server and against a running server.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { XclLocalTests.class, XclStandaloneTests.class,
        XclServerTests.class })
public class AllXclTests {}
