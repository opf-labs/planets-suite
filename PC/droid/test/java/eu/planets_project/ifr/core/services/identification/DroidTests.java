package eu.planets_project.ifr.core.services.identification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;

public class DroidTests {

	@Test
	public void localTests() throws FileNotFoundException, IOException,
			Exception {
		TestHelper.testAllFiles(new Droid());
	}

	//@Test Work in progress, not working yet
	public void clientTests() throws FileNotFoundException, IOException,
			Exception {
		Service service = Service.create(new URL(
				"http://Pham.local:8080/pserv-pc-droid/Droid?wsdl"), new QName(
				PlanetsServices.NS, Droid.NAME));
		Droid droid = service.getPort(Droid.class);
		TestHelper.testAllFiles(droid);
	}

}
