package eu.planets_project.ifr.core.services.comparison.comparator;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXCDLStrings;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.Comparator;

/**
 * Local and client tests of the Comparator service functionality
 * 
 * @author Fabian Steeg
 */
public class ComparatorTests {

	@Test
	public void environment() {
		assertNotNull("COMPARATOR_HOME is not set", Comparator.COMPARATOR_HOME);
	}

	/**
	 * Tests PP comparator comparison using a local Comparator instance
	 */
	@Test
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		test(new Comparator());
	}

	/**
	 * Tests PP comparator comparison using a Comparator instance retrieved via
	 * the web service running on a remote machine (e.g. the test server at UzK)
	 * or your local machine (see in-line comment)
	 */
	@Test
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service
				.create(
						new URL(
						/*
						 * Alternatives:
						 * "http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/Comparator?wsdl"
						 * "http://localhost:8080/pserv-pp-comparator/Comparator?wsdl"
						 */
						"http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/Comparator?wsdl"),
						new QName(PlanetsServices.NS,
								BasicCompareTwoXCDLStrings.NAME));
		BasicCompareTwoXCDLStrings comparator = service
				.getPort(BasicCompareTwoXCDLStrings.class);
		test(comparator);
	}

	/**
	 * @param comparator
	 *            The comparator instance to test
	 * 
	 */
	private void test(BasicCompareTwoXCDLStrings comparator) {
		String result = comparator.basicCompareTwoXCDLStrings(Comparator
				.read("PP/comparator/src/resources/xcdl1.xml"), Comparator
				.read("PP/comparator/src/resources/xcdl2.xml"));
		System.out.println("Result: " + result);
		assertNotNull("Comparator returned null", result);
	}

}
