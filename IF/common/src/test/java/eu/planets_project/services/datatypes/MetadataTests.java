package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for metadata objects.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class MetadataTests {
    private String content = "Some content";
    private URI type = URI.create("http://planets-project.eu");

    /** Test object creation, toString and equality. */
    @Test
    public void equality() {
        Metadata m1 = new Metadata(type, content);
        Metadata m2 = new Metadata(type, content);
        assertEquals("Equal objects are unequal;", m1, m2);
        assertEquals("toString for equal objects are unequal;", m1.toString(),
                m2.toString());
    }

    /** Test object sorting and uniqueness. */
    @Test
    public void sorting() {
        Metadata m1 = new Metadata(type, content + "1");
        Metadata m2 = new Metadata(type, content + "2");
        List<Metadata> list = Arrays.asList(m2, m1);
        Collections.sort(list);
        String message = "Sorting incorrect;";
        assertEquals(message, list.get(0), m1);
        assertEquals(message, list.get(1), m2);
        Set<Metadata> set = new HashSet<Metadata>(Arrays.asList(m1, m1, m1));
        assertEquals("Set of metadata contains duplicates;", 1, set.size());

    }
}
