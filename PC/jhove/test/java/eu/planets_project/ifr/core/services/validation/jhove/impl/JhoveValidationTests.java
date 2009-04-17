package eu.planets_project.ifr.core.services.validation.jhove.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * Tests of the JHOVE validation functionality.
 * @author Fabian Steeg
 */
public class JhoveValidationTests {
    static Validate jhove;

    /**
     * Tests JHOVE validation using a local JhoveValidation instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        jhove = new JhoveValidation();
    }

    /**
     * test the describe() method
     */
    @Test
    public void testServiceDescription() {
        ServiceDescription description = new JhoveValidation().describe();
        Assert
                .assertTrue(
                        "We have less supported pronom IDs than supported file types",
                        description.getInputFormats().size() >= FileType
                                .values().length);
        System.out.println(description.toXmlFormatted());
    }

    /**
     * Test AIFF validation
     */
    @Test
    public void testAiff() {
        test(FileType.AIFF);
    }

    /**
     * Test ASCII validation
     */
    @Test
    public void testAscii() {
        test(FileType.ASCII);
    }

    /**
     * Test GIF validation
     */
    @Test
    public void testGif() {
        test(FileType.GIF);
    }

    /**
     * Test HTML validation
     */
    @Test
    public void testHtml() {
        test(FileType.HTML);
    }

    /**
     * Test JPEG validation
     */
    @Test
    public void testJpeg1() {
        test(FileType.JPEG1);
    }

    /**
     * Test JPEG validation again
     */
    @Test
    public void testJpeg2() {
        test(FileType.JPEG2);
    }

    /**
     * Test JPEG validation yet again
     */
    // @Test TODO: There is something wrong with that JPEG file
    public void testJpeg3() {
        test(FileType.JPEG3);
    }

    /**
     * Test PDF validation
     */
    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    /**
     * Test TIFF validation
     */
    @Test
    public void testTiff() {
        test(FileType.TIFF);
    }

    /**
     * Test WAV validation
     */
    @Test
    public void testWave() {
        test(FileType.WAVE);
    }

    /**
     * test XML validation
     */
    @Test
    public void testXml() {
        test(FileType.XML);
    }

    /**
     * The old approach: iterate over the enum types...
     */
    @SuppressWarnings("unused")
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
            ValidateResult vr = jhove.validate(
                    new DigitalObject.Builder(ImmutableContent.byReference(new File(
                            location).toURI().toURL())).build(),
                    new URI(type.getPronom()), null );
            result = vr.isOfThisFormat() && vr.isValidInRegardToThisFormat();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertTrue("Not validated: " + type, result);
    }

}
