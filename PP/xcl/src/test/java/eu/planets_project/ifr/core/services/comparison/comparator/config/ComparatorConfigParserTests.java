package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParser;
import eu.planets_project.services.datatypes.Prop;

/**
 * Tests for the {@link ComparatorConfigParser} and implementations.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ComparatorConfigParserTests {
    private static final String CPR = "PP/xcl/src/java/eu/planets_project/"
            + "ifr/core/services/comparison/comparator/config/samplePCR.xml";

    @Test
    public void testParser() {
        check(new ComparatorConfigParser(new File(CPR)));
    }

    /**
     * @param parser The parser to check
     */
    private void check(final ComparatorConfigParser parser) {
        List<Prop<Object>> properties = parser.getProperties();
        Assert.assertTrue("No properties extracted by "
                + parser.getClass().getSimpleName(), properties.size() > 0);
        for (Prop prop : properties) {
            Assert.assertNotNull(String.format("Prop %s has no type", prop),
                    prop.getType());
            System.out.println(prop);
        }
    }
}
