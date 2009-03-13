package eu.planets_project.ifr.core.services.comparison.comparator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.SampleXclUsage;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigCreatorTests;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParserTests;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.ComparatorWrapperTests;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.ResultPropertiesReaderTests;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompareTests;

/**
 * Suite to run all tests in the comparator component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ComparatorWrapperTests.class, XcdlCompareTests.class,
        ResultPropertiesReaderTests.class, ComparatorConfigCreatorTests.class,
        ComparatorConfigParserTests.class, XcdlCompareTests.class,
        SampleXclUsage.class /* Overall sample, but in the end Comparator */})
public class AllComparatorSuite {}
