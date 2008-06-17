package eu.planets_project.ifr.core.services.identification;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

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
		RTF("PC/droid/Licence.rtf", "fmt/50", "fmt/51"), XML(
				"PC/droid/DROID_SignatureFile_Planets.xml", "fmt/101");
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
		String[] identify = droid.identifyBytes(array);
		for (String string : identify) {
			System.out.println(string);
		}
		return identify;
	}
}
