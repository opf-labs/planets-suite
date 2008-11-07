package eu.planets_project.ifr.core.services.identification.droid.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests of the Droid functionality.
 * @author Fabian Steeg
 */
public class DroidTests {

    static IdentifyOneBinary droid;

    /**
     * Tests Droid identification using a local Droid instance.
     */
    @BeforeClass
    public static void localTests() {
        droid = new Droid();
    }

    @Test
    public void testRTF() {
        test(TestFile.RTF);
    }

    @Test
    public void testXML() {
        test(TestFile.XML);
    }

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
        private String[] expected;

        /**
         * @param location The sample file location
         * @param expected The expected pronom URI
         */
        private TestFile(final String location, final String... expected) {
            this.location = location;
            this.expected = expected;
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
        identify = test(droid, f.location);
        if (identify != null) {
            for (int i = 0; i < identify.length; i++) {
                assertEquals("Identification failed for " + f.location,
                        f.expected[i], identify[i]);
            }
        }
    }

    /**
     * @param identify The IdentifyOneBinary instance to test
     * @param location The location of the file to test with the instance
     * @return Returns the resulting URI as ASCII strings
     */
    private static String[] test(final IdentifyOneBinary identify,
            final String location) {
        byte[] array = ByteArrayHelper.read(new File(location));
        URI[] result = identify.identifyOneBinary(array).types;
        String[] strings = new String[result.length];
        for (int i = 0; i < result.length; i++) {
            String string = result[i].toASCIIString();
            System.out.println(string);
            strings[i] = string;
        }
        return strings;
    }
}
