package eu.planets_project.ifr.core.services.validation.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;

/**
 * Local and client tests of the PngCheck functionality.
 * 
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
    @Test(expected = SOAPFaultException.class)
    /*
     * The IllegalArgumentException above is wrapped in that SOAPFaultExcpetion
     * when using the web service
     */
    public void clientTests() {
        URL url = null;
        try {
            url = new URL(
            /*
             * "http://localhost:8080/pserv-pc-pngcheck/PngCheck?wsdl" or
             * "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-pngcheck/PngCheck?wsdl"
             */
            "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-pngcheck/PngCheck?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                BasicValidateOneBinary.NAME));
        BasicValidateOneBinary pngCheck = service
                .getPort(BasicValidateOneBinary.class);
        test(pngCheck);
    }

    /**
     * Test a PngCheck instance by trying to validate a valid PNG file and by
     * trying to invalidate a JPG file.
     * 
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
