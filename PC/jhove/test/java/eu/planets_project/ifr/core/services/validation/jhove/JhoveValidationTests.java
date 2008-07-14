package eu.planets_project.ifr.core.services.validation.jhove;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.ifr.core.services.validation.jhove.impl.JhoveValidation;

/**
 * Local and client tests of the JHOVE validation functionality
 * 
 * @author Fabian Steeg
 */
public class JhoveValidationTests {

	/**
	 * Tests JHOVE validation using a local JhoveValidation instance
	 */
	@Test
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		System.out.println("Local:");
		test(new JhoveValidation());
	}

	/**
	 * Tests JHOVE validation using a JhoveValidation instance retrieved via the
	 * web service running on localhost
	 */
	@Test
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service.create(new URL(
				"http://localhost:8080/pserv-pc-jhove/JhoveValidation?wsdl"),
				new QName(PlanetsServices.NS, BasicValidateOneBinary.NAME));
		BasicValidateOneBinary jHove = service
				.getPort(BasicValidateOneBinary.class);
		System.out.println("Remote:");
		test(jHove);
	}

	/**
	 * Tests a JhoveValidation instance against the enumerated file types in
	 * FileTypes (testing sample files against their expected PRONOM IDs)
	 * 
	 * @param validation The JhoveValidation instance to test
	 */
	private void test(BasicValidateOneBinary validation)
			throws FileNotFoundException, IOException, Exception {
		/* We check all the enumerated file types: */
		for (FileType type : FileType.values()) {
			System.out.println("Testing validation of: " + type);
			/* For each we get the sample file: */
			String location = JhoveIdentification.RESOURCES + type.sample;
			/* And try validating it: */
			boolean result = validation.basicValidateOneBinary(ByteArrayHelper
					.read(new File(location)), new URI(type.pronom));
			assertTrue("Not validated: " + type, result);
		}
	}

}
