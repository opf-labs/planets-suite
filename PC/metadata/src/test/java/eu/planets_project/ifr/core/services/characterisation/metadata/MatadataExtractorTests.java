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
		File file = new File("PC/metadata/src/resources/samples/pdf/AA.pdf");
		byte[] binary = ByteArrayHelper.read(file);
		String result = characterise.basicCharacteriseOneBinary(binary);
		System.out.println("Characterised " + file.getAbsolutePath() + " as: "
				+ result);
		assertTrue("Result does not contain the correct mime type;", result
				.toLowerCase().contains("application/pdf"));
		/* TODO test all supported file types, as in JHOVE service */
	}
}
