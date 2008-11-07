package eu.planets_project.ifr.core.services.validation.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.validate.BasicValidateOneBinary;

/**
 * Local and client tests of the PngCheck functionality.
 * @author Fabian Steeg
 */
public final class PngCheckTests {

    /**
     * Tests PngCheck identification using a local PngCheck instance.
     */
    @Test(expected = IllegalArgumentException.class)
    public void localTests() {
        test(new PngCheck());
    }

    /**
     * Tests PngCheck identification using a PngCheck instance retrieved via the
     * web service (running on localhost).
     */
    @Test(expected = Exception.class)
    /*
     * Depending on the setting, the IllegalArgumentException might be wrapped
     * in a SOAPFaultExcpetion, so we expect an Exception
     */
    public void clientTests() {
        BasicValidateOneBinary pngCheck = ServiceCreator.createTestService(
                BasicValidateOneBinary.QNAME, PngCheck.class,
                "/pserv-pc-pngcheck/PngCheck?wsdl");
        test(pngCheck);
    }

    /**
     * Test a PngCheck instance by trying to validate a valid PNG file and by
     * trying to invalidate a JPG file.
     * @param pngCheck The pngCheck instance to test
     */
    private void test(final BasicValidateOneBinary pngCheck) {
        byte[] inPng = ByteArrayHelper.read(new File(
                "PC/pngcheck/src/resources/planets.png"));
        byte[] inJpg = ByteArrayHelper.read(new File(
                "PC/pngcheck/src/resources/planets.jpg"));
        /* Check with null PRONOM URI, both with PNG and JPG */
        try {
            assertTrue("Valid PNG was not validated;", pngCheck
                    .basicValidateOneBinary(inPng, null));
            assertTrue("Invalid PNG was not invalidated;", !pngCheck
                    .basicValidateOneBinary(inJpg, null));
            /* Check with valid and invalid PRONOM URI */
            assertTrue("Valid PNG was not validated;", pngCheck
                    .basicValidateOneBinary(inPng,
                            new URI("info:pronom/fmt/11")));
            /* This should throw an IllegalArgumentException: */
            assertTrue("Invalid PNG was not invalidated;", !pngCheck
                    .basicValidateOneBinary(inJpg,
                            new URI("info:pronom/fmt/10")));
        } catch (PlanetsException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
