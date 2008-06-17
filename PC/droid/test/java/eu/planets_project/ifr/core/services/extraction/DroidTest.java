package eu.planets_project.ifr.core.services.extraction;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;

public class DroidTest {
	@Test
	public void testByteArrayReadingRTF() throws Exception {
		String[] identify = test("PC/droid/Licence.rtf");
		assertEquals("fmt/50", identify[0]);
		assertEquals("fmt/51", identify[1]);
	}

	@Test
	public void testByteArrayReadingXML() throws Exception {
		String[] identify = test("PC/droid/DROID_SignatureFile_Planets.xml");
		assertEquals("fmt/101", identify[0]);
	}

	private String[] test(String location) throws FileNotFoundException,
			IOException, Exception {
		File file = new File(location);
		FileInputStream in = new FileInputStream(file);
		byte[] array = new byte[(int) file.length()];
		in.read(array);
		Droid d = new Droid();
		String[] identify = d.identify(array);
		for (String string : identify) {
			System.out.println(string);
		}
		return identify;
	}

}
