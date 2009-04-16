package eu.planets_project.ifr.core.services.characterisation.extractor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.Extractor2BinaryTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.ExtractorPropertiesListerTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacteriseTests;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrateTests;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccessTests;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreatorTests;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlPropertiesTests;

/**
 * Suite to run all tests in the extractor component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { Extractor2BinaryTest.class, XcdlAccessTests.class,
        XcdlPropertiesTests.class, XcdlCharacteriseTests.class,
        ExtractorPropertiesListerTest.class, XcdlMigrateTests.class,
        XcdlCreatorTests.class })
public class AllExtractorSuite {}
