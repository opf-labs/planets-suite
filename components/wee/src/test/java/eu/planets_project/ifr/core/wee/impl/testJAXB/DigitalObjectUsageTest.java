package eu.planets_project.ifr.core.wee.impl.testJAXB;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.ByteArrayHelper;

public class DigitalObjectUsageTest {
	
	private static final String LOCATION = "D:/Implementation/SVN_Planets/pserv/trunk/IF/common/src/test/resources/sample_content.txt";
    private URL url;
    private byte[] bytes;

    /** Creates the content value and reference. */
    @Before
    public void setup() {
        /* For a test file, we create the actual value and a reference: */
        java.io.File file = new java.io.File(LOCATION);
        bytes = ByteArrayHelper.read(file);
        try {
            url = file.toURL();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }
    
    /** Tests reading by reference and by value. */
    @Test
    public void reading() {
        /*
         * We create a content by reference and a content by value for the same
         * content (on the disk):
         */
        Content reference = Content.byReference(url);
        Content value = Content.byValue(bytes);
        System.out.println("Created: " + reference);
        System.out.println("Created: " + value);
        /* Then, we read both contents: */
        String readReference = read(reference.read());
        String readValue = read(value.read());
        /* These should be identical: */
        System.out.println(String.format(
                "Read by value: '%s', by reference: '%s'", readValue,
                readReference));
        assertEquals(
                "Reading by reference and reading by value return different results;",
                readValue, readReference);
    }
    
    /**
     * Creating DigitalObject Simple sample usage (only required values).
     */
    @Test
    public void creatingDigitalObject() throws MalformedURLException {
        /* A simple example with only required values: */
        URL id = new URL("http://someDataRegistryPointer");
        
        /* using the Builder in short */
        /*DigitalObject o = new DigitalObject.Builder(id, 
        		Content.byReference(url), 
        		Content.byValue(bytes)
        	).build();
        */
        
        /* adding objects incrementally */
        DigitalObject oo;
        Content c = Content.byValue(bytes);
        //OR BY REFERENCE - but only one Content allowed for a DigitalObject
        //Content c2 = Content.byReference(url);
        DigitalObject.Builder builder = new DigitalObject.Builder(c);
        builder.permanentUrl(id);
        builder.checksum(new Checksum("MD5","FDSFDSFSD"));
        //etc.
        oo = builder.build();
        
        System.out.println(oo.toXml());
        
        /*
         * These objects can be serialized to XML and instantiated from that
         * form:
         */
        assertEquals(oo, DigitalObject.of(oo.toXml()));
        
        String xml = oo.toXml();
        System.out.println(xml);
        DigitalObject o2 = DigitalObject.of(xml);
        assertEquals("Compare rountrip expected 0",0 ,o2.compareTo(oo));
        assertEquals(true,o2.equals(oo));
        assertEquals("Checksum","MD5",o2.getChecksum().getAlgorithm());

    }
    
    /**
     * @param source The source to read from
     * @return Returns the content of the source
     */
    private String read(final InputStream source) {
        StringBuilder builder = new StringBuilder();
        Scanner s = new Scanner(source);
        while (s.hasNextLine()) {
            builder.append(s.nextLine());
        }
        return builder.toString();
    }

}
