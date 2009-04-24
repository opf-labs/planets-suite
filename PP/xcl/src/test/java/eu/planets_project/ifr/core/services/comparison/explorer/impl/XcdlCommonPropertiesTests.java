package eu.planets_project.ifr.core.services.comparison.explorer.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.compare.CommonProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the XCDL common properties service.
 * @see XcdlCommonProperties
 */
public class XcdlCommonPropertiesTests {

    private static final String WSDL = "/pserv-xcl/XcdlCommonProperties?wsdl";
    
    @Test
	public void testDescribe() {
		CommonProperties c = ServiceCreator.createTestService(XcdlCommonProperties.QNAME,
				XcdlCommonProperties.class, WSDL);
		ServiceDescription sd = c.describe();
        assertTrue("The ServiceDescription should not be NULL.", sd != null);
        System.out.println("test: describe()");
        System.out
                .println("--------------------------------------------------------------------");
        System.out.println();
        System.out.println("Received ServiceDescription from: "
                + c.getClass().getName());
        System.out.println(sd.toXmlFormatted());
        System.out
                .println("--------------------------------------------------------------------");
    }

    @Test
    public void testBmpGif() {
        testFor("BMP", "GIF");
    }

    @Test
    public void testBmpTif() {
        testFor("BMP", "TIF");
    }

    @Test
    public void testPngTif() {
        testFor("PNG", "TIF");
    }

    @Test
    public void testPngTifJpg() {
        testFor("PNG", "TIF", "JPG");
    }

    @Test
    public void testPngTifJpgGif() {
        testFor("PNG", "TIF", "JPG", "GIF");
    }

    /**
     * @param suffixes The suffixes of the file formats to test
     */
    private void testFor(final String... suffixes) {
        CommonProperties commonProperties = ServiceCreator.createTestService(
                CommonProperties.QNAME, XcdlCommonProperties.class,
                WSDL);
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        List<URI> puids = new ArrayList<URI>();
        for (String suffix : suffixes) {
            puids.addAll(registry.getUrisForExtension(suffix));
        }
        System.out.println("PUIDS: " + puids);
        testIntersection(commonProperties, puids);
        testUnion(commonProperties, puids);
    }

    private void testUnion(CommonProperties commonProperties, List<URI> puids) {
        CompareResult union = commonProperties.union(puids);
        commonCheck(union, union.getProperties());
        List<Property> properties = commonProperties.union(
                Arrays.asList(puids.get(0))).getProperties();
        Assert.assertTrue("Less union properties than properties of one only",
                union.getProperties().size() >= properties.size());
    }

    private void testIntersection(CommonProperties commonProperties,
            List<URI> puids) {
        CompareResult intersection = commonProperties.intersection(puids);
        commonCheck(intersection, intersection.getProperties());
        Assert.assertTrue(
                "More intersection properties than properties of one only",
                intersection.getProperties().size() <= commonProperties
                        .intersection(Arrays.asList(puids.get(0)))
                        .getProperties().size());
    }

    private void commonCheck(final CompareResult compareResult,
            final List<Property> list) {
        assertNotNull("response was null", list);
        String info = compareResult.getReport().getInfo();
        assertTrue("Result contains an error: " + info, !info.contains("Error"));
        assertTrue("Wrong result: " + info, info
                .startsWith("<fpmResponse><format puid="));
        assertTrue("No result found: " + info, !info.contains("unavailable"));
        printInfo(list, info);
    }

    /**
     * @param list The properties
     * @param info The raw result
     */
    private void printInfo(final List<Property> list, final String info) {
        // System.out.println("FPM raw result: " + info);
        System.out.println("FPM props result: ");
        for (Property prop : list) {
            System.out.println(prop);
        }
        System.out.println(list.size() + " common properties");
    }
}
