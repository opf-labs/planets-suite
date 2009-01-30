package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.Extractor2BinaryTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacteriseLocalTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.ExtractorPropertiesListerTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacteriseServerTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacteriseStandaloneTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrateLocalTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrateServerTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccessTests;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreatorTests;

/**
 * Suite to run all tests in the extractor component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { Extractor2BinaryTest.class, XcdlAccessTests.class,
        XcdlCharacteriseLocalTest.class, XcdlCharacteriseServerTest.class,
        XcdlCharacteriseStandaloneTest.class,
        ExtractorPropertiesListerTest.class, XcdlMigrateLocalTest.class,
        XcdlMigrateServerTest.class, XcdlCreatorTests.class })
public class AllExtractorSuite {}
