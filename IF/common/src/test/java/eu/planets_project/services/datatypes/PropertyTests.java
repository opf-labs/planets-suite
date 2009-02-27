package eu.planets_project.services.datatypes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link #Property} class.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class PropertyTests {
    private static URI uri1 = null;
    private static URI uri2 = null;
    static {
        try {
            uri1 = new URI("planets:uri/1");
            uri2 = new URI("planets:uri/2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    private final Property p1 = new Property(uri1, "name1", "value1");
    private final Property p2 = new Property(uri2, "name2", "value2");

    @Test
    public void testToString() {
        Assert.assertTrue(p1.toString().contains(p1.getName())
                && p1.toString().contains(p1.getValue()));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(p1, new Property(p1.getUri(),p1.getName(), p1.getValue()));
    }

    @Test
    public void testHashCode() {
        Set<Property> props = new HashSet<Property>(Arrays.asList(p1, p1, p2,
                p2));
        Assert.assertEquals("Set contains duplicate entries", 2, props.size());
    }
}
