/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author melmsp
 *
 */
public class ZipUtilsTest {
	
	private static final File TEST_FILE_FOLDER = 
			new File("IF/common/src/test/resources/test_zip");
	
	private static final File OUTPUT_FOLDER = 
		FileUtils.createWorkFolderInSysTemp("ZipUtilsTest_Tmp");
	
	private static final File EXTRACT_RESULT_OUT = 
		FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, "EXTRACTED");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileUtils.deleteAllFilesInFolder(OUTPUT_FOLDER);
	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#createZip(java.io.File, java.io.File, java.lang.String)}.
	 */
	@Test
	public void testCreateZipAndUnzipTo() {
		int inputFileCount = FileUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, OUTPUT_FOLDER, "zipUtilsTest.zip");
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, folderName);
		List<File> extracted = ZipUtils.unzipTo(zip, extract);
		System.out.println("Extracted files:" + System.getProperty("line.separator"));
		for (File file : extracted) {
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("input file-count:  " + inputFileCount);
		System.out.println("output file-count: " + extracted.size());
	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#createZipAndCheck(java.io.File, java.io.File, java.lang.String)}.
	 */
	@Test
	public void testCreateZipAndCheckAndCheckAndUnzip() {
		int inputFileCount = FileUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		ZipResult zip = ZipUtils.createZipAndCheck(TEST_FILE_FOLDER, OUTPUT_FOLDER, "zipUtilsTestCheck.zip");
		System.out.println("[Checksum]: Algorith=" + zip.getChecksum().getAlgorithm() + " | checksum=" + zip.getChecksum().getValue());
		System.out.println("Zip created. Please find it here: " + zip.getZipFile().getAbsolutePath());
		String folderName = zip.getZipFile().getName().substring(0, zip.getZipFile().getName().lastIndexOf("."));
		File extract = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, folderName);
		List<File> extracted = ZipUtils.checkAndUnzipTo(zip.getZipFile(), EXTRACT_RESULT_OUT, zip.getChecksum());
		System.out.println("Extracted files:" + System.getProperty("line.separator"));
		for (File file : extracted) {
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("input file-count:  " + inputFileCount);
		System.out.println("output file-count: " + extracted.size());
	}
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#removeFileFrom(java.io.File, java.lang.String)}.
	 */
	@Test
	public void testRemoveFile() {
		FileUtils.deleteAllFilesInFolder(OUTPUT_FOLDER);
		int inputFileCount = FileUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, OUTPUT_FOLDER, "zipUtilsTestRemove.zip");
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, folderName);
		List<File> extracted = ZipUtils.unzipTo(zip, extract);
		File deleteSingleFile = new File("IF/common/src/test/resources/test_zip/images/test_jp2/canon-ixus.jpg.jp2");
		File modifiedZip = ZipUtils.removeFileFrom(zip, "images\\test_jp2\\canon-ixus.jpg.jp2");
		modifiedZip = ZipUtils.removeFileFrom(zip, "images\\test_gif");
		System.out.println("Zip modified. Please find it here: " + zip.getAbsolutePath());
	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#insertFileInto(java.io.File, java.io.File, java.lang.String)}.
	 */
	@Test
	public void testInsertFile() {
		FileUtils.deleteAllFilesInFolder(OUTPUT_FOLDER);
		int inputFileCount = FileUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, OUTPUT_FOLDER, "zipUtilsTestInsert.zip");
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, folderName);
		List<File> extracted = ZipUtils.unzipTo(zip, extract);
		Date date = new Date(50,9,30);
		File toInsert = new File("tests/test-files/documents/test_pdf/nested_folder/nested3.pdf");
		toInsert.setLastModified(date.getDate());
		File modifiedZip = ZipUtils.insertFileInto(zip, toInsert, "documents\\test_pdf\\nested_folder\\eingefuegtesPDF.pdf");
		File insertMore = new File("tests/test-files/documents/test_pdf/");
		modifiedZip = ZipUtils.insertFileInto(zip, insertMore, "documents\\test_pdf");
		System.out.println("Zip modified. Please find it here: " + zip.getAbsolutePath());
	}
	
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#insertFileInto(java.io.File, java.io.File, java.lang.String)}.
	 */
	@Test
	public void testGetFileFrom() {
		FileUtils.deleteAllFilesInFolder(OUTPUT_FOLDER);
		int inputFileCount = FileUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, OUTPUT_FOLDER, "zipUtilsTestGetFile.zip");
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = FileUtils.createFolderInWorkFolder(OUTPUT_FOLDER, folderName);
		List<File> extracted = ZipUtils.unzipTo(zip, extract);
		
		File fromZip = ZipUtils.getFileFrom(zip, "images\\test_jpeg\\fujifilm-mx1700.jpg", OUTPUT_FOLDER);
		System.out.println("File extracted. Please find it here: " + fromZip.getAbsolutePath());
	}

	

	

}
