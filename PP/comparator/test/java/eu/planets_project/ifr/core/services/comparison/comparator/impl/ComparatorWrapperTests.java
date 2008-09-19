package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Tests of the comparator wrapper functionality, which is used by the different
 * comparator services.
 * @author Fabian Steeg
 */
public final class ComparatorWrapperTests {
    /***/
    static final String XCDL3 = "PP/comparator/src/resources/COMPARATOR_HOME/XCDL3.xcdl";
    /***/
    static final String XCDL2 = "PP/comparator/src/resources/COMPARATOR_HOME/XCDL2.xcdl";
    /***/
    static final String XCDL1 = "PP/comparator/src/resources/COMPARATOR_HOME/XCDL1.xcdl";
    /***/
    static final String PCR_SINGLE = "PP/comparator/src/resources/COMPARATOR_HOME/defaultPCR.xml";
    /***/
    static final String PCR_MULTI = "PP/comparator/src/resources/COMPARATOR_HOME/defaultPCRMulti.xml";

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
                        .read(XCDL2)), ComparatorWrapper.read(PCR_SINGLE));
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
                ComparatorWrapper.read(PCR_MULTI));
        System.out.println("Result: " + result);
        check(result);
    }

    /** Tests PP comparator wrapper with three XCDLs and no given config. */
    @Test
    public void testWrapperMultiNoConfig() {
        String result = ComparatorWrapper.compare(
                ComparatorWrapper.read(XCDL1), Arrays.asList(ComparatorWrapper
                        .read(XCDL2), ComparatorWrapper.read(XCDL3)), null);
        System.out.println("Result: " + result);
        check(result);
    }

    /**
     * @param result The result to check
     */
    private void check(final String result) {
        assertNotNull("Comparator returned null", result);
        assertTrue("Comparator could not validate: " + result, !result
                .contains("validation failed"));
    }
}
