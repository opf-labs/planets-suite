/**
 * 
 */
package eu.planets_project.ifr.core.services.fixity.javadigest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;
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
    @Test public void testRtf() { testDefaultDigest(TestFile.RTF, javaDigest); }
    @Test public void testAllRtf() { testAllDigestAlgorithms(TestFile.RTF, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testBmp() { testDefaultDigest(TestFile.BMP, javaDigest); }
    @Test public void testAllBmp() { testAllDigestAlgorithms(TestFile.BMP, javaDigest);}
/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testXml() { testDefaultDigest(TestFile.XML, javaDigest); }
    @Test public void testAllCml() { testAllDigestAlgorithms(TestFile.XML, javaDigest);}
/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testZip() { testDefaultDigest(TestFile.ZIP, javaDigest); }
    @Test public void testAllZip() { testAllDigestAlgorithms(TestFile.ZIP, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPdf() { testDefaultDigest(TestFile.PDF, javaDigest); }
    @Test public void testAllPdf() { testAllDigestAlgorithms(TestFile.PDF, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testGif() { testDefaultDigest(TestFile.GIF, javaDigest); }
    @Test public void testAllGif() { testAllDigestAlgorithms(TestFile.GIF, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testJpg() { testDefaultDigest(TestFile.JPG, javaDigest); }
    @Test public void testAllJpg() { testAllDigestAlgorithms(TestFile.JPG, javaDigest);}
/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testTif() { testDefaultDigest(TestFile.TIF, javaDigest); }
    @Test public void testAllTif() { testAllDigestAlgorithms(TestFile.TIF, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPcx() { testDefaultDigest(TestFile.PCX, javaDigest); }
    @Test public void testAllPcx() { testAllDigestAlgorithms(TestFile.PCX, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testPng() { testDefaultDigest(TestFile.PNG, javaDigest); }
    @Test public void testAllPng() { testAllDigestAlgorithms(TestFile.PNG, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testWav() { testDefaultDigest(TestFile.WAV, javaDigest); }
    @Test public void testAllWav() { testAllDigestAlgorithms(TestFile.WAV, javaDigest);}
	/**
	 * Test method for {@link eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest#calculateChecksum(eu.planets_project.services.datatypes.DigitalObject, java.util.List)}.
	 */
    @Test public void testHtml(){ testDefaultDigest(TestFile.HTML, javaDigest);}
    @Test public void testAllHtml() { testAllDigestAlgorithms(TestFile.HTML, javaDigest);}
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
		// Ok let's make the call
        FixityResult fixityResult = fixity.calculateChecksum(
                new DigitalObject.Builder(Content.byReference(new File(testFile
                        .getLocation()))).build(), null);
        
        this.checkResult(fixityResult);
        
        System.out.println("File " + testFile.toString() + 
        				   " gave digest " + fixityResult.getDigestValueAsString());
	}
	
	private void testAllDigestAlgorithms(TestFile testFile, Fixity fixity) {
		return;
//		// First get the supported algs
//		URI[] theAlgs = JavaDigestUtils.getDigestAlgorithms();
//		DigitalObject digitalObject = new DigitalObject.Builder(Content.byReference(new File(testFile
//                .getLocation()))).build(); 
//		// Now test the algs
//		for (URI uri : theAlgs) {
//			List<Parameter> paramList = new ArrayList<Parameter>();
//			paramList.add(this.createAlgParam(uri));
//
//	        FixityResult fixityResult =
//	        	fixity.calculateChecksum(digitalObject, paramList);
//	        
//	        this.checkResult(fixityResult);
//	        System.out.println("File " + testFile.toString() + 
// 				   " gave digest " + fixityResult.getDigestValueAsString());
//
//	        // Save the result and the "default provider"
//	        byte[] firstDigestValue = fixityResult.getDigestValue().clone();
//
//	        // Now we can test against other provider implementations
//	        for (String provider : JavaDigestUtils.getProviders()) {
//	        	// If its the same provider or they don't implement the alg then don't bother 
//
//	        	// Add the provider param
//	        	paramList.add(this.createProvParam(provider));
//
//	        	// get the result
//		        FixityResult newResult =
//		        	fixity.calculateChecksum(digitalObject, paramList);
//
//				this.checkResult(newResult);
//				
//				// Test that the vals are equal
//				assertEquals("Expected firstResult to equal newResult.getDigestValue",
//							 firstDigestValue, newResult.getDigestValue());
//	        }
//		}
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
        
        // We'd not expect a null Digest value or algorithm identifier
        assertNotNull("FixityResult.getDigestValue() should not be null",
        		fixityResult.getDigestValue());
        assertNotNull("FixityResult.getDigestValueAsString() should not be null",
        		fixityResult.getDigestValueAsString());
        assertNotNull("FixityResult.getAlgorithmId() should not be null",
        		fixityResult.getAlgorithmId());
	}

	private Parameter createAlgParam(URI algID) {
		// Add the algorithm selection parameter from a builder
		// We need the name and the default value
		Parameter.Builder algBuilder = 
			new Parameter.Builder(JavaDigest.ALG_PARAM_NAME,
								  algID.toString());
		
		// Finally the type and deliver parameter goodness to our list
		algBuilder.type(JavaDigest.ALG_PARAM_TYPE);
		
		return algBuilder.build();
	}

	private Parameter createProvParam(String provName) {
		// Add the algorithm selection parameter from a builder
		// We need the name and the default value
		Parameter.Builder algBuilder = 
			new Parameter.Builder(JavaDigest.PROV_PARAM_NAME,
								  provName);
		
		// Finally the type and deliver parameter goodness to our list
		algBuilder.type(JavaDigest.PROV_PARAM_TYPE);
		
		return algBuilder.build();
	}
}
