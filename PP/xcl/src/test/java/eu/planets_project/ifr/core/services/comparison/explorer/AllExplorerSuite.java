package eu.planets_project.ifr.core.services.comparison.explorer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.comparison.explorer.impl.XcdlCommonPropertiesTests;

/**
 * Suite to run all tests in the explorer component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { XcdlCommonPropertiesTests.class })
public class AllExplorerSuite {}
