/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author melmsp
 *
 */
public class ZipUtilsTest {
	
	private static final File TEST_FILE_FOLDER = new File("IF/common/src/test/resources/test_zip");
//	private static final File TEST_FILE_FOLDER = new File("C:/Dokumente und Einstellungen/melmsp/Desktop/hartwig/test_zip");
	
    private static File outputFolder = null;

    private static File extractResultOut = null;
    private static File zip = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	    outputFolder = new File(DigitalObjectUtils.SYSTEM_TEMP_DIR, "ZipUtils_Test_Tmp".toUpperCase());
	    FileUtils.forceMkdir(outputFolder);
	    extractResultOut = new File(outputFolder, "EXTRACTED");
	    FileUtils.forceMkdir(extractResultOut);
		FileUtils.cleanDirectory(outputFolder);
		zip = ZipUtils.createZip(TEST_FILE_FOLDER, outputFolder,
                TEST_FILE_FOLDER.getName() + ".zip", false);
	}
	

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#createZip(java.io.File, java.io.File, java.lang.String, boolean)}.
	 * @throws IOException 
	 */
	@Test
	public void testCreateZipAndUnzipTo() throws IOException {
		FileUtils.cleanDirectory(outputFolder);
		int inputFileCount = ZipUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, outputFolder, "zipUtilsTest.zip", true);
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = new File(outputFolder, folderName);
		FileUtils.forceMkdir(extract);
		List<File> extracted = ZipUtils.unzipTo(zip, extract);
		System.out.println("Extracted files:" + System.getProperty("line.separator"));
		for (File file : extracted) {
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("input file-count:  " + inputFileCount);
		System.out.println("output file-count: " + extracted.size());
	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#createZipAndCheck(java.io.File, java.io.File, java.lang.String, boolean)}.
	 * @throws IOException 
	 */
	@Test
	public void testCreateZipAndCheckAndCheckAndUnzip() throws IOException {
		int inputFileCount = ZipUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		ZipResult zip = ZipUtils.createZipAndCheck(TEST_FILE_FOLDER, outputFolder, "zipUtilsTestCheck.zip", true);
		System.out.println("[Checksum]: Algorith=" + zip.getChecksum().getAlgorithm() + " | checksum=" + zip.getChecksum().getValue());
		System.out.println("Zip created. Please find it here: " + zip.getZipFile().getAbsolutePath());
		String folderName = zip.getZipFile().getName().substring(0, zip.getZipFile().getName().lastIndexOf("."));
		File extract = new File(outputFolder, folderName);
		FileUtils.forceMkdir(extract);
		List<File> extracted = ZipUtils.checkAndUnzipTo(zip.getZipFile(), extractResultOut, zip.getChecksum());
		System.out.println("Extracted files:" + System.getProperty("line.separator"));
		for (File file : extracted) {
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("input file-count:  " + inputFileCount);
		System.out.println("output file-count: " + extracted.size());
	}
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#removeFileFrom(java.io.File, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testRemoveFile() throws IOException {
		FileUtils.cleanDirectory(outputFolder);
		ZipUtils.listAllFilesAndFolders(TEST_FILE_FOLDER, new ArrayList<File>()).size();
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, outputFolder, "zipUtilsTestRemove.zip", true);
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		String folderName = zip.getName().substring(0, zip.getName().lastIndexOf("."));
		File extract = new File(outputFolder, folderName);
		FileUtils.forceMkdir(extract);
		ZipUtils.unzipTo(zip, extract);
		new File("framework/utils/src/test/resources/test_zip/images/test_jp2/canon-ixus.jpg.jp2");
//		File deleteSingleFile = new File("IF/common/src/test/resources/test_zip/images/test_jp2/canon-ixus.jpg.jp2");
		ZipUtils.removeFileFrom(zip, "images\\test_jp2\\canon-ixus.jpg.jp2");
		ZipUtils.removeFileFrom(zip, "images\\test_gif");
		System.out.println("Zip modified. Please find it here: " + zip.getAbsolutePath());
	}

	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#insertFileInto(java.io.File, java.io.File, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testInsertFile() throws IOException {
		FileUtils.cleanDirectory(outputFolder);
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, outputFolder, "zipUtilsTestInsert.zip", true);
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		File toInsert = new File("IF/common/src/test/resources/test_zip/images/Kopie von test_gif");
		ZipUtils.insertFileInto(zip, toInsert, "images\\test_gif");
//		File modifiedZip = ZipUtils.insertFileInto(zip, toInsert, "images\\test_gif");
//		File insertMore = new File("tests/test-files/documents/test_pdf/");
//		modifiedZip = ZipUtils.insertFileInto(zip, insertMore, "documents\\test_pdf");
		System.out.println("Zip modified. Please find it here: " + zip.getAbsolutePath());
	}
	
	
	/**
	 * Test method for {@link eu.planets_project.services.utils.ZipUtils#insertFileInto(java.io.File, java.io.File, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetFileFrom() throws IOException {
		FileUtils.cleanDirectory(outputFolder);
		File zip = ZipUtils.createZip(TEST_FILE_FOLDER, outputFolder, "zipUtilsTestGetFile.zip", true);
		System.out.println("Zip created. Please find it here: " + zip.getAbsolutePath());
		
		File fromZip = ZipUtils.getFileFrom(zip, "images\\test_gif", outputFolder);
		System.out.println("File extracted. Please find it here: " + fromZip.getAbsolutePath());
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
     * {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File, java.io.File)}
     * .
     */
    @Test
    public void testExtractFilesFromZip() {
        List<File> files = ZipUtils.unzipTo(zip,
                extractResultOut);
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
                TEST_FILE_FOLDER, outputFolder, DigitalObjectUtils.randomizeFileName(TEST_FILE_FOLDER.getName()
                        + ".zip"), false);
        File zip = zipResult.getZipFile();
        System.out.println("Please find ZIP here: " + zip.getAbsolutePath());
        System.out.println("Zip Checksum is: " + zipResult.getChecksum());
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.utils.FileUtils#extractFilesFromZip(java.io.File, java.io.File)}
     * .
     */
    @Test
    public void testcheckAndExtractFilesFromZip() {
        ZipResult zipResult = ZipUtils.createZipAndCheck(
                TEST_FILE_FOLDER, outputFolder, DigitalObjectUtils.randomizeFileName(TEST_FILE_FOLDER.getName()
                        + ".zip"), false);
        File zip = zipResult.getZipFile();
        System.out.println("Checksum before Extraction: "
                + zipResult.getChecksum());
        List<File> files = ZipUtils.checkAndUnzipTo(zip,
                extractResultOut, zipResult.getChecksum());
        if (files != null) {
            for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
                File file = iterator.next();
                System.out.println("Extracted file name: "
                        + file.getAbsolutePath());
            }
        }
    }

}
