package eu.planets_project.ifr.core.services.characterisation.extractor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
@Suite.SuiteClasses( { XcdlAccessTests.class,
        XcdlPropertiesTests.class, XcdlCharacteriseTests.class,
        XcdlMigrateTests.class, XcdlCreatorTests.class })
public class AllExtractorSuite {}
