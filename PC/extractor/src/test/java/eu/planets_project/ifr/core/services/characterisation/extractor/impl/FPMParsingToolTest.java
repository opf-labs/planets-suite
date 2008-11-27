package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.utils.FileUtils;

public class FPMParsingToolTest {
	
	public static List<String> listOfPronomIDs = new ArrayList<String> ();
	public static File formatIDs;
	
	
	@BeforeClass
	public static void setup() {
		File puidFile = new File(System.getenv("FPMTOOL_HOME") + File.separator + "res" + File.separator + "PUIDList.txt");
		formatIDs = new File("PC/extractor/src/resources/fpm_files/" + "formatIDs.txt");
		
		System.setProperty("pserv.test.context", "local");
		
		listOfPronomIDs = new ArrayList <String> ();
		
		String puidListString = FileUtils.readTxtFileIntoString(puidFile);
		StringTokenizer tokenizer = new StringTokenizer(puidListString, ":");
		
		while(tokenizer.hasMoreTokens()) {
			String currentPuid = tokenizer.nextToken() + ":";
			if(!currentPuid.contains("#")) {
				listOfPronomIDs.add(currentPuid);
			}
		}
			
	}

	@Test
	public void testGeneratePropertiesFile() {
		StringBuffer puidBuffer = new StringBuffer();
		int i = 1;
		for (Iterator iterator = listOfPronomIDs.iterator(); iterator.hasNext();) {
			String currentPuid = (String) iterator.next();
			FPMParsingTool.generatePropertiesFile(currentPuid);
			puidBuffer.append(currentPuid.substring(0,currentPuid.length()-1) + "\r\n");
			i++;
		}
		FileUtils.writeStringToFile(puidBuffer.toString(), formatIDs.getAbsolutePath());
	}

}
