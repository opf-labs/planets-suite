package eu.planets_project.ifr.core.services.identification.droid.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Helper class for testing the Droid service.
 * 
 * @author Fabian Steeg
 */
public final class DroidUnittestHelper {
    /** Enforce non-instanitability with a private constructor. */
    private DroidUnittestHelper() {
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
        RTF(
                Droid.LOCAL + "Licence.rtf",
                "info:pronom/fmt/50",
                "info:pronom/fmt/51"),
        /**
         * Extensible Mark-up Language.
         */
        XML(
                Droid.LOCAL + "DROID_SignatureFile_Planets.xml",
                "info:pronom/fmt/101"),
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
     * @param droid The Droid instance to test. All the files in the Files enum
     *        are identified using the droid instance and received results are
     *        compared to the expected results defined in the elements of the
     *        Files enum
     */
    public static void testAllFiles(final IdentifyOneBinary droid) {
        for (TestFile f : TestFile.values()) {
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
