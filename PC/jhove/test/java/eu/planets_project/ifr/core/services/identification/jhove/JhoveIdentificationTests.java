package eu.planets_project.ifr.core.services.identification.jhove;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;

/**
 * Local and client tests of the JHOVE identification functionality
 * 
 * @author Fabian Steeg
 */
public class JhoveIdentificationTests {

	/**
	 * Tests JHOVE identification using a local JhoveIdentification instance
	 */
	@Test
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		test(new JhoveIdentification());
	}

	/**
	 * Tests JHOVE identification using a JhoveIdentification instance retrieved
	 * via the web service running on localhost
	 */
	@Test
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service
				.create(
						new URL(
								"http://localhost:8080/pserv-pc-jhove/JhoveIdentification?wsdl"),
						new QName(PlanetsServices.NS, IdentifyOneBinary.NAME));
		IdentifyOneBinary jHove = service.getPort(IdentifyOneBinary.class);
		test(jHove);
	}

	/**
	 * Tests a JhoveIdentification instance against the enumerated file types in
	 * FileTypes (testing sample files against their expected PRONOM IDs)
	 * 
	 * @param hove The JhoveIdentification instance to test
	 */
	private void test(IdentifyOneBinary hove) throws FileNotFoundException,
			IOException, Exception {
		/* We check all the enumerated file types: */
		for (FileType type : FileType.values()) {
			System.out.println("Testing " + type);
			/* For each we get the sample file: */
			String location = JhoveIdentification.RESOURCES + type.sample;
			/* And try identifying it: */
			Types result = hove.identifyOneBinary(ByteArrayHelper
					.read(new File(location)));
			assertEquals("Wrong pronom ID;", type.pronom, result.types[0]
					.toString());
		}
	}

}
