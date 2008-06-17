package eu.planets_project.ifr.core.services.identification;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;
import eu.planets_project.ifr.core.services.identification.droid.impl.FileHelper;

public class TestHelper {
	/**
	 * Enum containing files to test the Droid identification with. Each entry
	 * contains the file location and the expected results. In the tests, we
	 * iterate over all files, identify the file at the location and compare the
	 * received results with the expected ones.
	 */
	public enum File {
		RTF(FileHelper.LOCAL + "Licence.rtf", "info:pronom/fmt/50",
				"info:pronom/fmt/51"),

		XML(FileHelper.LOCAL + "DROID_SignatureFile_Planets.xml",
				"info:pronom/fmt/101"),

		ZIP(FileHelper.LOCAL + "Licence.zip", "info:pronom/x-fmt/263");
		String location;
		String[] expected;

		File(String location, String... expected) {
			this.location = location;
			this.expected = expected;
		}
	}

	/**
	 * @param droid
	 *            The Droid instance to test. Alle the files in the Files enum
	 *            are identified using the droid instance and recieved results
	 *            are compared to the expected results defined in the elements
	 *            ofthe Files enum.
	 */
	public static void testAllFiles(Droid droid) throws FileNotFoundException,
			IOException, Exception {
		for (File f : File.values()) {
			String[] identify = test(droid, f.location);
			for (int i = 0; i < identify.length; i++) {
				assertEquals("Identification failed for " + f.location,
						f.expected[i], identify[i]);
			}
		}
	}

	private static String[] test(Droid droid, String location)
			throws FileNotFoundException, IOException, Exception {
		byte[] array = FileHelper.byteArrayForFile(location);
		URI[] identify = droid.identifyBytes(array).types;
		String[] strings = new String[identify.length];
		for (int _loop = 0; _loop < identify.length; _loop++) {
			String string = identify[_loop].toASCIIString();
			System.out.println(string);
			strings[_loop] = string;
		}
		return strings;
	}
}
