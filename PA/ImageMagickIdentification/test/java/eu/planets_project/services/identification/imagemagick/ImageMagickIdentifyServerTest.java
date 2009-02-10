package eu.planets_project.services.identification.imagemagick;

import org.junit.BeforeClass;

import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.utils.test.ServiceCreator;

public class ImageMagickIdentifyServerTest extends ImageMagickIdentifyLocalTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("************************************************");
		System.out.println("* Running SERVER tests for ImageMagickIdentify *");
		System.out.println("************************************************");
		System.out.println();
		System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost");
        System.setProperty("pserv.test.port", "8080");
		imIdentify = ServiceCreator.createTestService(Identify.QNAME, ImageMagickIdentify.class, WSDL);
	}
}
