/**
 * 
 */
package eu.planets_project.ifr.core.services.fixity.javadigest;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.TestFile;

/**
 * @author CFWilson
 *
 */
public class JavaDigestTests {

    static Fixity javaDigest = null;

    /**
     * Tests JavaDigest Checksum creation
     */
    @BeforeClass
    public static void setup() {
        javaDigest = ServiceCreator.createTestService(Fixity.QNAME, JavaDigest.class,
                "/pserv-pc-javadigest/javaDigest?wsdl");
    }
    
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testRtf() { test(TestFile.RTF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testBmp() { test(TestFile.BMP, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testXml() { test(TestFile.XML, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testZip() { test(TestFile.ZIP, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPdf() { test(TestFile.PDF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testGif() { test(TestFile.GIF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testJpg() { test(TestFile.JPG, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testTif() { test(TestFile.TIF, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPcx() { test(TestFile.PCX, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPng() { test(TestFile.PNG, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testWav() { test(TestFile.WAV, javaDigest); }
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testHtml(){ test(TestFile.HTML, javaDigest);}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#describe()}.
	 */
	@Test
	public void testDescribe() {
        ServiceDescription desc = javaDigest.describe();
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
	}

	private void test(TestFile testFile, Fixity fixity) {
		// Ok let's make the call
        FixityResult fixityResult = fixity.calculateChecksum(
                new DigitalObject.Builder(Content.byReference(new File(testFile
                        .getLocation()))).build(), null);
        
        // We'd expect the Report type to be INFO
        assertEquals("Expected ServiceReport.Type to be " + ServiceReport.Type.INFO,
        			 fixityResult.getReport().getType(),
        			 ServiceReport.Type.INFO);

        // We'd expect the Report status to be SUCCESS
        assertEquals("Expected ServiceReport.Status to be " + ServiceReport.Status.SUCCESS,
        			 fixityResult.getReport().getStatus(),
        			 ServiceReport.Status.SUCCESS);
        
        // We'd not expect a null Digest value or algorithm identifier
        assertNotNull("FixityResult.getDigestValue() should not be null",
        		fixityResult.getDigestValue());
        assertNotNull("FixityResult.getDigestValueAsString() should not be null",
        		fixityResult.getDigestValueAsString());
        assertNotNull("FixityResult.getAlgorithmId() should not be null",
        		fixityResult.getAlgorithmId());
        
        // Finally check that the String value in the result == byte[] value.toString()
        assertEquals("Expected the FixityResult.getDigestValueAsString() to equal " +
        			 "FixityResult.getDigestValue().toString()",
        			 fixityResult.getDigestValueAsString(),
        			 fixityResult.getDigestValue().toString());
	}
}
