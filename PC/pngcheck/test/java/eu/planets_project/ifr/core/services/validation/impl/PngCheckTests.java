package eu.planets_project.ifr.core.services.validation.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;

/**
 * Local and client tests of the PngCheck functionality
 * 
 * @author Fabian Steeg
 */
public class PngCheckTests {

	/**
	 * Tests PngCheck identification using a local PngCheck instance
	 */
	@Test(expected = IllegalArgumentException.class)
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		test(new PngCheck());
	}

	/**
	 * Tests PngCheck identification using a PngCheck instance retrieved via the
	 * web service (running on localhost)
	 */
	@Test
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service
				.create(
						new URL(
						/*
						 * "http://localhost:8080/pserv-pc-pngcheck/PngCheck?wsdl"
						 * or
						 * "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-pngcheck/PngCheck?wsdl"
						 */
						"http://planetarium.hki.uni-koeln.de:8080/pserv-pc-pngcheck/PngCheck?wsdl"),
						new QName(PlanetsServices.NS,
								BasicValidateOneBinary.NAME));
		BasicValidateOneBinary pngCheck = service
				.getPort(BasicValidateOneBinary.class);
		test(pngCheck);
	}

	/**
	 * Test a PngCheck instance by trying to validate a valid PNG file and by
	 * trying to invalidate a JPG file
	 * 
	 * @param pngCheck The pngCheck instance to test
	 * @throws PlanetsException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void test(BasicValidateOneBinary pngCheck) throws PlanetsException,
			IOException, URISyntaxException {
		byte[] inPng = ByteArrayHelper.read(new File(
				"PC/pngcheck/src/resources/planets.png"));
		byte[] inJpg = ByteArrayHelper.read(new File(
				"PC/pngcheck/src/resources/planets.jpg"));
		/* Check with null PRONOM URI, both with PNG and JPG */
		assertTrue("Valid PNG was not validated;", pngCheck
				.basicValidateOneBinary(inPng, null));
		assertTrue("Invalid PNG was not invalidated;", !pngCheck
				.basicValidateOneBinary(inJpg, null));
		/* Check with valid and invalid PRONOM URI */
		assertTrue("Valid PNG was not validated;", pngCheck
				.basicValidateOneBinary(inPng, new URI("info:pronom/fmt/11")));
		/* This should throw an IllegalArgumentException: */
		assertTrue("Invalid PNG was not invalidated;", !pngCheck
				.basicValidateOneBinary(inJpg, new URI("info:pronom/fmt/10")));

	}

}
