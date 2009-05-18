package eu.planets_project.ifr.core.services.validation.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.ServiceCreator.Mode;
import eu.planets_project.services.validate.Validate;

/**
 * Local and client tests of the PngCheck functionality.
 * @author Fabian Steeg
 */
public final class PngCheckTests {

    /**
     * test the describe() method
     */
    @Test
    public void testServiceDescription() {
        PngCheck check = new PngCheck();
        ServiceDescription description = check.describe();
        /* There are 3 Pronom IDs for PNG: */
        Assert.assertEquals(3, description.getInputFormats().size());
        System.out.println(description.toXmlFormatted());
    }

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
        Validate pngCheck = ServiceCreator
                .createTestService(Validate.QNAME, PngCheck.class,
                        "/pserv-pc-pngcheck/PngCheck?wsdl", Mode.SERVER);
        test(pngCheck);
    }

    /**
     * Test a PngCheck instance by trying to validate a valid PNG file and by
     * trying to invalidate a JPG file.
     * @param pngCheck The pngCheck instance to test
     */
    private void test(final Validate pngCheck) {
        try {
            DigitalObject inPng = new DigitalObject.Builder(Content
                    .byReference(new File(
                            "PC/pngcheck/src/resources/planets.png").toURI().toURL()))
                    .build();
            DigitalObject inJpg = new DigitalObject.Builder(Content
                    .byReference(new File(
                            "PC/pngcheck/src/resources/planets.jpg").toURI().toURL()))
                    .build();
            /* Check with null PRONOM URI, both with PNG and JPG */
            assertTrue("Valid PNG was not validated;", pngCheck.validate(inPng,
                    null, null).isValidInRegardToThisFormat() );
            assertTrue("Invalid PNG was not invalidated;", !pngCheck.validate(
                    inJpg, null, null).isValidInRegardToThisFormat() );
            /* Check with valid and invalid PRONOM URI */
            assertTrue("Valid PNG was not validated;", pngCheck.validate(inPng,
                    new URI("info:pronom/fmt/11"), null).isValidInRegardToThisFormat() );
            /* This should throw an IllegalArgumentException: */
            assertTrue("Invalid PNG was not invalidated;", !pngCheck.validate(
                    inJpg, new URI("info:pronom/fmt/10"), null).isValidInRegardToThisFormat() );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
