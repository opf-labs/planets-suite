package eu.planets_project.services.datatypes;

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

import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Tests for Content objects. Reads the same data using Content objects both by
 * value and by reference, checking for equality of the results.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * 
 */
public final class ContentTests {
    private static final String LOCATION = "IF/common/src/test/resources/sample_content.txt";
    private URL url;
    private byte[] bytes;

    /** Creates the content value and reference. */
    @Before
    public void setup() {
        /* For a test file, we create the actual value and a reference: */
        java.io.File file = new java.io.File(LOCATION);
        bytes = ByteArrayHelper.read(file);
        try {
            url = file.toURI().toURL();
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

    /** Equality tests for content objects. */
    @Test
    public void equality() {
        Content c1 = Content.byReference(url);
        Content c2 = Content.byReference(url);
        assertEquals("Equal object don't equal;", c1, c2);
        assertEquals("Equal objects have different string representations;", c1
                .toString(), c2.toString());
        Set<Content> set = new HashSet<Content>(Arrays.asList(c1, c1, c1));
        assertEquals("Set contains duplicates;", 1, set.size());

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
