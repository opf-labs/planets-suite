package eu.planets_project.ifr.core.services.comparison.fpm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.comparison.fpm.impl.FpmCommonPropertiesTests;

/**
 * Suite to run all tests in the comparator component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { FpmCommonPropertiesTests.class })
public class AllFpmSuite {}
