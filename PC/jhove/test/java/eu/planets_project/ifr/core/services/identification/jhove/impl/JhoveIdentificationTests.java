package eu.planets_project.ifr.core.services.identification.jhove.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;

/**
 * Tests of the JHOVE identification functionality.
 * @author Fabian Steeg
 */
public class JhoveIdentificationTests {
    static Identify jhove;

    /**
     * Tests JHOVE identification using a local JhoveIdentification instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        jhove = new JhoveIdentification();
    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testServiceDescription() {
        ServiceDescription description = new JhoveIdentification().describe();
        Assert
                .assertTrue(
                        "We have less supported pronom IDs than supported file types",
                        description.getInputFormats().size() >= FileType
                                .values().length);
        System.out.println(description.toXmlFormatted());
    }

    /**
     * Test AIFF identification.
     */
    @Test
    public void testAiff() {
        test(FileType.AIFF);
    }

    /**
     * Test ASCII identification.
     */
    @Test
    public void testAscii() {
        test(FileType.ASCII);
    }

    /**
     * Test GIF identification.
     */
    @Test
    public void testGif() {
        test(FileType.GIF);
    }

    /**
     * Test HTML identifcation.
     */
    @Test
    public void testHtml() {
        test(FileType.HTML);
    }

    /**
     * Test JPEG 1 identification.
     */
    @Test
    public void testJpeg1() {
        test(FileType.JPEG1);
    }

    /**
     * Test JPEG 2 identification.
     */
    @Test
    public void testJpeg2() {
        test(FileType.JPEG2);
    }

    /**
     * Test JPEG identification.
     */
    // @Test TODO: There is something wrong with that JPEG file
    public void testJpeg3() {
        test(FileType.JPEG3);
    }

    /**
     * Test PDF identification.
     */
    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    /**
     * Test TIFF identification.
     */
    @Test
    public void testTiff() {
        test(FileType.TIFF);
    }

    /**
     * Test wav identification.
     */
    @Test
    public void testWave() {
        test(FileType.WAVE);
    }

    /**
     * Test xml identification.
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
    private void test(final FileType type) {
        System.out.println("Testing identification of: " + type);
        /* For each we get the sample file: */
        String location = type.getSample();
        /* And try identifying it: */
        URI result = null;
        try {
            result = jhove.identify(
                    new DigitalObject.Builder(Content.byReference(new File(
                            location).toURI().toURL())).build()).getTypes()
                    .get(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(result);
        assertEquals("Wrong pronom ID;", type.getPronom(), result.toString());
    }

}
