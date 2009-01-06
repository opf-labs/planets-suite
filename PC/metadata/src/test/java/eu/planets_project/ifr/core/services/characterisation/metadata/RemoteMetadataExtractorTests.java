package eu.planets_project.ifr.core.services.characterisation.metadata;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.services.characterisation.metadata.impl.MetadataExtractor;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Client tests of the metadata extractor functionality.
 * @author Fabian Steeg
 */
public final class RemoteMetadataExtractorTests extends MetadataExtractorTests {

    /**
     * Tests MetadataExtractor characterization using a MetadataExtractor
     * service instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Remote:");
        characterizer = ServiceCreator.createTestService(
                Characterise.QNAME, MetadataExtractor.class,
                "/pserv-pc-metadata/MetadataExtractor?wsdl");
    }
}
