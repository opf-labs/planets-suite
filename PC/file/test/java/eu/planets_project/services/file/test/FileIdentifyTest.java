/**
 * 
 */
package eu.planets_project.services.file.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.file.FileIdentify;
import eu.planets_project.services.file.util.FileServiceSetup;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the file identification service
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class FileIdentifyTest {

    /* The location of this service when deployed. */
    private final static String wsdlLoc = "/pserv-pc-file/FileIdentify?wsdl";

	/** A holder for the service to be tested */
	private Identify doi = null;
	
    /** 
     * @throws Exception 
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        doi = ServiceCreator.createTestService(Identify.QNAME, FileIdentify.class,
        									   FileIdentifyTest.wsdlLoc);
    }

    /**
     * 
     * @throws Exception 
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test for {@link eu.planets_project.services.file.FileIdentify#describe()}.
     */
    @Test
    public void testDescribe() {
        System.out.println("Test description");
        ServiceDescription desc = doi.describe();
        System.out.println("Received service description: ");
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
        System.out.println(desc.toXml());
    }
    
    /**
     * Test for {@link eu.planets_project.services.file.FileIdentify#identify(eu.planets_project.services.datatypes.DigitalObject)}.
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @Test
    public void testIdentify() throws MalformedURLException, URISyntaxException {
    	// Run the tests if on a windows box, they'll currently fail otherwise
    	if ((FileServiceSetup.isWindows()) && (FileServiceSetup.isCygwinFileDetected())) {
    		System.out.println("OS is windows based and cygwin file.exe detected so run the tests");
	        testIdentifyThis(new File("PC/file/test/resources/test_word.doc").toURI().toURL(), new URI("planets:fmt/mime/application/msword"));
	        testIdentifyThis(new File("PC/file/test/resources/test_pdf.pdf").toURI().toURL(), new URI("planets:fmt/mime/application/pdf"));
	        testIdentifyThis(new File("PC/file/test/resources/test_jpeg.jpg").toURI().toURL(), new URI("planets:fmt/mime/image/jpeg"));
	        testIdentifyThis(new File("PC/file/test/resources/test_png.png").toURI().toURL(), new URI("planets:fmt/mime/image/png"));
    	} else if (FileServiceSetup.isWindows()) {
    		System.out.println("OS is windows but cygwin file exe is not detected.");
    		System.out.println("No identification tests run.");
    	} else {
    		System.out.println("None windows OS so no identification tests run.");
    	}
    }
    
    private void testIdentifyThis(URL purl, URI type) {
        /* Create the content: */
        Content c1 = Content.byReference(purl);
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUrl(purl).build();
        
        /* Now pass this to the service */
        IdentifyResult ir = doi.identify(object);
        
        /* Check the result */
        assertTrue("The IdentifyResult should not be NULL.", ir != null);
        System.out.println("Recieved type: " + ir.getTypes() );
        System.out.println("Recieved service report: " + ir.getReport() );
        assertEquals("The returned type did not match the expected;", type, ir
                .getTypes().get(0));
        
    }
}
