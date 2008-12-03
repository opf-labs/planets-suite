package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.FileFormatProperties;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.utils.FileUtils;

public class ExtractorPropertiesListerTest {
	
	public static List<String> listOfPronomIDs = new ArrayList<String> ();
	public static File formatIDs;
	
	
	@BeforeClass
	public static void setup() {
		File puidFile = new File(System.getenv("EXTRACTOR_HOME") + File.separator + "res" + File.separator + "PUIDList.txt");
		formatIDs = new File("PC/extractor/src/resources/fpm_files/" + "formatIDs.txt");
		
		System.setProperty("pserv.test.context", "local");
		
		listOfPronomIDs = new ArrayList <String> ();
		
		String puidListString = FileUtils.readTxtFileIntoString(puidFile);
		StringTokenizer tokenizer = new StringTokenizer(puidListString, ":");
		
		while(tokenizer.hasMoreTokens()) {
			String currentPuid = tokenizer.nextToken();
			if(!currentPuid.contains("#")) {
				listOfPronomIDs.add(currentPuid);
			}
		}
			
	}

//	@Test
//	public void testGeneratePropertiesFile() throws URISyntaxException {
//		StringBuffer puidBuffer = new StringBuffer();
//		int i = 1;
//		for (Iterator iterator = listOfPronomIDs.iterator(); iterator.hasNext();) {
//			String currentPuid = (String) iterator.next();
//			ExtractorPropertiesLister.generatePropertiesFile(new URI(currentPuid));
//			puidBuffer.append(currentPuid + "\r\n");
//			i++;
//		}
//		FileUtils.writeStringToFile(puidBuffer.toString(), formatIDs.getAbsolutePath());
//	}
	
	@Test
	public void testGetFileFormatProperties() throws URISyntaxException {
		int i = 1;
		for (Iterator iterator = listOfPronomIDs.iterator(); iterator.hasNext();) {
			String currentPuid = "info:pronom://" + (String) iterator.next();
			FileFormatProperties propertiesList = ExtractorPropertiesLister.getFileFormatProperties(new URI(currentPuid));
			i++;
			
			System.out.println("******************START*****************");
			System.out.println("Properties for PronomID: " + currentPuid);
			System.out.println("****************************************");
			for (Iterator iterator1 = propertiesList.getProperties().iterator(); iterator1.hasNext();) {
				FileFormatProperty testOutProp = (FileFormatProperty) iterator1.next();
				System.out.println(testOutProp.toString());
			}
			System.out.println("******************END*******************");
		}
	}

}
