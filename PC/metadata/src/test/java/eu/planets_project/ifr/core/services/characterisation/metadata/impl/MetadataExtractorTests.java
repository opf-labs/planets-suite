package eu.planets_project.ifr.core.services.characterisation.metadata.impl;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Tests of the metadata extractor functionality.
 * @author Fabian Steeg
 */
public class MetadataExtractorTests {

    private static final FormatRegistry FORMAT_REGISTRY = FormatRegistryFactory
            .getFormatRegistry();
    private static final String HOME = "PC/metadata/";
    /***/
    protected static final String SAMPLES = HOME + "src/resources/samples/";
    protected static Characterise characterizer;

    /**
     * Tests MetadataExtractor characterization using a local MetadataExtractor
     * instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        characterizer = new MetadataExtractor();
    }

    @Test
    public void testServiceDescription() {
        ServiceDescription description = characterizer.describe();
        /*
         * We expect to have at least as many input formats (PRONOM) as there
         * are supported file types (extensions):
         */
        int size = description.getInputFormats().size();
        Assert.assertTrue(
                "Service description contains less input formats than it should: "
                        + size, size >= MetadataType.values().length);
    }

    /**
     * Test BMP metadata extraction.
     */
    @Test
    public void testBMPExtraction() {
        test(MetadataType.BMP);
    }

    /**
     * Test GIF metadata extraction.
     */
    @Test
    public void testGIFExtraction() {
        test(MetadataType.GIF);
    }

    /**
     * Test JPEG metadata extraction.
     */
    @Test
    public void testJPEGExtraction() {
        test(MetadataType.JPEG);
    }

    /**
     * Test TIFF metadata extraction.
     */
    @Test
    public void testTIFFExtraction() {
        test(MetadataType.TIFF);
    }

    /**
     * Test PDF metadata extraction.
     */
    @Test
    public void testPDFExtraction() {
        test(MetadataType.PDF);
    }

    /**
     * Test WAV metadata extraction.
     */
    @Test
    public void testWAVExtraction() {
        test(MetadataType.WAV);
    }

    /**
     * Test HTML metadata extraction.
     */
    @Test
    public void testHTMLExtraction() {
        test(MetadataType.HTML);
    }

    /**
     * Test Open Office file metadata extraction.
     */
    @Test
    public void testOpenOfficeExtraction() {
        test(MetadataType.OPEN_OFFICE1);
    }

    /**
     * Test Word Perfect file metadata extraction.
     */
    @Test
    public void testWordPerfectExtraction() {
        test(MetadataType.WORD_PERFECT);
    }

    /**
     * Test Word 6 file metadata extraction.
     */
    @Test
    public void testWORD6Extraction() {
        test(MetadataType.WORD6);
    }

    /**
     * Test MS Workd format metadata extraction.
     */
    @Test
    public void testWORKSExtraction() {
        test(MetadataType.WORKS);
    }

    /**
     * Test excel file metadata extraction.
     */
    @Test
    public void testEXCELExtraction() {
        test(MetadataType.EXCEL);
    }

    /**
     * Test powerpoint file metadata extraction.
     */
    @Test
    public void testPowerPointExtraction() {
        test(MetadataType.POWER_POINT);
    }

    /**
     * Test MP3 file metadata extraction.
     */
    @Test
    public void testMP3Extraction() {
        test(MetadataType.MP3);
    }

    /**
     * Test XML file metadata extraction.
     */
    @Test
    public void testXMLExtraction() {
        test(MetadataType.XML);
    }

    /**
     * @param type The enum type to test
     */
    private void test(final MetadataType type) {
        testPropertyListing(type);
        testCharacterization(type);
    }

    /**
     * @param type The type to test characterization for
     */
    private void testCharacterization(final MetadataType type) {
        System.out.println("Testing characterisation of " + type);
        File file = new File(SAMPLES + type.sample);
        byte[] binary = ByteArrayHelper.read(file);
        if (binary.length == 0) {
            throw new IllegalStateException("Empty file: " + file);
        }
        DigitalObject digitalObject = new DigitalObject.Builder(Content
                .byValue(binary)).build();
        CharacteriseResult characteriseResult = characterizer.characterise(
                digitalObject, null);
        List<Property> properties = characteriseResult.getProperties();
        System.out.println("Characterised " + file.getAbsolutePath() + " as: "
                + properties);
        Assert.assertTrue("Result does not contain the correct mime type: "
                + type.mime + " in result: " + properties, properties
                .contains(new Property(MetadataExtractor.makePropertyURI("TYPE"), "TYPE", type.mime)));
    }

    /**
     * @param type he type to test property listing for
     */
    private void testPropertyListing(final MetadataType type) {
        System.out.println("Testing adapter access for " + type);
        List<String> props = MetadataExtractor.listProperties(type);
        Assert.assertTrue("No props read for adapter: " + type.adapter, props
                .size() > 0);
        System.out.println("Testing properties listing for " + type);
        URI puidToUri = FORMAT_REGISTRY.puidToUri(type.samplePuid);
        System.out.println("URI: " + puidToUri);
        List<FileFormatProperty> listProperties = characterizer
                .listProperties(puidToUri);
        Assert.assertTrue("No props listed for PUID: " + type.samplePuid,
                listProperties.size() > 0);
        System.out.println("Found " + listProperties.size() + " properties");
    }

    /**
     * Old approach: iterate over all enum types...
     */
    @SuppressWarnings("unused")
    private void test() {
        /* Test all file types supported by the tool */
        for (MetadataType type : MetadataType.values()) {
            test(type);
        }
    }
}
