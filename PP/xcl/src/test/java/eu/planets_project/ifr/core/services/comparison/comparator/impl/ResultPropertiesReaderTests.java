package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.datatypes.Property;

/**
 * Tests for the ResultPropertiesReader.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ResultPropertiesReaderTests {
    private static final String OUTPUT = "PP/xcl/src/test/resources/copra.xml";

    /** Test reading XCDL comparator result files. */
    @Test
    public void testProperties() {
        check(new ResultPropertiesReader(new File(OUTPUT)));
    }

    /**
     * @param reader The access to check
     */
    private void check(final ResultPropertiesReader reader) {
        List<List<PropertyComparison>> properties = reader.getProperties();
        Assert.assertTrue("No properties extracted by "
                + reader.getClass().getSimpleName(), properties.size() > 0);
        for (List<PropertyComparison> prop : properties) {
            System.out.println(prop);
        }
    }
}
