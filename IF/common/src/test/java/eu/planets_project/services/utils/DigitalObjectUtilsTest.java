/**
 * 
 */
package eu.planets_project.services.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author melmsp
 *
 */
public class DigitalObjectUtilsTest {
	
	static File testFolder = new File("tests/test-files/documents/test_pdf");
	File resultZip = new File("IF/common/src/test/resources/test_zip/DigObUtilsTestZip.zip");

//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
////		FormatRegistry reg = FormatRegistryFactory.getFormatRegistry();
//	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.DigitalObjectUtils#createContainedAsStream(java.util.List)}.
	 */
//	@Test
//	public void testCreateContainedAsStream() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link eu.planets_project.services.utils.DigitalObjectUtils#createContainedbyReference(java.util.List)}.
//	 */
//	@Test
//	public void testCreateContainedbyReference() {
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.DigitalObjectUtils#getZipDigitalObjectFromFolder(java.io.File, boolean)}.
	 */
	@Test
	public void testGetZipDigitalObjectFromFolderAsStream() {
		DigitalObject result = DigitalObjectUtils.getZipDigitalObjectFromFolder(testFolder, "zip-from-folder", false);
		assertTrue("DigitalObject should NOT be NULL!", result!=null);
		System.out.println(result);
		List<DigitalObject> contained = result.getContained();
		assertTrue("The contained list should NOT be NULL", contained!=null);
		for (DigitalObject digitalObject : contained) {
			System.out.println(digitalObject);
		}
	}
	
	@Test
	public void testGetZipDigitalObjectFromFolderByReference() {
		DigitalObject result = DigitalObjectUtils.getZipDigitalObjectFromFolder(testFolder, "zip-from-folder.zip", true);
		assertTrue("DigitalObject should NOT be NULL!", result!=null);
		System.out.println(result);
		List<DigitalObject> contained = result.getContained();
		assertTrue("The contained list should NOT be NULL", contained!=null);
		for (DigitalObject digitalObject : contained) {
			System.out.println(digitalObject);
		}
	}
	
	@Test
	public void testGetZipDigitalObjectFromFileByReference() {
		DigitalObject result = DigitalObjectUtils.getZipDigitalObjectFromZipfile(resultZip, true);
		assertTrue("DigitalObject should NOT be NULL!", result!=null);
		System.out.println(result);
		List<DigitalObject> contained = result.getContained();
		assertTrue("The contained list should NOT be NULL", contained!=null);
		for (DigitalObject digitalObject : contained) {
			System.out.println(digitalObject);
		}
	}
	
	

}
