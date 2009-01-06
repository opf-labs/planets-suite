package eu.planets_project.ifr.core.services.characterisation.metadata;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.metadata.impl.MetadataExtractor;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;

/**
 * Tests of the metadata extractor functionality.
 * @author Fabian Steeg
 */
public class MetadataExtractorTests {

    /***/
    protected static final String SAMPLES = "PC/metadata/src/resources/samples/";
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
     * Enumeration of sample files and their types (as the tool detects them).
     */
    private enum MetadataType {
        /** Some types work just fine and give a full result. */
        BMP("bmp/test1.bmp", "image/bmp"),
        /***/
        GIF("gif/AA_Banner.gif", "image/gif"),
        /***/
        JPEG("jpeg/AA_Banner.jpg", "image/jpeg"),
        /***/
        TIFF("tiff/AA_Banner.tif", "image/tiff"),
        /***/
        PDF("pdf/AA_Banner-single.pdf", "application/pdf"),
        /***/
        WAV("wav/comet.wav", "wave"),
        /***/
        HTML("html/sample.html", "text/html"),
        /** The OO adapter throws an exception but still works. */
        OPEN_OFFICE1("oo1/planets.sxw", "application/open-office-1.x"),
        /**
         * And some are characterized as unknown although they should be
         * supported (according to http://meta-extractor.sourceforge.net).
         */
        WORD_PERFECT("wordperfect/sample.wpd",
        /* Word perfect identification works only on windows: */
        System.getProperty("os.name").toLowerCase().contains("windows") ? "application/vnd.wordperfect"
                : "file/unknown"),
        /***/
        WORD6("word6/planets.doc", "file/unknown"),
        /***/
        WORKS("works/sample.wps", "file/unknown"),
        /***/
        EXCEL("excel/Travel.xls", "file/unknown"),
        /***/
        POWER_POINT("pp/planets.ppt", "file/unknown"),
        /***/
        MP3("mp3/Arkansas.mp3", "file/unknown"),
        /***/
        XML("xml/sample.xml", "file/unknown");
        /***/
        private String mime;
        /***/
        private String location;

        /**
         * @param location The file location
         * @param type The type, as the tool detects it
         */
        private MetadataType(final String location, final String type) {
            this.location = location;
            this.mime = type;
        }
    }

    /**
     * @param type The enum type to test
     */
    private void test(final MetadataType type) {
        System.out.println("Testing characterisation of " + type);
        File file = new File(SAMPLES + type.location);
        byte[] binary = ByteArrayHelper.read(file);
        if (binary.length == 0) {
            throw new IllegalStateException("Empty file: " + file);
        }
        String result = null;
        DigitalObject digitalObject = new DigitalObject.Builder(Content
                .byValue(binary)).build();
        CharacteriseResult characteriseResult = characterizer.characterise(
                digitalObject, null, null);
        byte[] resultBytes = FileUtils
                .writeInputStreamToBinary(characteriseResult.getDigitalObject()
                        .getContent().read());
        result = new String(resultBytes);
        System.out.println("Characterised " + file.getAbsolutePath() + " as: "
                + result);
        assertTrue("Result does not contain the correct mime type: "
                + type.mime + " in result: " + result, result.toLowerCase()
                .contains(type.mime));
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
