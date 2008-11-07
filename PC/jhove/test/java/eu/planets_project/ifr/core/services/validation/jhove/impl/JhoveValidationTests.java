package eu.planets_project.ifr.core.services.validation.jhove.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.validate.BasicValidateOneBinary;

/**
 * Tests of the JHOVE validation functionality.
 * @author Fabian Steeg
 */
public class JhoveValidationTests {
    protected static BasicValidateOneBinary jhove;

    /**
     * Tests JHOVE validation using a local JhoveValidation instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        jhove = new JhoveValidation();
    }

    @Test
    public void testAiff() {
        test(FileType.AIFF);
    }

    @Test
    public void testAscii() {
        test(FileType.ASCII);
    }

    @Test
    public void testGif() {
        test(FileType.GIF);
    }

    @Test
    public void testHtml() {
        test(FileType.HTML);
    }

    @Test
    public void testJpeg() {
        test(FileType.JPEG);
    }

    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    @Test
    public void testTiff() {
        test(FileType.TIFF);
    }

    @Test
    public void testWave() {
        test(FileType.WAVE);
    }

    @Test
    public void testXml() {
        test(FileType.XML);
    }

    /**
     * The old approach: iterate over the enum types...
     */
    private void test() {
        /* We check all the enumerated file types: */
        for (FileType type : FileType.values()) {
            test(type);
        }
    }

    /**
     * @param type The enum type to test
     */
    private void test(FileType type) {
        System.out.println("Testing validation of: " + type);
        /* For each we get the sample file: */
        String location = type.getSample();
        /* And try validating it: */
        boolean result = false;
        try {
            result = jhove.basicValidateOneBinary(ByteArrayHelper
                    .read(new File(location)), new URI(type.getPronom()));
        } catch (PlanetsException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertTrue("Not validated: " + type, result);
    }

}
