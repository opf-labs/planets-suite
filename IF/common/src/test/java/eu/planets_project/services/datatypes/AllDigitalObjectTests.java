package eu.planets_project.services.datatypes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite to run all digital object related tests.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ChecksumTests.class, DigitalObjectTests.class,
        MetadataTests.class, ContentTests.class })
public class AllDigitalObjectTests {}
