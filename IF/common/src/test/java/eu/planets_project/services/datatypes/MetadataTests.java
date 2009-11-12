package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.net.URI;
import java.util.*;

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

    /** Test object uniqueness. */
    @Test
    public void uniqueness() {
        Metadata m1 = new Metadata(type, content + "1");
        Set<Metadata> set = new HashSet<Metadata>(Arrays.asList(m1, m1, m1));
        assertEquals("Set of metadata contains duplicates;", 1, set.size());
    }
}
