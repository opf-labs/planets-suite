package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;

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
        System.setProperty("proxySet","true");
        System.setProperty("http.proxyHost","bspcache.bl.uk");
        System.setProperty("http.proxyPort","8080");
        System.setProperty("http.nonProxyHosts","localhost|127.0.0.1|*.ad.bl.uk");
        System.out.println("Set.");
        
        droid = ServiceCreator.createTestService(Identify.QNAME,
                Droid.class, "/pserv-pc-droid/Droid?wsdl");
    }

    /**
     * test rich text format id
     */
    @Test
    public void testRTF() {
        test(TestFile.RTF);
    }

    /**
     * test xml id
     */
    @Test
    public void testXML() {
        test(TestFile.XML);
    }

    /**
     * test zip id
     */
    @Test
    public void testZIP() {
        test(TestFile.ZIP);
    }

    /**
     * Enum containing files to test the Droid identification with. Each entry
     * contains the file location and the expected results. In the tests, we
     * iterate over all files, identify the file at the location and compare the
     * received results with the expected ones
     */
    private enum TestFile {
        /**
         * Rich Text Format.
         */
        RTF(Droid.LOCAL + "Licence.rtf", "info:pronom/fmt/50", "info:pronom/fmt/51"),
        /**
         * Extensible Mark-up Language.
         */
        XML(Droid.LOCAL + "DROID_SignatureFile_Planets.xml", "info:pronom/fmt/101"),
        /**
         * ZIP archive files.
         */
        ZIP(Droid.LOCAL + "Licence.zip", "info:pronom/x-fmt/263");
        /***/
        private String location;
        /***/
        private List<String> expected;

        /**
         * @param location The sample file location
         * @param expected The expected pronom URI
         */
        private TestFile(final String location, final String... expected) {
            this.location = location;
            this.expected = new ArrayList<String>(Arrays.asList(expected));
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
        String[] identify = null;
        try {
            identify = test(droid, f.location);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (identify != null) {
            for (int i = 0; i < identify.length; i++) {
                Assert.assertTrue("Identification failed for " + f.location,
                        f.expected.contains(identify[i]));
            }
        }
    }

    /**
     * @param identify The Identify instance to test
     * @param location The location of the file to test with the instance
     * @return Returns the resulting URI as ASCII strings
     * @throws MalformedURLException
     */
	private static String[] test(final Identify identify, final String location)
            throws MalformedURLException {
        IdentifyResult result = identify.identify(new DigitalObject.Builder(
                Content.byValue(new File(location) )).build(), null );
        String[] strings = new String[result.getTypes().size()];
        for (int i = 0; i < result.getTypes().size(); i++) {
            String string = result.getTypes().get(i).toASCIIString();
            System.out.println(string);
            strings[i] = string;
        }
        return strings;
    }
}
