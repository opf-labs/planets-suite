/**
 * 
 */
package eu.planets_project.services.file.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.file.FileIdentify;
import eu.planets_project.services.file.FileServiceUtilities;
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
        this.doi = ServiceCreator.createTestService(Identify.QNAME, FileIdentify.class,
        									   FileIdentifyTest.wsdlLoc);
    }

    /**
     * Test for {@link eu.planets_project.services.file.FileIdentify#describe()}.
     */
    @Test
    public void testDescribe() {
        System.out.println("Test description");
        ServiceDescription desc = this.doi.describe();
        assertNotNull("The ServiceDescription should not be NULL.", desc);
        if (desc != null)
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
    	if ((FileServiceUtilities.isLinux()) || (FileServiceUtilities.isWindows()) && (FileServiceUtilities.isCygwinFileDetected())) {
    		System.out.println("OS is windows based and cygwin file.exe detected so run the tests");
	        testIdentifyThis(new File("PC/file/src/test/resources/test_word.doc").toURI(), new URI("planets:fmt/mime/application/msword"));
	        testIdentifyThis(new File("PC/file/src/test/resources/test_pdf.pdf").toURI(), new URI("planets:fmt/mime/application/pdf"));
	        testIdentifyThis(new File("PC/file/src/test/resources/test_jpeg.jpg").toURI(), new URI("planets:fmt/mime/image/jpeg"));
	        testIdentifyThis(new File("PC/file/src/test/resources/test_png.png").toURI(), new URI("planets:fmt/mime/image/png"));
    	} else if (FileServiceUtilities.isWindows()) {
    		System.out.println("OS is windows but cygwin file exe is not detected.");
    		System.out.println("No identification tests run.");
    	} else {
    		System.out.println("None windows OS so no identification tests run.");
    	}
    }
    
    private void testIdentifyThis(URI purl, URI type) throws MalformedURLException {
        /* Create the content: */
        DigitalObjectContent c1 = Content.byReference(purl.toURL());
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).build();
        
        /* Now pass this to the service */
        IdentifyResult ir = doi.identify(object,null);
        
        /* Check the result */
        assertTrue("The IdentifyResult should not be NULL.", ir != null);
        System.out.println("Recieved type: " + ir.getTypes() );
        System.out.println("Recieved service report: " + ir.getReport() );
        assertEquals("The returned type did not match the expected;", type, ir
                .getTypes().get(0));
        
    }
}
