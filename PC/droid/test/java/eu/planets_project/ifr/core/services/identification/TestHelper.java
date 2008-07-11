package eu.planets_project.ifr.core.services.identification;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;
import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;

/**
 * Helper class for testing the Droid service
 * 
 * @author Fabian Steeg
 */
public class TestHelper {
	/**
	 * Enum containing files to test the Droid identification with. Each entry
	 * contains the file location and the expected results. In the tests, we
	 * iterate over all files, identify the file at the location and compare the
	 * received results with the expected ones
	 */
	public enum TestFile {
		/**
		 * Rich Text Format
		 */
		RTF(Droid.LOCAL + "Licence.rtf", "info:pronom/fmt/50",
				"info:pronom/fmt/51"),
		/**
		 * Extensible Mark-up Language
		 */
		XML(Droid.LOCAL + "DROID_SignatureFile_Planets.xml",
				"info:pronom/fmt/101"),
		/**
		 * ZIP archive files
		 */
		ZIP(Droid.LOCAL + "Licence.zip", "info:pronom/x-fmt/263");
		String location;
		String[] expected;

		TestFile(String location, String... expected) {
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
	public static void testAllFiles(IdentifyOneBinary droid)
			throws FileNotFoundException, IOException, Exception {
		for (TestFile f : TestFile.values()) {
			String[] identify = test(droid, f.location);
			for (int i = 0; i < identify.length; i++) {
				assertEquals("Identification failed for " + f.location,
						f.expected[i], identify[i]);
			}
		}
	}

	private static String[] test(IdentifyOneBinary droid, String location)
			throws FileNotFoundException, IOException, Exception {
		byte[] array = ByteArrayHelper.read(new File(location));
		URI[] identify = droid.identifyOneBinary(array).types;
		String[] strings = new String[identify.length];
		for (int i = 0; i < identify.length; i++) {
			String string = identify[i].toASCIIString();
			System.out.println(string);
			strings[i] = string;
		}
		return strings;
	}
}
