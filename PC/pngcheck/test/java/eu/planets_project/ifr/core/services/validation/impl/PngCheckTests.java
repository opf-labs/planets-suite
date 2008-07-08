package eu.planets_project.ifr.core.services.validation.impl;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
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
	@Test
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
		Service service = Service.create(new URL(
		/*
		 * "http://localhost:8080/pserv-pc-pngcheck/PngCheck?wsdl" or
		 * "http://planetarium.hki.uni-koeln.de:8080/pserv-pc-pngcheck/PngCheck?wsdl"
		 */
		"http://localhost:8080/pserv-pc-pngcheck/PngCheck?wsdl"), new QName(
				PlanetsServices.NS, BasicValidateOneBinary.NAME));
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
	 */
	private void test(BasicValidateOneBinary pngCheck) throws PlanetsException,
			IOException {
		byte[] inPng = PngCheck.bytes("PC/pngcheck/src/resources/planets.png");
		byte[] inJpg = PngCheck.bytes("PC/pngcheck/src/resources/planets.jpg");
		assertTrue("Valid PNG was not validated;", pngCheck
				.basicValidateOneBinary(inPng, null));
		assertTrue("Invalid PNG was not invalidated;", !pngCheck
				.basicValidateOneBinary(inJpg, null));

	}

}
