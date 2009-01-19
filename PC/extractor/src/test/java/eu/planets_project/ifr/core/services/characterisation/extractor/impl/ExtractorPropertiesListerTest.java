package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.FileFormatProperties;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.utils.FileUtils;

/**
 * @author Peter Melms
 */
public class ExtractorPropertiesListerTest {

    /**
     * list of pronom ids
     */
    public static List<String> listOfPronomIDs = new ArrayList<String>();
    /**
     * file of format ids
     */
    public static File formatIDs;

    /**
     * setup the test
     */
    @BeforeClass
    public static void setup() {
        File puidFile = new File(System.getenv("EXTRACTOR_HOME")
                + File.separator + "res" + File.separator + "PUIDList.txt");
        formatIDs = new File("PC/extractor/src/resources/fpm_files/"
                + "formatIDs.txt");

        System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost");
        System.setProperty("pserv.test.port", "8080");

        listOfPronomIDs = new ArrayList<String>();

        String puidListString = FileUtils.readTxtFileIntoString(puidFile);
        StringTokenizer tokenizer = new StringTokenizer(puidListString, ":");

        while (tokenizer.hasMoreTokens()) {
            String currentPuid = tokenizer.nextToken();
            if (!currentPuid.contains("#")) {
                listOfPronomIDs.add(currentPuid);
            }
        }

    }

    // @Test
    // public void testGeneratePropertiesFile() throws URISyntaxException {
    // StringBuffer puidBuffer = new StringBuffer();
    // int i = 1;
    // for (Iterator iterator = listOfPronomIDs.iterator(); iterator.hasNext();)
    // {
    // String currentPuid = (String) iterator.next();
    // ExtractorPropertiesLister.generatePropertiesFile(new URI(currentPuid));
    // puidBuffer.append(currentPuid + "\r\n");
    // i++;
    // }
    // FileUtils.writeStringToFile(puidBuffer.toString(),
    // formatIDs.getAbsolutePath());
    // }

    /**
     * @throws URISyntaxException
     */
    @Test
    public void testGetFileFormatProperties() throws URISyntaxException {
        for (String puid : listOfPronomIDs) {
            String currentPuid = "info:pronom://" + puid.replaceAll("_", "/");
            FileFormatProperties propertiesList = ExtractorPropertiesLister
                    .getFileFormatProperties(new URI(currentPuid));
            System.out.println("******************START*****************");
            System.out.println("Properties for PronomID: " + currentPuid);
            System.out.println("****************************************");
            for (FileFormatProperty testOutProp : propertiesList
                    .getProperties()) {
                System.out.println(testOutProp.toString());
            }
            System.out.println("******************END*******************");
        }
    }

}
