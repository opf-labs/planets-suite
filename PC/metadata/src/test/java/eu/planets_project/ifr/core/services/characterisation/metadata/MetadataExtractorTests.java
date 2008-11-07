package eu.planets_project.ifr.core.services.characterisation.metadata;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.metadata.impl.MetadataExtractor;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinary;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the metadata extractor functionality.
 * @author Fabian Steeg
 */
public final class MetadataExtractorTests {

    /***/
    public static final String SAMPLES = "PC/metadata/src/resources/samples/";

    /**
     * Tests MetadataExtractor characterization using a local MetadataExtractor
     * instance.
     */
    @Test
    public void localTests() {
        System.out.println("Local:");
        test(new MetadataExtractor());
    }

    /**
     * Tests MetadataExtractor identification using a MetadataExtractor instance
     * retrieved via the web service (running on localhost).
     */
    @Test
    public void clientTests() {
        System.out.println("Remote:");
        BasicCharacteriseOneBinary characterise = ServiceCreator
                .createTestService(BasicCharacteriseOneBinary.QNAME,
                        MetadataExtractor.class,
                        "/pserv-pc-metadata/MetadataExtractor?wsdl");
        test(characterise);
    }

    /**
     * Test a BasicValidateOneBinary instance.
     * @param characterise The BasicCharacteriseOneBinary instance to test
     */
    private void test(final BasicCharacteriseOneBinary characterise) {
        /* Test all file types supported by the tool */
        for (MetadataType type : MetadataType.values()) {
            System.out.println("Testing characterisation of " + type);
            File file = new File(SAMPLES + type.location);
            byte[] binary = ByteArrayHelper.read(file);
            String result = null;
            try {
                result = characterise.basicCharacteriseOneBinary(binary);
            } catch (PlanetsException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Characterised " + file.getAbsolutePath()
                    + " as: " + result);
            assertTrue("Result does not contain the correct mime type: "
                    + type.mime, result.toLowerCase().contains(type.mime));
        }
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
        WORD_PERFECT("wordperfect/sample.wpd", "file/unknown"),
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
}
