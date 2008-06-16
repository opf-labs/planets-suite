package eu.planets_project.ifr.core.services.extraction;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;


public class DroidTest {
	@Test
	public void testByteArrayReading() throws Exception{
		File file = new File("/Users/fsteeg/Documents/eclipsestuff/workspace/if_sp/components/droid/Licence.rtf");
		FileInputStream in = new FileInputStream(file);
		byte[] array = new byte[(int)file.length()];
		in.read(array);
		Droid d = new Droid();
		String[] identify = d.identify(array);
		assertEquals("fmt/50", identify[0]);
		assertEquals("fmt/51", identify[1]);
		for (String string : identify) {
			System.out.println(string);
		}
	}
}
