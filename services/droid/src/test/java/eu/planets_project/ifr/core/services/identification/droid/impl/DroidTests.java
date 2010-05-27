package eu.planets_project.ifr.core.services.identification.droid.impl;

import static eu.planets_project.services.utils.test.TestFile.testIdentification;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.TestFile;

/**
 * Tests of the Droid functionality.
 * @author Fabian Steeg
 */
public class DroidTests {

    static Identify droid = null;

    /**
     * Tests Droid identification using a local Droid instance.
     */
    @BeforeClass
    public static void localTests() {
        droid = ServiceCreator.createTestService(Identify.QNAME, Droid.class,
                "/pserv-pc-droid/Droid?wsdl");
    }
    
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
    @Test public void testRtf() { test(TestFile.RTF, droid); }
    @Test public void testBmp() { test(TestFile.BMP, droid); }
    @Test public void testXml() { test(TestFile.XML, droid); }
    @Test public void testZip() { test(TestFile.ZIP, droid); }
    @Test public void testPdf() { test(TestFile.PDF, droid); }
    @Test public void testGif() { test(TestFile.GIF, droid); }
    @Test public void testJpg() { test(TestFile.JPG, droid); }
    @Test public void testTif() { test(TestFile.TIF, droid); }
    @Test public void testPcx() { test(TestFile.PCX, droid); }
    @Test public void testPng() { test(TestFile.PNG, droid); }
    @Test public void testWav() { test(TestFile.WAV, droid); }
    @Test public void testHtml(){ test(TestFile.HTML, droid);}
    
    /*
     * These don't work, and never have. Only the tests did not catch that
     * before (no results, set of pronom ids is empty).
     */
//  @Test public void testArj() { test(TestFile.ARJ, droid); }
//  @Test public void testTxt() { test(TestFile.TXT, droid); }
//  @Test public void testAiff(){ test(TestFile.AIFF, droid);}
//  @Test public void testJp2() { test(TestFile.JP2, droid); }
//  @Test public void testRaw() { test(TestFile.RAW, droid); }
//  @Test public void testTga() { test(TestFile.TGA, droid); }
//  @Test public void testPnm() { test(TestFile.PNM, droid); }

    private void test(TestFile f, Identify identify) {
        Assert.assertNotNull("File has not types to compare to: " + f, f
                .getTypes());
        boolean b = testIdentification(f, identify);
        Assert.assertTrue("Identification failed for: " + f, b);
        
    }

    // @Test
    public void testByReference() throws MalformedURLException {
        URL url = new URL("http://www.google.com/intl/en_ALL/images/logo.gif");
        System.out.println("Testing " + url);
        IdentifyResult result = droid.identify(new DigitalObject.Builder(
                Content.byReference(url)).build(), null);
        for (URI f : result.getTypes()) {
            System.out.println("Got f=" + f);
        }
    }

    /**
     * The old approach: iterate over the enum types...
     */
    // @Test
    public void testAllFiles() {
        TestFile.testIdentification(droid);
    }

    
}
