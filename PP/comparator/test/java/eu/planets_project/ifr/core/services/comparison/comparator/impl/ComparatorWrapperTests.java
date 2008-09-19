package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests of the comparator wrapper functionality, which is used by the different
 * comparator services.
 * @author Fabian Steeg
 */
public final class ComparatorWrapperTests {

    /***/
    static final String XCDL2 = "PP/comparator/src/resources/xcdl2.xml";
    /***/
    static final String XCDL1 = "PP/comparator/src/resources/xcdl1.xml";
    /***/
    static final String PCR = "PP/comparator/src/resources/PCR.xml";

    /** Tests if the required environment variable is set. */
    @Test
    public void environment() {
        assertNotNull("COMPARATOR_HOME is not set",
                ComparatorWrapper.COMPARATOR_HOME);
    }

    /**
     * Tests PP comparator comparison using the comparator wrapper utility
     * class.
     */
    @Test
    public void testWrapper() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2)), ComparatorWrapper.read(PCR));
        System.out.println("Result: " + result);
        assertNotNull("Comparator returned null", result);
        result = ComparatorWrapper.compare(ComparatorWrapper.read(XCDL1),
                Arrays.asList(ComparatorWrapper.read(XCDL2)), null);
        System.out.println("Result: " + result);
        assertNotNull("Comparator returned null", result);
    }
}
