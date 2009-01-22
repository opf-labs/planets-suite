package eu.planets_project.ifr.core.services.comparison;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.comparison.comparator.impl.ComparatorServiceTests;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.ComparatorWrapperTests;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.ResultPropertiesReaderTests;

/**
 * Suite to run all tests in the comparator component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ComparatorWrapperTests.class,
        ComparatorServiceTests.class, ResultPropertiesReaderTests.class })
public class AllComparatorSuite {}
