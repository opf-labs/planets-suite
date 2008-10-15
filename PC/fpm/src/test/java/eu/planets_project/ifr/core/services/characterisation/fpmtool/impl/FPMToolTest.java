package eu.planets_project.ifr.core.services.characterisation.fpmtool.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.BasicCompareFormatProperties;

public class FPMToolTest {

	@Test
	public void localTests() {
			test(new FPMTool());
	}
	
	
	
	
	@Test
	public void clientTests() {
		try {
			
			Service service = Service
			.create(
					new URL(
					"http://localhost:8080/pserv-pc-fpmtool/FPMTool?wsdl"),
					new QName(PlanetsServices.NS,
							BasicCompareFormatProperties.NAME));
			
			BasicCompareFormatProperties bcfp = service.getPort(BasicCompareFormatProperties.class);
			test(bcfp);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	public void test(BasicCompareFormatProperties fpmt) {

		try {
			String parameters = "fmt_10:fmt_13:";
			String result = null;
			result = fpmt.basicCompareFormatProperties(parameters);
			assertNotNull("response was null", result);
			assertTrue(result.startsWith("<fpmResponse><format puid="));
			System.out.println("XCDL: " + result);
		} catch (PlanetsException e) {

			fail(e.getMessage());
		}
	}

	@Test
	public void testBasicCompareFormatProperties() throws IOException,
			PlanetsException {

		String FPMTOOL_HOME = System.getenv("FPMTOOL_HOME");
		System.out.println(FPMTOOL_HOME);
		System.setProperty("FPMTOOL_HOME", "/home/tk/FPMTool/");
		String parameters = "fmt_10:fmt_13:";
		FPMTool fpmtool = new FPMTool();
		String result = null;
		result = fpmtool.basicCompareFormatProperties(parameters);
		assertNotNull("response was null", result);
		assertTrue(result.startsWith("<fpmResponse><format puid="));
		System.out.println("XCDL: " + result);

	}

}
