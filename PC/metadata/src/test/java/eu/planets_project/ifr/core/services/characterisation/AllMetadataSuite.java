package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.metadata.MetadataExtractorTests;

/**
 * Suite to run all tests in the metadata component.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { MetadataExtractorTests.class })
public class AllMetadataSuite {}
