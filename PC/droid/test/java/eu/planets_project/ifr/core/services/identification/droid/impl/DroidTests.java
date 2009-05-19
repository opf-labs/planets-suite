package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.FileAccess;

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
    
    /**
     * Enum containing files to test the Droid identification with. Each entry
     * contains the file location and the expected results. In the tests, we
     * iterate over all files, identify the file at the location and compare the
     * received results with the expected ones
     */
    private enum TestFile {
        /*
         * CAUTION: adding an extension here without adding a corresponding file
         * to the test files folder of PSERV will cause all these tests to fail!
         */
        BMP, RTF, XML, ZIP, PDF, GIF, JPG, TIF, PCX, JP2, RAW, TGA, PNM, PNG, ARJ;
        /** We retrieve test files and correct PRONOM IDs automatically. */
        private String location = FileAccess.INSTANCE.get(toString())
                .getAbsolutePath();
        private Set<URI> expected = FormatRegistryFactory.getFormatRegistry()
                .getUrisForExtension(toString());
    }
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
    @Test public void testRtf() { test(TestFile.RTF); }
    @Test public void testBmp() { test(TestFile.BMP); }
    @Test public void testXml() { test(TestFile.XML); }
    @Test public void testZip() { test(TestFile.ZIP); }
    @Test public void testPdf() { test(TestFile.PDF); }
    @Test public void testGif() { test(TestFile.GIF); }
    @Test public void testJpg() { test(TestFile.JPG); }
    @Test public void testTif() { test(TestFile.TIF); }
    @Test public void testPcx() { test(TestFile.PCX); }
    @Test public void testJp2() { test(TestFile.JP2); }
    @Test public void testRaw() { test(TestFile.RAW); }
    @Test public void testTga() { test(TestFile.TGA); }
    @Test public void testPnm() { test(TestFile.PNM); }
    @Test public void testPng() { test(TestFile.PNG); }
    @Test public void testArj() { test(TestFile.ARJ); }

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
    public static void testAllFiles() {
        for (TestFile f : TestFile.values()) {
            test(f);
        }
    }

    /**
     * @param f The enum type to test
     */
    private static void test(TestFile f) {
        System.out.println("Testing " + f);
        List<URI> identify = droid
                .identify(
                        new DigitalObject.Builder(Content.byReference(new File(
                                f.location))).build(), null).getTypes();
        if (identify != null) {
            for (URI uri : identify) {
                String message = String
                        .format(
                                "Identification failed for %s, expected one of %s but was %s ",
                                f.location, f.expected, uri);
                Assert.assertTrue(message, f.expected.contains(uri));
            }
        }
    }
}
