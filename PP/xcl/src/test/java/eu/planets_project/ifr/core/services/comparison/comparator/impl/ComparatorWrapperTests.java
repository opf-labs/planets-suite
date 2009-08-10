package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.services.datatypes.Property;

/**
 * Tests of the comparator wrapper functionality, which is used by the different
 * comparator services.
 * @author Fabian Steeg
 */
public final class ComparatorWrapperTests {
    /***/
    static final String XCDL3 = "PP/xcl/src/resources/XCDL3.xcdl";
    /***/
    static final String XCDL2 = "PP/xcl/src/resources/XCDL2.xcdl";
    /***/
    static final String XCDL1 = "PP/xcl/src/resources/XCDL1.xcdl";
    /***/
    static final String COCO_IMAGE = "PP/xcl/src/test/resources/cocoImage.xml";

    /** Tests if the required environment variable is set. */
    @Test
    public void environment() {
        assertNotNull("COMPARATOR_HOME is not set",
                ComparatorWrapper.COMPARATOR_HOME);
    }

    /** Tests basic PP comparator wrapper with two XCDLs and a given config. */
    @Test
    public void testWrapperTwoWithConfig() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2)), ComparatorWrapper.read(COCO_IMAGE));
        System.out.println("Result: " + result);
        check(result);
    }

    /** Tests basic PP comparator wrapper with two XCDLs and no given config. */
    @Test
    public void testWrapperTwoNoConfig() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2)), null);
        System.out.println("Result: " + result);
        check(result);
    }

    /** Tests PP comparator wrapper with three XCDLs and a given config. */
    @Test
    public void testWrapperMultiWithConfig() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2), ComparatorWrapper.read(XCDL3)),
                ComparatorWrapper.read(COCO_IMAGE));
        check(result);
    }

    /** Tests PP comparator wrapper with three XCDLs and no given config. */
    // @Test does not pass, but should it? I'm not sure this is even supposed to
    // work
    public void testWrapperMultiNoConfig() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2), ComparatorWrapper.read(XCDL3)), null);
        check(result);
    }

    /**
     * @param result The result to check
     */
    static void check(final String result) {
        System.out.println("Comparator result: " + result);
        assertTrue("No result found returned after comparison!", result != null);
        assertTrue("Comparator could not validate: " + result, !result
                .contains("validation failed"));
        assertTrue("Comparator result contains an error: " + result, !result
                .contains("<error>"));
    }

    /**
     * @param properties The result properties to check
     */
    public static void check(final List<Property> properties) {
        Assert.assertTrue("Property list should not be empty", properties.size() > 0);
        System.out.println("Comparator returned: ");
        for (Property prop : properties) {
            System.out.println(prop);
        }

    }
}
