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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author melmsp
 *
 */
public class DigitalObjectUtilsTests {
	
    private static final File PROJECT_BASE_FOLDER = new File(System.getProperty("app.dir"));
    
	static File testFolder = new File(PROJECT_BASE_FOLDER, "tests/test-files/documents/test_pdf");
	File testZip = new File(PROJECT_BASE_FOLDER, "tests/test-files/archives/test_pdf.zip");
	File removeZip = new File(PROJECT_BASE_FOLDER, "tests/test-files/archives/insertFragmentTest.zip");
	static File work_folder = null;
	
	@BeforeClass
	public static void setup() throws IOException{
	    work_folder = new File(DigitalObjectUtils.SYSTEM_TEMP_DIR, "DigitalObjectUtilsTest_TMP".toUpperCase());
	    FileUtils.forceMkdir(work_folder);
	}
	
	@Test
    public void toFileDigitalObjectFile() throws IOException {
        DigitalObject object = new DigitalObject.Builder(Content.byReference(testZip)).build();
        File file = File.createTempFile("planets", null);
        DigitalObjectUtils.toFile(object, file);
        Assert.assertTrue(IOUtils.contentEquals(object.getContent().getInputStream(), file.toURI()
                .toURL().openStream()));
    }
    
    @Test
    public void toFileDigitalObject() throws MalformedURLException, IOException {
        DigitalObject object = new DigitalObject.Builder(Content.byReference(testZip)).build();
        File file = DigitalObjectUtils.toFile(object);
        Assert.assertTrue(IOUtils.contentEquals(object.getContent().getInputStream(), file.toURI()
                .toURL().openStream()));
    }
	
	@Test
	public void testCreateZipTypeDigObFolder() {
		printTestTitle("Test createZipTypeDigOb() from FOLDER");
		DigitalObject result = DigitalObjectUtils.createZipTypeDigitalObject(testFolder, testFolder.getName(), true, false, true);
		
		assertTrue("DigitalObject should NOT be NULL!", result!=null);
		printDigOb(result);
		File resultFile = new File(work_folder, result.getTitle());
		DigitalObjectUtils.toFile(result, resultFile);
		System.out.println("Result size: " + resultFile.length());
	}
	
	@Test
	public void testCreateZipTypeDigObZip() {
		printTestTitle("Test createZipTypeDigOb() from ZIP file");
		DigitalObject result = DigitalObjectUtils.createZipTypeDigitalObject(testZip, testFolder.getName(), true, false, true);
		assertTrue("DigitalObject should NOT be NULL!", result!=null);
		printDigOb(result);
	}
	
	@Test
	public void testGetFragmentFromZipTypeDigitalObject() {
		printTestTitle("Test getFragmentFromZipTypeDigitalObject()");
		DigitalObject result = DigitalObjectUtils.createZipTypeDigitalObject(testFolder, "getFragmentTest.zip", false, false, true);
		List<String> fragments = result.getFragments();
		DigitalObject fragmentDigOb = null;
		Random random = new Random();
		int index = random.nextInt(fragments.size());
		System.err.println("Getting file: " + fragments.get(index));
		fragmentDigOb = DigitalObjectUtils.getFragment(result, fragments.get(index), false);
		printDigOb(fragmentDigOb);
		
	}
	
	@Test
	public void testInsertFragmentIntoZipTypeDigitalObject() {
		printTestTitle("Test insertFragmentIntoZipTypeDigitalObject()");
		DigitalObject result = DigitalObjectUtils.createZipTypeDigitalObject(testFolder, "insertFragmentTest.zip", false, false, true);
		List<String> fragments = result.getFragments();
		DigitalObject insertionResult = null;
		Random random = new Random();
		int index = random.nextInt(fragments.size());
		System.err.println("Getting file: " + fragments.get(index));
		File toInsert = new File("IF/common/src/test/resources/test_zip/images/test_gif/laptop.gif");
		insertionResult = DigitalObjectUtils.insertFragment(result, toInsert, new String("insertedFiles\\images\\" + toInsert.getName()), false);
		printDigOb(insertionResult);
		insertionResult = DigitalObjectUtils.insertFragment(insertionResult, toInsert, new String("insertedFiles\\images\\" + toInsert.getName()), false);
		printDigOb(insertionResult);
	}
	
	@Test
	public void testRemoveFragmentFromZipTypeDigitalObject() {
		printTestTitle("Test removeFragmentFromZipTypeDigitalObject()");
		DigitalObject result = DigitalObjectUtils.createZipTypeDigitalObject(removeZip, "removeFragmentTest.zip", false, false, true);
		printDigOb(result);
		DigitalObject removeResult = DigitalObjectUtils.removeFragment(result, new String("insertedFiles\\images\\laptop.gif"), false);
		DigitalObjectUtils.toFile(removeResult, new File(work_folder, removeResult.getTitle()));
		printDigOb(removeResult);
	}
	
    /**
     * As a helper method for counting the size of the bytestream content has been added, this should be tested.
     * <p/>
     * TODO: Should go into a DigitalObjectUtilsTests class
     */
    @Test
    public void contentSizeCalculation() {
        int size = 23823;
        DigitalObject bytes1 = new DigitalObject.Builder(Content.byValue(new byte[size])).build();
        long bytes = DigitalObjectUtils.getContentSize(bytes1);
        assertEquals("Counted, shallow byte[] size is not correct.", size, bytes);
    }

	
	private void printTestTitle(String title) {
		for(int i=0;i<title.length()+4;i++) {
			System.out.print("*");
		}
		System.out.println();
		System.out.println("* " + title + " *");
		for(int i=0;i<title.length()+4;i++) {
			System.out.print("*");
		}
		System.out.println();
	}
	
	private String tabulator(int level) {
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<=(level*4);i++) {
			buf.append(" ");
		}
		return buf.toString();
	}
	
	private void printFragments(DigitalObject digOb) {
		List<String> fragments = digOb.getFragments();
		int i = 1;
		for (String fragment : fragments) {
			System.out.println(tabulator(1) + i + ") " + fragment);
			i++;
		}
		System.out.println(tabulator(1) + "total count: " + fragments.size());
	}
	
	private void printDigOb(DigitalObject digOb) {
		System.out.println("--------------------------------------");
		System.out.println("Summary DigitalObject: " + digOb.getTitle());
		System.out.println("--------------------------------------");
		if(DigitalObjectUtils.isZipType(digOb)) {
			System.out.println("Contains Fragments: " + digOb.getFragments().size());
			printFragments(digOb);
		}
	}

}
