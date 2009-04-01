/**
 * 
 */
package eu.planets_project.services.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author melmsp
 *
 */
public class FileUtilsZipTest {

	public static File TEST_FILE_FOLDER = new File("IF/common/src/test/resources/test_gif");
	public static File OUTPUT_FOLDER = FileUtils.createWorkFolderInSysTemp("FileUtilsZipTest_OUT");
	public static File zip = null;
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.FileUtils#createSimpleZipFile(java.io.File, File, java.lang.String)}.
	 */
	@Test
	public void testCreateSimpleZipFile() {
		File[] files = TEST_FILE_FOLDER.listFiles();
		System.out.println("File count: " + files.length);
		for (int i = 0; i < files.length; i++) {
			System.out.println(i + ": " + files[i].getAbsolutePath());
		}
		zip = FileUtils.createSimpleZipFile(TEST_FILE_FOLDER, OUTPUT_FOLDER, TEST_FILE_FOLDER.getName() + ".zip");
		System.out.println("Please find ZIP here: " + zip.getAbsolutePath());
	}


	/**
	 * Test method for {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File)}.
	 */
	@Test
	public void testExtractFilesFromZip() {
		List<File> files = FileUtils.extractFilesFromZip(zip, OUTPUT_FOLDER);
		for (Iterator iterator = files.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			System.out.println("Extracted file name: " + file.getAbsolutePath());
		}
	}

	

}
