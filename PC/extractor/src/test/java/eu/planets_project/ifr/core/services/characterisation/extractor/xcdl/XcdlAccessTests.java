package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccess;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlParser;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.services.datatypes.Property;

/**
 * Tests for the XcdlAccess and implementations.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlAccessTests {
    private static final String XCDL = "PC/extractor/src/java/eu/planets_project" +
    		"/ifr/core/services/characterisation/extractor/xcdl/xcdl.xml";

    @Test
    public void testProperties() {
        check(new XcdlProperties(new File(XCDL)));
    }

    @Test
    public void testParser() {
        check(new XcdlParser(new File(XCDL)));
    }

    @Test
    public void testBoth() {
        /* Both implementations should return identical results: */
        List<Property> p1 = new XcdlProperties(new File(XCDL)).getProperties();
        List<Property> p2 = new XcdlParser(new File(XCDL)).getProperties();
        Assert.assertEquals(p1, p2);
    }

    /**
     * @param xcdlAccess The access to check
     */
    private void check(final XcdlAccess xcdlAccess) {
        List<Property> properties = xcdlAccess.getProperties();
        Assert.assertTrue("No properties extracted by "
                + xcdlAccess.getClass().getSimpleName(), properties.size() > 0);
    }
}
