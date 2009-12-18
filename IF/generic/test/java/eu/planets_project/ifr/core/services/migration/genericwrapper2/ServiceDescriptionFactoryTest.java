/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class ServiceDescriptionFactoryTest {

    private ServiceDescriptionFactory serviceDescriptionFactory;
    private static final String TOOL_ID = "DummyTestID";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	final DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");

	serviceDescriptionFactory = new ServiceDescriptionFactory(TOOL_ID,
		documentLocator.getDocument());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.ServiceDescriptionFactory#getServiceDescription(org.w3c.dom.Document, java.util.List, java.lang.String)}
     * .
     */
    @Test
    public void testGetServiceDescription() throws Exception {
	final ServiceDescription serviceDescription = serviceDescriptionFactory
		.getServiceDescription();

	assertNotNull(serviceDescription);
	assertEquals("Un-expected author (creator) information.",
		"\"Easter Bunny <easter.bunny@bunny.net>\"", serviceDescription
			.getAuthor());

	final String expectedDescription = "Example description of a service "
		+ "wrapping of a fantastic\n\t\t\tcommand line tool for migrating an "
		+ "egg to eggnog.";
	assertEquals("Un-expected description.", expectedDescription,
		serviceDescription.getDescription());

	// assertEquals("Un-expected end-point.","",
	// serviceDescription.getEndpoint());

	final URI expectedFurtherInfoURI = new URI("http://example.org");
	assertEquals("Un-expected text returned by getFurtherInfo().",
		expectedFurtherInfoURI, serviceDescription.getFurtherInfo());

	assertEquals("Un-expected identifier.", "Example_custom_identifier",
		serviceDescription.getIdentifier());

	final Set<URI> expectedInputFormats = new HashSet<URI>();
	expectedInputFormats.add(new URI("info:pronom/x-fmt/408"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/lowercase"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/407"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/406"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/foo"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/91"));
	
	assertEquals("Un-expected input formats.", expectedInputFormats,
		new HashSet<URI>(serviceDescription.getInputFormats()));

	assertEquals("Un-expected instructions.",
		"Example: Please install the XYZ tool on the system to\n\t\t\t"
			+ "make this service work.", serviceDescription
			.getInstructions());

	assertEquals("Un-expected logo URI.",
		"http://www.planets-project.eu/graphics/Planets_Logo.png",
		serviceDescription.getLogo().toString());

	assertEquals("Un-expected service name.",
		"Example: Eggnog migration service.", serviceDescription
			.getName());

	/* TODO: Yet to be finished....
	System.out.println("@%^@^%@%^ GNARF>>> "
		+ serviceDescription.getParameters());

	assertEquals("Un-expected parameter list.", "", serviceDescription
		.getParameters());
	assertEquals("Un-expected property list.", "", serviceDescription
		.getProperties());
	assertEquals("Un-expected service provider information.", "",
		serviceDescription.getServiceProvider());
	assertEquals("Un-expected tool description.", "", serviceDescription
		.getTool());

	assertEquals("Un-expected service interface type.", "",
		serviceDescription.getType());
		*/

	assertEquals("Un-expected service version.", "3.141592653589793",
		serviceDescription.getVersion());
    }
}
