package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.metadata.impl.MetadataExtractorTests;
import eu.planets_project.ifr.core.services.characterisation.metadata.impl.RemoteMetadataExtractorTests;

/**
 * Suite to run all tests in the metadata component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { MetadataExtractorTests.class,
        RemoteMetadataExtractorTests.class })
public class AllMetadataSuite {}
