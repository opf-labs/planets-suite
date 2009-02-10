/**
 * 
 */
package eu.planets_project.services.identification.imagemagick;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * @author melmsp
 *
 */
public class ImageMagickIdentifyLocalTest {
	
	static Identify imIdentify;
	
	static String WSDL = "/pserv-pa-imagemagick/ImageMagickIdentify?wsdl";
	
	static FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("***********************************************");
		System.out.println("* Running LOCAL tests for ImageMagickIdentify *");
		System.out.println("***********************************************");
		System.out.println();
		System.setProperty("pserv.test.context", "local");
		imIdentify = ServiceCreator.createTestService(Identify.QNAME, ImageMagickIdentify.class, WSDL);
	}

	/**
	 * Test method for {@link eu.planets_project.services.identification.imagemagick.impl.ImageMagickIdentify#describe()}.
	 */
	@Test
	public void testDescribe() {
		 ServiceDescription desc = imIdentify.describe();
	     System.out.println("Recieved service description: " + desc.toXmlFormatted());
	     assertTrue("The ServiceDescription should not be NULL.", desc != null );
	}

	/**
	 * Test method for {@link eu.planets_project.services.identification.imagemagick.impl.ImageMagickIdentify#identify(eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testIdentify() throws MalformedURLException {
		HashMap<File, String> files = ImageMagickIdentifyTestHelper.getTestFiles();
		
		Set<File> fileSet = files.keySet();
		
		for (File file : fileSet) {
			String ext = files.get(file);
			DigitalObject digObj = DigitalObject.create(Content.byValue(file))
			.permanentUrl(new URL("http://planets-project.eu/services/pserv-pa-imagemagickidentify-test"))
			.format(Format.extensionToURI(ext))
			.title(file.getName())
			.build();
			System.out.println("Testing identification of " + ext.toUpperCase() + ": " + file.getName());
			IdentifyResult ir = imIdentify.identify(digObj);
			validateResult(ir);
		}
	}
	
	private void validateResult(IdentifyResult identifyResult) {
		ServiceReport sr = identifyResult.getReport();
		
		if(sr.getErrorState()==1) {
			System.err.println("FAILED: " + sr);
		}
		else {
			System.out.println("SUCCESS! Got Report: " + sr);
			List<URI> types = identifyResult.getTypes();
			assertTrue("List of types should not be null or of length 0", types.size()>0);
			System.out.println("Received file type URIs: ");
			for (URI uri : types) {
				System.out.println(uri.toASCIIString());
			}
		}
	}

}
