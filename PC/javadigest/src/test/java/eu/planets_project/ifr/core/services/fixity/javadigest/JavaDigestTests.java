/**
 * 
 */
package eu.planets_project.ifr.core.services.fixity.javadigest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.commons.codec.digest.DigestUtils;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.TestFile;

/**
 * Automated tests for the JavaDigest fixity service.  
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class JavaDigestTests {

    static Fixity javaDigest = null;

    /**
     * Tests JavaDigest Checksum creation
     */
    @BeforeClass
    public static void setup() {
        javaDigest = ServiceCreator.createTestService(Fixity.QNAME, JavaDigest.class,
                "/pserv-pc-javadigest/JavaDigest?wsdl");
    }
    
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testRtf() { testDefaultDigest(TestFile.RTF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testBmp() { testDefaultDigest(TestFile.BMP, javaDigest); }
    /**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testXml() { testDefaultDigest(TestFile.XML, javaDigest); }
    /**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testZip() { testDefaultDigest(TestFile.ZIP, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPdf() { testDefaultDigest(TestFile.PDF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testGif() { testDefaultDigest(TestFile.GIF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testJpg() { testDefaultDigest(TestFile.JPG, javaDigest); }
    /**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testTif() { testDefaultDigest(TestFile.TIF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPcx() { testDefaultDigest(TestFile.PCX, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPng() { testDefaultDigest(TestFile.PNG, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testWav() { testDefaultDigest(TestFile.WAV, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testHtml(){ testDefaultDigest(TestFile.HTML, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#describe()}.
	 */
	@Test
	public void testDescribe() {
        ServiceDescription desc = javaDigest.describe();
        assertNotNull("The ServiceDescription should not be NULL.", desc);
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
	}

	private void testDefaultDigest(TestFile testFile, Fixity fixity) {
		// Ok let's make the call to test
        FixityResult fixityResult = fixity.calculateChecksum(
                new DigitalObject.Builder(Content.byReference(new File(testFile
                        .getLocation()))).build(), null);
        
        // Check the result against an "independent" MD5 hash implementation
        try {
        	// Use the apache codec MD5 algorithm
        	File theFile = new File(testFile.getLocation());
        	InputStream inStream = new FileInputStream(theFile);
			byte[] hash = DigestUtils.md5(inStream);
			inStream.close();

			// Assert that the hashes are equal
			assertTrue("Expecting Fast MD5 and Java MD5 byte hashes to be equal",
					Arrays.equals(hash, fixityResult.getDigestValue()));
			
			// Check the hex string value of the hash
			InputStream newStream = new FileInputStream(theFile);
			String hexhash = DigestUtils.md5Hex(newStream);
			
			// Assert that the string hashes are equal
			assertEquals("Expecting Fast MD5 and Java MD5 string hashes to be equal",
					hexhash,
					fixityResult.getHexDigestValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Failure reading TestFile " + testFile.getLocation());
		}
        
        this.checkResult(fixityResult);
        
        System.out.println("File " + testFile.toString() + 
        				   " gave digest " + fixityResult.getHexDigestValue());
	}

	private void checkResult(FixityResult fixityResult) {
		if (fixityResult.getReport().getType() != ServiceReport.Type.INFO) {
			System.out.println("Problem with Service Report");
			System.out.println(fixityResult.getReport().getMessage());
		}
        // We'd expect the Report type to be INFO
        assertEquals("Expected ServiceReport.Type to be " + ServiceReport.Type.INFO,
        			 ServiceReport.Type.INFO,
        			 fixityResult.getReport().getType());

        // We'd expect the Report status to be SUCCESS
        assertEquals("Expected ServiceReport.Status to be " + ServiceReport.Status.SUCCESS,
        			 fixityResult.getReport().getStatus(),
        			 ServiceReport.Status.SUCCESS);
        
        // We'd not expect a null Digest value or algorithm identifier, or provider
        assertNotNull("FixityResult.getDigestValue() should not be null",
        		fixityResult.getDigestValue());
        assertNotNull("FixityResult.getDigestValueAsString() should not be null",
        		fixityResult.getHexDigestValue());
        assertNotNull("FixityResult.getAlgorithmId() should not be null",
        		fixityResult.getAlgorithmId());
        assertNotNull("FixityResult.getAlgorithmprovider() should not be null",
        		fixityResult.getAlgorithmProvider());
	}
}
