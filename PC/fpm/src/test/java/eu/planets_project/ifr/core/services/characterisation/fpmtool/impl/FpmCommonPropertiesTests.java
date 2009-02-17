package eu.planets_project.ifr.core.services.characterisation.fpmtool.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.compare.CommonProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Prop;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the FPM common properties service.
 * @see FpmCommonProperties
 */
public class FpmCommonPropertiesTests {

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
                CommonProperties.QNAME, FpmCommonProperties.class,
                "/pserv-pc-fpmtool/FpmCommonProperties?wsdl");
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        List<URI> puids = new ArrayList<URI>();
        for (String suffix : suffixes) {
            puids.addAll(registry.getURIsForExtension(suffix));
        }
        System.out.println("PUIDS: " + puids);
        CompareResult compareResult = commonProperties.of(puids);
        List<Prop> list = compareResult.getProperties();
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
    private void printInfo(final List<Prop> list, final String info) {
        // System.out.println("FPM raw result: " + info);
        System.out.println("FPM props result: ");
        for (Prop prop : list) {
            System.out.println(prop);
        }
        System.out.println(list.size() + " common properties");
    }
}
