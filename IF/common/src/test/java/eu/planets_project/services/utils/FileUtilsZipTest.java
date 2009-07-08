/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author melmsp
 */
public class FileUtilsZipTest {
    private static final File TEST_FILE_FOLDER = new File(
            "IF/common/src/test/resources/test_zip");
    private static final File OUTPUT_FOLDER = FileUtils
            .createWorkFolderInSysTemp("FileUtilsZipTest_OUT");
    private static final File EXTRACT_RESULT_OUT = FileUtils
            .createFolderInWorkFolder(OUTPUT_FOLDER, "EXTRACTED");
    private static File zip = null;

    @BeforeClass
    public static void setup() {
    	FileUtils.deleteAllFilesInFolder(OUTPUT_FOLDER);
        zip = ZipUtils.createZip(TEST_FILE_FOLDER, OUTPUT_FOLDER,
                TEST_FILE_FOLDER.getName() + ".zip", false);
    }

    @Test
    public void testCreateSimpleZipFile() {
        File[] files = TEST_FILE_FOLDER.listFiles();
        System.out.println("File count: " + files.length);
        for (int i = 0; i < files.length; i++) {
            System.out.println(i + ": " + files[i].getAbsolutePath());
        }
        System.out.println("Please find ZIP here: " + zip.getAbsolutePath());
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File)}
     * .
     */
    @Test
    public void testExtractFilesFromZip() {
        List<File> files = ZipUtils.unzipTo(zip,
                EXTRACT_RESULT_OUT);
        if (files != null) {
            for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
                File file = iterator.next();
                System.out.println("Extracted file name: "
                        + file.getAbsolutePath());
            }
        }
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.utils.FileUtils#createZipFileWithChecksum(java.io.File, File, java.lang.String)}
     * .
     */
    @Test
    public void testCreateZipFileWithChecksum() {
        File[] files = TEST_FILE_FOLDER.listFiles();
        System.out.println("File count: " + files.length);
        for (int i = 0; i < files.length; i++) {
            System.out.println(i + ": " + files[i].getAbsolutePath());
        }
        ZipResult zipResult = ZipUtils.createZipAndCheck(
                TEST_FILE_FOLDER, OUTPUT_FOLDER, TEST_FILE_FOLDER.getName()
                        + ".zip", false);
        File zip = zipResult.getZipFile();
        System.out.println("Please find ZIP here: " + zip.getAbsolutePath());
        System.out.println("Zip Checksum is: " + zipResult.getChecksum());
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File)}
     * .
     */
    @Test
    public void testcheckAndExtractFilesFromZip() {
        ZipResult zipResult = ZipUtils.createZipAndCheck(
                TEST_FILE_FOLDER, OUTPUT_FOLDER, TEST_FILE_FOLDER.getName()
                        + ".zip", false);
        File zip = zipResult.getZipFile();
        System.out.println("Checksum before Extraction: "
                + zipResult.getChecksum());
        List<File> files = ZipUtils.checkAndUnzipTo(zip,
                EXTRACT_RESULT_OUT, zipResult.getChecksum());
        if (files != null) {
            for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
                File file = iterator.next();
                System.out.println("Extracted file name: "
                        + file.getAbsolutePath());
            }
        }
    }

}
