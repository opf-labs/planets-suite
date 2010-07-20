package eu.planets_project.ifr.core.services.identification.jhove.impl;

import static eu.planets_project.services.utils.test.TestFile.testIdentification;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.TestFile;

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
        Assert.assertTrue("We have no supported pronom IDs", description
                .getInputFormats().size() > 0);
        System.out.println(description.toXmlFormatted());
    }
    
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
    @Test public void testXml() { test(TestFile.XML, jhove); }
    @Test public void testPdf() { test(TestFile.PDF, jhove); }
    @Test public void testGif() { test(TestFile.GIF, jhove); }
    @Test public void testJpg() { test(TestFile.JPG, jhove); }
    @Test public void testTif() { test(TestFile.TIF, jhove); }
    @Test public void testWav() { test(TestFile.WAV, jhove); }
    @Test public void testTxt() { test(TestFile.TXT, jhove); }
    @Test public void testHtml(){ test(TestFile.HTML, jhove);}
    @Test public void testAiff(){ test(TestFile.AIFF, jhove);}

    private void test(TestFile f, Identify identify) {
        Assert.assertNotNull("File has not types to compare to: " + f, f
                .getTypes());
        boolean b = testIdentification(f, identify);
        Assert.assertTrue("Identification failed for: " + f, b);
    }

    @Test
    public void testUnsupportedBmp() throws MalformedURLException {
        IdentifyResult identify = jhove.identify(new DigitalObject.Builder(
                Content.byReference(new File(TestFile.BMP.getLocation())
                        .toURI().toURL())).build(), null);
        checkUnidentified(identify);
    }

    @Test
    public void testUnsupportedPng() throws MalformedURLException {
        IdentifyResult identify = jhove.identify(new DigitalObject.Builder(
                Content.byReference(new File(TestFile.PNG.getLocation())
                        .toURI().toURL())).build(), null);
        checkUnidentified(identify);
    }
    
    @Test
    public void testEmptyFile() throws IOException {
        File emptyTempFile = File.createTempFile("jhove-test", "empty-file");
        emptyTempFile.deleteOnExit();
        IdentifyResult identify = jhove.identify(new DigitalObject.Builder(
                Content.byReference(emptyTempFile)).build(), null);
        checkUnidentified(identify);
    }
    
    private void checkUnidentified(final IdentifyResult identify) {
        /*
         * Jhove identifies unknown files as "application/octet-stream" (which Droid categorizes as
         * x-fmt/411, among others).
         */
        URI binaryUri = FormatRegistryFactory.getFormatRegistry().createPronomUri("x-fmt/411");
        Assert.assertTrue(
                "Identified types should contain corresponding type to 'application/octet-stream'",
                identify.getTypes().contains(binaryUri));
        /* More info is available in the report message: */
        System.err.println(identify.getReport().getMessage());
    }

}
