package eu.planets_project.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.services.datatypes.CharacteriseResultTests;
import eu.planets_project.services.datatypes.ChecksumTests;
import eu.planets_project.services.datatypes.ContentTests;
import eu.planets_project.services.datatypes.DigitalObjectTests;
import eu.planets_project.services.datatypes.MetadataTests;
import eu.planets_project.services.datatypes.PropertyTests;
import eu.planets_project.services.datatypes.ServiceDescriptionTest;
import eu.planets_project.services.datatypes.ServiceReportTests;
import eu.planets_project.services.utils.FileUtilsZipTest;
import eu.planets_project.services.utils.test.FileAccessTests;

/**
 * Suite to run all tests in the common component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ChecksumTests.class, DigitalObjectTests.class,
        MetadataTests.class, ContentTests.class, ServiceDescriptionTest.class,
        ServiceReportTests.class, PropertyTests.class, PropertyTests.class,
        FileUtilsZipTest.class, FileAccessTests.class, CharacteriseResultTests.class })
public class AllCommonSuite {}
