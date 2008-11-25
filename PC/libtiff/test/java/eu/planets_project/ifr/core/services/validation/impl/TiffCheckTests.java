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
 * Local and client tests of the LibTiffCheck functionality.
 * @author Klaus Rechert
 */
public final class TiffCheckTests 
{
	/**
	 * Tests TiffValidation identification using a local TiffValidation instance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void localTests() {
		test(new TiffValidation());
	}

	/**
	 * Tests TiffCheck identification using a TiffCheck instance retrieved via the
	 * web service (running on localhost).
	 */
	@Test(expected = Exception.class)
	/*
	 * Depending on the setting, the IllegalArgumentException might be wrapped
	 * in a SOAPFaultExcpetion, so we expect an Exception
	 */
	public void clientTests() 
	{
		BasicValidateOneBinary tiffCheck = ServiceCreator.createTestService(
			BasicValidateOneBinary.QNAME, TiffValidation.class,
			"/pserv-pc-libtiff/TiffValidation?wsdl");
		test(tiffCheck);
	}

	/**
	 * Test a TiffCheck instance by trying to validate a valid TIFF file and by
	 * trying to invalidate a PNG file.
	 * @param tiffCheck The tiffCheck instance to test
	 */
	@Test(expected = AssertionError.class)
	private void test(final BasicValidateOneBinary tiffCheck) 
	{
		byte[] inPng = ByteArrayHelper.read(
			new File("PC/libtiff/src/resources/image01.png"));
		byte[] inTiff = ByteArrayHelper.read(
			new File("PC/libtiff/src/resources/image01.tif"));

		boolean result;
		/* Check with null PRONOM URI, both with PNG and TIFF */
		try {
			result = tiffCheck.basicValidateOneBinary(inTiff, null);
			// assertTrue("Valid TIFF was not validated;", result);

			result = !tiffCheck.basicValidateOneBinary(inPng, null);
			// assertTrue("Invalid TIFF was not invalidated;", result);


			/* Check with valid and invalid PRONOM URI */
			URI uri = new URI("info:pronom/fmt/7");
			result = tiffCheck.basicValidateOneBinary(inTiff, uri);
			// assertTrue("Valid TIFF with URI was not validated;", result);

			/* This should throw an IllegalArgumentException: */
			uri = new URI("info:pronom/fmt/11");
			result = !tiffCheck.basicValidateOneBinary(inTiff, uri);
			// assertTrue("Valid TIFF with invalid URI not invalidated;", result);

		} catch (PlanetsException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
