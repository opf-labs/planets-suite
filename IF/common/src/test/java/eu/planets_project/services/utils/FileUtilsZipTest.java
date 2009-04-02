/**
 * 
 */
package eu.planets_project.services.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Checksum;

/**
 * @author melmsp
 *
 */
public class FileUtilsZipTest {

	public static File TEST_FILE_FOLDER = new File("IF/common/src/test/resources/testFileUtils");
//	public static File TEST_FILE_FOLDER = new File("IF/common/src/test/resources/test_zip");
	
	public static File OUTPUT_FOLDER = FileUtils.createWorkFolderInSysTemp("FileUtilsZipTest_OUT");
	public static File EXTRACT_RESULT_OUT = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, "EXTRACTED");
	public static File zip = null;
//	public static ZipResult zipResult = null;
//	public static Checksum checksum = null;
	
//	@BeforeClass
//	public static void setup() {
//		System.setProperty("pserv.test.context", "server");
//        System.setProperty("pserv.test.host", "localhost");
//        System.setProperty("pserv.test.port", "8080");
//	}
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.FileUtils#createZipFileWithChecksum(java.io.File, File, java.lang.String)}.
	 * @throws IOException 
	 * @throws ZipException 
	 */
	@Test
	public void testCreateSimpleZipFile() throws ZipException, IOException {
		File[] files = TEST_FILE_FOLDER.listFiles();
		System.out.println("File count: " + files.length);
		for (int i = 0; i < files.length; i++) {
			System.out.println(i + ": " + files[i].getAbsolutePath());
		}
		zip = FileUtils.createSimpleZipFile(TEST_FILE_FOLDER, OUTPUT_FOLDER, TEST_FILE_FOLDER.getName() + ".zip");
//		zipResult = FileUtils.createZipFileWithChecksum(TEST_FILE_FOLDER, OUTPUT_FOLDER, TEST_FILE_FOLDER.getName() + ".zip");
//		zip = zipResult.getZipFile();
		System.out.println("Please find ZIP here: " + zip.getAbsolutePath());
//		System.out.println("Zip Checksum is: " + zipResult.getChecksum());
	}


	/**
	 * Test method for {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File)}.
	 */
	@Test
	public void testExtractFilesFromZip() {
//		System.out.println("Checksum before Extraction: " + zipResult.getChecksum());
//		List<File> files = FileUtils.extractFilesFromZipAndCheck(zip, EXTRACT_RESULT_OUT, zipResult.getChecksum());
		List<File> files = FileUtils.extractFilesFromZip(zip, EXTRACT_RESULT_OUT);
		if(files!=null) {
			for (Iterator iterator = files.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				System.out.println("Extracted file name: " + file.getAbsolutePath());
			}
		}
	}

	

}
