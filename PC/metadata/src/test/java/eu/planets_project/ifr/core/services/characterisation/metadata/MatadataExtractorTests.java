package eu.planets_project.ifr.core.services.characterisation.metadata;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinary;
import eu.planets_project.ifr.core.services.characterisation.metadata.impl.MetadataExtractor;

/**
 * Local and client tests of the metadata extractor functionality
 * 
 * @author Fabian Steeg
 */
public class MatadataExtractorTests {

	public static final String SAMPLES = "PC/metadata/src/resources/samples/";

	public enum MetadataType {
		/** Some types work just fine and give a full result: */
		BMP("bmp/test1.bmp", "image/bmp"),
		GIF("gif/AA_Banner.gif", "image/gif"),
		JPEG("jpeg/AA_Banner.jpg", "image/jpeg"),
		TIFF("tiff/AA_Banner.tif", "image/tiff"),
		PDF("pdf/AA.pdf", "application/pdf"),
		WAV("wav/comet.wav", "wave"),
		HTML("html/sample.html", "text/html"),
		/** The OO adapter throws an exception but still works: */
		OPEN_OFFICE1("oo1/planets.sxw", "application/open-office-1.x"),
		/**
		 * And some are characterized as unknown although they should be
		 * supported (according to http://meta-extractor.sourceforge.net):
		 */
		WORD_PERFECT("wordperfect/sample.wpd", "file/unknown"),
		WORD6("word6/planets.doc", "file/unknown"),
		WORKS("works/sample.wps", "file/unknown"),
		EXCEL("excel/Travel.xls", "file/unknown"),
		POWER_POINT("pp/planets.ppt", "file/unknown"),
		MP3("mp3/Arkansas.mp3", "file/unknown"),
		XML("xml/sample.xml", "file/unknown");
		private String mime;
		private String location;

		private MetadataType(String location, String type) {
			this.location = location;
			this.mime = type;
		}
	}

	/**
	 * Tests MetadataExtractor characterization using a local MetadataExtractor
	 * instance
	 * 
	 * @throws PlanetsException
	 */
	@Test
	public void localTests() throws PlanetsException {
		System.out.println("Local:");
		test(new MetadataExtractor());
	}

	/**
	 * Tests MetadataExtractor identification using a MetadataExtractor instance
	 * retrieved via the web service (running on localhost)
	 * 
	 * @throws MalformedURLException
	 * @throws PlanetsException
	 */
	@Test
	public void clientTests() throws MalformedURLException, PlanetsException {
		System.out.println("Remote:");
		Service service = Service
				.create(
						new URL(
								"http://localhost:8080/pserv-pc-metadata/MetadataExtractor?wsdl"),
						new QName(PlanetsServices.NS,
								BasicCharacteriseOneBinary.NAME));
		BasicCharacteriseOneBinary characterise = service
				.getPort(BasicCharacteriseOneBinary.class);
		test(characterise);
	}

	/**
	 * Test a BasicValidateOneBinary instance
	 * 
	 * @param characterise The BasicCharacteriseOneBinary instance to test
	 * @throws PlanetsException
	 */
	private void test(BasicCharacteriseOneBinary characterise)
			throws PlanetsException {
		/* Test all file types supported by the tool */
		for (MetadataType type : MetadataType.values()) {
			System.out.println("Testing characterisation of " + type);
			File file = new File(SAMPLES + type.location);
			byte[] binary = ByteArrayHelper.read(file);
			String result = characterise.basicCharacteriseOneBinary(binary);
			System.out.println("Characterised " + file.getAbsolutePath()
					+ " as: " + result);
			assertTrue("Result does not contain the correct mime type: "
					+ type.mime, result.toLowerCase().contains(type.mime));
		}
	}
}
