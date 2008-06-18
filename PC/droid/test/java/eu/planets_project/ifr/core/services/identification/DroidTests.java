package eu.planets_project.ifr.core.services.identification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;
import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;

/**
 * Local and client tests of the Droid functionality.
 * 
 * @author Fabian Steeg
 */
public class DroidTests {

	@Test
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		TestHelper.testAllFiles(new Droid());
	}

	@Test
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service.create(new URL(
				"http://localhost:8080/pserv-pc-droid/Droid?wsdl"), new QName(
				PlanetsServices.NS, IdentifyOneBinary.NAME));
		IdentifyOneBinary droid = service.getPort(IdentifyOneBinary.class);
		TestHelper.testAllFiles(droid);
	}

}
